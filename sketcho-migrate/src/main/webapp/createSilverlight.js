///////////////////////////////////////////////////////////////////
function debugLog(text) {
	var e = document.getElementById("debug_holder");
	if (e) {
		e.appendChild(document.createTextNode(text + " "));
	}
}

function onError(msg, href, lineNo) {
    debugLog(msg + " " + href + " " + lineNo);
}

window.onerror = onError;

///////////////////////////////////////////////////////////////////
var __uniqueId = 0;

function getUniqueId(nameBase) {
	if (!nameBase) {
		nameBase = "silverunique"; 
	}
	return nameBase + __uniqueId++;
}

///////////////////////////////////////////////////////////////////
function Shape(plugin) {
	this.plugin = null;
	
	this.addEventListener = function(eventType, handlerFunc) {
		this.rawNode.addEventListener(eventType, handlerFunc);
	}

	this.setRawNode = function(rawNode) {
		this.rawNode = rawNode;
		return this;
	}
	
	this.setClip = function(clipInfo) {
		var geometry = this.plugin.content.createFromXaml('<RectangleGeometry />');
		geometry.rect = clipInfo.rect;
		this.rawNode.clip = geometry;
		return this;
	}
	
	this.setShape = function(rawShape) {
		return null; // force to implement this method
	}
	
	this.getShape = function() {
		return this.rawShape;
	}
	
	this.setStroke = function(strokeInfo) {
		var brush = this.plugin.content.createFromXaml('<SolidColorBrush />');
		brush.color = strokeInfo.color;
		this.rawNode.Stroke = brush;
		if (strokeInfo.width) {
			this.rawNode.strokeThickness = strokeInfo.width;
		}
		if (strokeInfo.dashed) {
			this.rawNode.StrokeDashArray = strokeInfo.dashed;
		}

		return this;
	}
	
	this.setBackground = function(color) {
		var brush = this.plugin.content.createFromXaml("<SolidColorBrush />");
		brush.color = color;
		this.rawNode.background = brush;
	}
	
	this.setFill = function(fill) {
		var brush = this.plugin.content.createFromXaml("<SolidColorBrush />");
		var colorValue = fill.color;
		if (fill instanceof Array) {
			// "sc#0.5,0,0,1"
			colorValue = "sc#";// + fill[fill.length - 1] + ","; // add alpha as first
			for(var index=0; index<fill.length - 1; index++) {
				colorValue += fill[index]
				if (index < fill.length - 2) {
					colorValue += ',';
				}
			}
		}
		brush.color = colorValue;
		brush.opacity = fill.opacity ? fill.opacity : 1;
		this.rawNode.fill = brush;
		return this;
	}
	
	this.applyTransform = function(transformationInfo) {
		var transformation = null;
		if (transformationInfo.angle) {
			transformation = this.plugin.content.createFromXaml("<RotateTransform />");
			transformation.centerX = transformationInfo.dx;
			transformation.centerY = transformationInfo.dy;
			transformation.angle = transformationInfo.angle;
		} else {
			transformation = this.plugin.content.createFromXaml("<TranslateTransform />");
			transformation.x = transformationInfo.dx;
			transformation.y = transformationInfo.dy;
		}
		this.rawNode.RenderTransform = transformation;
		return this;
	}
	
	this.getTransform = function() {
		var t = this.rawNode.RenderTransform;
		var result = {dx: 0, dy: 0};
		if (t) {
			result = {dx: t.x, dy: t.y};
		}
		return result;
	}
	
	this.getNodeType = function() {
		return this.nodeType;
	}
	
}

Circle.prototype = new Shape();
function Circle(plugin) {
	this.plugin = plugin;
	this.nodeType = "Ellipse";
	
	this.setShape = function(rawShape) {
		this.rawShape = rawShape;
		this.rawNode.height = rawShape.r * 2;
		this.rawNode.width = rawShape.r * 2;
		this.rawNode["Canvas.Left"] = rawShape.cx - rawShape.r;
		this.rawNode["Canvas.Top"] = rawShape.cy - rawShape.r;
		return this;
	}
}

Ellipse.prototype = new Shape();
function Ellipse(plugin) {
	this.plugin = plugin;
	this.nodeType = "Ellipse";
	
	this.setShape = function(rawShape) {
		this.rawShape = rawShape;
		this.rawNode.width = rawShape.width;
		this.rawNode.height = rawShape.height;
		this.rawNode["Canvas.Left"] = rawShape.left;
		this.rawNode["Canvas.Top"] = rawShape.top;
		return this;
	}
}

