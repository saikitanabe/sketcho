package net.sevenscales.editor.silver;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.gfx.domain.IShape;

import com.google.gwt.core.client.JavaScriptObject;

public class SilverUtils {

	public static final String Canvas = "Canvas";
	public static final String TextBlock = "TextBlock";
	public static final String RESIZE = "crosshair";
	public static final String DEFAULT = "default"; 

	public static native void setLocation(JavaScriptObject element, int left, int top)/*-{
		element.setShape( {x:left, y:top} )
	}-*/;

	public static native double textActualWidth(JavaScriptObject text)/*-{
		return text.rawNode.ActualWidth;
	}-*/;

	 public static native double textActualHeight(JavaScriptObject text)/*-{
     return text.rawNode.ActualHeight;
   }-*/;

	public static native int getZIndex(JavaScriptObject element)/*-{
		return element.rawNode["Canvas.ZIndex"];
	}-*/;

	public static native void setZIndex(JavaScriptObject text, int zindex)/*-{
		element.rawNode["Canvas.ZIndex"] = zindex;
	}-*/;

	public static native void applyTransform(JavaScriptObject element, int x, int y)/*-{
		element.applyTransform($wnd.dojox.gfx.matrix.translate(x, y));
	}-*/;
	
	public static native int getTransformX(JavaScriptObject element)/*-{
	  if (element.getTransform() == null) {
	    return 0;
	  }
	  return parseInt(element.getTransform().dx);
	}-*/;
	
	public static native int getTransformY(JavaScriptObject element)/*-{
    if (element.getTransform() == null) {
      return 0;
    }
    return parseInt(element.getTransform().dy);
	}-*/;
	
	public static native void applyRotate(JavaScriptObject element, int centerX, int centerY, double angle)/*-{
    element.applyTransform({dx: centerX, dy: centerY, angle: angle});
	}-*/;

	public static void setStroke(JavaScriptObject element, String color) {
	  if (element != null) {
	    setStrokeImpl(element, color);
	  }
	}

	private static native void setStrokeImpl(JavaScriptObject element, String color)/*-{
		element.setStroke({color:color});
	}-*/;

	public static native String getText(JavaScriptObject element)/*-{
		return element.rawNode.text;
	}-*/;

	public static native void setText(JavaScriptObject element, String text)/*-{
		element.setShape( {text:text} );
	}-*/;

	public static native JavaScriptObject createCanvas(JavaScriptObject parentCanvas, int left, int top, int height, int width)/*-{
		return parentCanvas.createRect(
			{x:left, y:top, height:height, width:width})
    		.setStroke( {color:'black', width:1} )
    		.setFill("yellow")
	}-*/;

	public static native JavaScriptObject createRect(JavaScriptObject parentCanvas, int left, int top, int height, int width)/*-{
		return parentCanvas.createRect(
			{x:left, y:top, height:height, width:width})
			.setStroke( {color:'black', width:1} )
			.setFill("green")
	}-*/;

	public static native void setBackground(JavaScriptObject element, String color)/*-{
		element.setBackground(color);
	}-*/;

	public static String createCanvasXamlWithObjects(String[] items) {
		String result = "<Canvas Width='10' Height='100'>";
		for (int i = 0; i < items.length; ++i) {
			result += "<" + items[i] + " Text='holli' Width='10' Height='10' />";
		}
		result += "</Canvas>";
		return result;
	}

	public static native JavaScriptObject createFromXaml(JavaScriptObject surface, String xaml)/*-{
		var canvas = surface.plugin.content.createFromXaml(xaml, true);
		var result = new $wnd.Canvas();
		result.rawNode = canvas;
		result.plugin = canvas.getHost();
		$wnd.debugLog(result.rawNode);
		surface.canvas.children.add(result.rawNode);
		return result;
	}-*/;

	public static void setRectClip(JavaScriptObject element, Rect rect) {
		String r = rect.x + "," + rect.y + "," + rect.width + "," + rect.height;
		setClip(element, "RectangleGeometry", r);
	}
	
	private static native void setClip(JavaScriptObject element, String geometry, String rect)/*-{
		element.setClip( {geometry:geometry, rect:rect} );
	}-*/;
	
  public static native JavaScriptObject createEllipse
    (JavaScriptObject surface, int left, int top, int width, int height)/*-{
    var ellipse = { left: left, top: top, width: width, height: height };
    return surface.createEllipse(ellipse).setStroke( {color:'black', width:1} ).
      setFill( {color:'HoneyDew'} );
  }-*/;

  public static native JavaScriptObject createCircle
    (JavaScriptObject surface, int circleX, int circleY, int radius)/*-{
    var circleInfo = { cx: circleX, cy: circleY, r: radius };
    return surface
    	.createCircle(circleInfo)
    	.setStroke( {color:'black', width:1} )
    	.setFill( [255, 255, 255, 0.1] );
  }-*/;