Rectangle.prototype = new Shape();
function Rectangle(plugin) {
	this.plugin = plugin;
	this.nodeType = "Rectangle";
	
	this.setShape = function(rawShape) {
		this.rawShape = rawShape;
		this.rawNode["Canvas.Left"] = rawShape.left ? rawShape.left : 0;
		this.rawNode["Canvas.Top"] = rawShape.top ? rawShape.top : 0;
		this.rawNode.Height = rawShape.height;
		this.rawNode.Width = rawShape.width;
		return this;
	}
}

Canvas.prototype = new Shape();
function Canvas(plugin) {
	this.plugin = plugin;
	this.canvas = null;
	this.nodeType = "Canvas";
	extend(this, canvasMethods);
	
	this.setShape = function(rawShape) {
		this.rawShape = rawShape;
		this.rawNode["Canvas.Left"] = rawShape.left ? rawShape.left : 0;
		this.rawNode["Canvas.Top"] = rawShape.top ? rawShape.top : 0;
		this.rawNode.Height = rawShape.height;
		this.rawNode.Width = rawShape.width;
		return this;
	}
}

TextBlock.prototype = new Shape();
function TextBlock(plugin) {
	this.plugin = plugin;
	this.nodeType = "TextBlock";
	
	this.setShape = function(rawShape) {
		this.rawShape = rawShape;
		this.rawNode.text = rawShape.text ? rawShape.text : "";
		this.rawNode["Canvas.Left"] = rawShape.left;
		this.rawNode["Canvas.Top"] = rawShape.top;
		return this;
	}
}

Line.prototype = new Shape();
function Line(plugin) {
	this.nodeType = "Line";
	this.plugin = plugin;
	
	this.setShape = function(newShape) {
		this.rawNode.x1 = newShape.x1;
		this.rawNode.y1 = newShape.y1;
		this.rawNode.x2 = newShape.x2;
		this.rawNode.y2 = newShape.y2;
		return this;
	}
}

Polyline.prototype = new Shape();
function Polyline(plugin) {
	this.nodeType = "Polyline";
	this.plugin = plugin;
	
	this.setShape = function(newShape) {
		var result = "";
		// construct shape params 10,10 22,22 ... 
		for(var index = 0; index < newShape.length; index++) {
			result += newShape[index].x + ',' + newShape[index].y + ' ';
		}
		this.rawNode.points = result;
		return this;
	}
}

Image.prototype = new Shape();
function Image(plugin) {
	this.nodeType = "Image";
	this.plugin = plugin;

	this.setShape = function(newShape) {
		this.rawNode["Canvas.Left"] = newShape.left;
		this.rawNode["Canvas.Top"] = newShape.top;
		this.rawNode.source = newShape.source;
		return this;
	}
}

///////////////////////////////////////////////////////////////////

function helloworld() {
	debugLog("helloworld");
}

delegator = function(instance, method, arguments) {
	return function() {
		return method.apply(instance, arguments);
	}
}

extend = function(subclass, baseclass) {
	for (var i in baseclass) {
		subclass[i] = baseclass[i];
	}
}

var canvasMethods = {
	remove: function(diagram) {
		this.canvas.children.remove(diagram.rawNode);
	},
	createLine: function(lineInfo) {
		return this.createObject(Line, lineInfo);
	},
	createPolyline: function(points) {
		return this.createObject(Polyline, points);	// dojox.gfx.Polyline
	},
	createCircle: function(circle) {
		return this.createObject(Circle, circle);
	},	
	createEllipse: function(ellipse) {
		return this.createObject(Ellipse, ellipse);
	},
	createRectangle: function(rectInfo) {
		return this.createObject(Rectangle, rectInfo);
	},	
	createTextBlock: function(textInfo) {
		return this.createObject(TextBlock, textInfo);
	},	
	createCanvas: function(canvasInfo) {
		return this.createObject(Canvas, canvasInfo);
	},
	createImage: function(imageInfo) {
		return this.createObject(Image, imageInfo);
	},
	createObject: function(shapeType, rawShape) {
		var newShapeObject = new shapeType(this.plugin);
		var xaml = '<' + newShapeObject.getNodeType() + ' Name="' + 
					getUniqueId(newShapeObject.getNodeType()) + '" />';
//		debugLog('createObject:' + xaml);
		var node = this.plugin.content.createFromXaml(xaml, true);
		newShapeObject.setRawNode(node);
		newShapeObject.setShape(rawShape);
//		this.canvas.children.add(newShapeObject.rawNode);
		return newShapeObject;
	}
}

//extend(Canvas, canvasMethods);
//extend(Surface, canvasMethods);

function Surface() {
	this.name = "Surface";
	this.evenListeners = [];

	this.handleMouseDown = function(sender,args) {
		this.handleEvent(sender, args, "MouseLeftButtonDown");
	}

	this.handleMouseUp = function(sender,args) {
		this.handleEvent(sender, args, "MouseLeftButtonUp");
	}

	this.handleMouseMove = function(sender,args) {
		this.handleEvent(sender, args, "MouseMove");
	}
	
	this.keyDown = function(sender,args) {
		this.handleEvent(sender,args,"KeyDown");
	}
	
	this.keyUp = function(sender,args) {
		this.handleEvent(sender,args,"KeyUp");
	}

	this.handleEvent = function(sender, args, type) {
		for(var index=0; index < this.evenListeners.length; index++) {
			if (this.evenListeners[index].type == type) {
				this.evenListeners[index].handler(sender,args);
			}
		}
	}
	
	Surface.onLoaded = function(plgn, eventArgs, sender) {
		var surface = eventArgs.object; 
		extend(surface, canvasMethods);

		surface.canvas = sender;
		surface.rawNode = sender;
		surface.plugin = plgn;
		surface.height = eventArgs.height;
		surface.width = eventArgs.width;

//		debugLog("onLoaded end: " + surface.plugin + surface.canvas);
		surface.rect = surface.createRectangle
			({height: surface.height, width: surface.width}).setFill({color:'transparent'});

		surface.canvas.children.add(surface.rect.rawNode);

		surface.canvas.AddEventListener("MouseLeftButtonDown", delegator
			(surface, surface.handleMouseDown));
		surface.canvas.AddEventListener("MouseLeftButtonUp", delegator
			(surface, surface.handleMouseUp));
		surface.canvas.AddEventListener("MouseMove", delegator
			(surface, surface.handleMouseMove));

		surface.canvas.AddEventListener("KeyDown", delegator
			(surface, surface.keyDown));
		
		surface.canvas.AddEventListener("KeyUp", delegator
			(surface, surface.keyUp));

		// notify that canvas is loaded
		surface.handleEvent(surface.canvas, null, "OnLoaded");
	}

	this.addEventListener = function(eventType, handlerFunc) {
		this.evenListeners.push({type:eventType, handler:handlerFunc});
	}	
}

function createSurface(parentNode, width, height) {
	/*
	var t = parentNode.ownerDocument.createElement("script");
	t.type = "text/xaml";
	t.id = getUniqueId();
	t.text = "<Canvas xmlns='http://schemas.microsoft.com/client/2007' />";
	document.body.appendChild(t);
	*/
	
	var pluginName = getUniqueId('silverplugin');
//	debugLog(pluginName);

	var result = new Surface();
	result.pluginName = pluginName;
	Silverlight.createObject(
        "myxaml.xaml",                     // Source property value.
        parentNode,                     // DOM reference to hosting DIV tag.
        pluginName,         		    // Unique plug-in ID value.
		{                               // Per-instance properties.
			width:String(width),                // Width of rectangular region of 
			                            // plug-in area in pixels.
			height:String(height),      // Height of rectangular region of 
			                            // plug-in area in pixels.
			inplaceInstallPrompt:false, // Determines whether to display 
			                            // in-place install prompt if 
			                            // invalid version detected.
			background:"transparent",       // Background color of plug-in.
			isWindowless:'true',        // Determines whether to display plug-in 
			                            // in Windowless mode.
			framerate:'24',             // MaxFrameRate property value.
			version:'1.0'               // Silverlight version to use.
		},
        {
            onError:null,               // OnError property value -- 
                                        // event handler function name.
            onLoad:Surface.onLoaded      		// OnLoad property value -- 
                                        // event handler function name.
		}, null, {object:result, height:height, width:width}); // provide initial parameters

	return result;
}