	 public static native JavaScriptObject createTextBlock(JavaScriptObject surface)/*-{
		return surface.createText()
			 .setFont({family: "Arial", size: "12px", weight: "bold"} )
			 .setFill("black");
//			 .setStroke("red");
//     return element.createTextBlock({left:0, top:0, height:0, width: 0});
   }-*/;

	public static native JavaScriptObject createTextBlock
		(JavaScriptObject surface, int left, int top, int height, int width)/*-{
		return surface.createText( {x:left, y:top})
			 .setFont({family: "Arial", size: "12px", weight: "bold"} )
			 .setFill("black");
//			 .setStroke("red");
	}-*/;
	
  public static native JavaScriptObject createDashedLine(JavaScriptObject canvas, int x1, int y1,
    int x2, int y2)/*-{
    var lineInfo = {x1: x1, y1: y1, x2: x2, y2: y2};
    var strokeInfo = {color: "black", width: 1, dashed:"6 3"};
    return canvas.createLine(lineInfo).setStroke(strokeInfo);
  }-*/;
	
  
  public static native void setDashedLineShape(JavaScriptObject line, int x1, int y1, int x2,
      int y2, String color)/*-{ 
      line.setShape( {x1:x1, y1:y1, x2:x2, y2:y2} ).
         setStroke({color: color, style:"ShortDash"});
         //: dashed:"6 3"});
    }-*/;


	public static native void setCursor(String cursorType)/*-{
		$doc.body.style.cursor = cursorType;
	}-*/;

	public static native void captureMouse(JavaScriptObject element)/*-{
		element.rawNode.captureMouse();
	}-*/;

	public static void setShape(JavaScriptObject element, RectShape rectShape) {
		setShape(element, rectShape.left, rectShape.top, rectShape.width, rectShape.height);
	}
	
  public static native void setCircleShape
    (JavaScriptObject element, int circleX, int circleY, int radius)/*-{
    element.setShape({ cx: circleX, cy: circleY, r: radius } )
           .setStroke( {color:'black', width:1} )
           .setFill( {color:'white', opacity:0.1} );
  }-*/;

  private static native void setEllipseShape
  (JavaScriptObject element, int left, int top, int width, int height)/*-{
    element.setShape( {cx:left, cy:top, width:width, height:height} );
  }-*/;
  

  public static void setShape(JavaScriptObject element, String text, RectShape rectShape) {
    setShape(element, text, rectShape.left, rectShape.top, rectShape.width, rectShape.height);
  }

	public static native void setShape
		(JavaScriptObject element, int left, int top, int width, int height)/*-{
		element.setShape( {x:left, y:top, width:width, height:height} );
	}-*/;

	 public static native void setShape(
	     JavaScriptObject element, String text, int left, int top, int width, int height)/*-{
     element.setShape( {text:text, left:left, top:top, width:width, height:height} );
   }-*/;

	public static native int getLeft(JavaScriptObject element)/*-{
		return element.rawNode["Canvas.Left"];
	}-*/;

	public static native int getTop(JavaScriptObject element)/*-{
		return element.rawNode["Canvas.Top"];
	}-*/;

	public static native int getWidth(JavaScriptObject element)/*-{
		return element.rawNode.width;
	}-*/;

	public static native int getHeight(JavaScriptObject element)/*-{
		return element.rawNode.height;
	}-*/;

	public static native void resetRenderTransform(JavaScriptObject element)/*-{
		var tf = element.getTransform();
		if (tf != null) {
//		$wnd.debugLog("dx:"+tf.dx+"dy"+tf.dy);
//		var s = element.getShape();
//		if (s && tf) {
//			element.setShape({x: s.x + tf.dx, y: s.y + tf.dy});
//		}
			tf.dx = 0;
			tf.dy = 0;
			element.setTransform(tf);
		}
	}-*/;

  public static native void setVisibility(JavaScriptObject element, boolean visible)/*-{
    if (!visible) {
      element._fill = element.getFill();
      element._stroke = element.getStroke();
 	  element.setFill(null).setStroke(null);
    } else {
      if (element._fill)
      	element.setFill(element._fill);
      if (element._stroke)
      	element.setStroke(element._stroke);
    }
  }-*/;

  public static native void setColor(JavaScriptObject element, String color)/*-{
    element.setStroke({color:color});
  }-*/;
	
	public static void add(JavaScriptObject surface, Diagram diagram) {
		for (IShape e : diagram.getElements()) {
			add(surface, e.getRawNode());
		}
	}

	public static native void add(JavaScriptObject surface, JavaScriptObject item)/*-{
//		surface.
//		surface.canvas.children.add(item.rawNode);
	}-*/;

	public static native int getX(JavaScriptObject object)/*-{
		var shape = object.getShape();
		return shape.x;
	}-*/;

	public static native int getY(JavaScriptObject object)/*-{
		var shape = object.getShape();
		return shape.y;
	}-*/;
  
}
