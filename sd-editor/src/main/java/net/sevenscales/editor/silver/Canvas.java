package net.sevenscales.editor.silver;

import java.util.ArrayList;

import com.google.gwt.user.client.Element;

public class Canvas {
	private ArrayList eventListeners;
	private Element plugin;
	private Element canvas;
/*
	public Canvas () {
//		this.name = "Surface";
		this.evenListeners = new ArrayList();


	private void createObject(Class shapeType, rawShape) {
			var newShapeObject = new shapeType(this.plugin);
			var xaml = '<' + newShapeObject.getNodeType() + ' Name="' + 
						getUniqueId(newShapeObject.getNodeType()) + '" />';
			debugLog('createObject:' + xaml);
			var node = this.plugin.content.createFromXaml(xaml, true);
			newShapeObject.setRawNode(node);
			newShapeObject.setShape(rawShape);
			this.canvas.children.add(newShapeObject.rawNode);
			return newShapeObject;
		}
	}

	function createSurface(parentNode, width, height) {

		var pluginName = getUniqueId('silverplugin');
		debugLog(pluginName);

		var result = new Surface();
		result.pluginName = pluginName;
		Silverlight.createObject(
	        "myxaml.xaml",                     // Source property value.
	        parentNode,                     // DOM reference to hosting DIV tag.
	        pluginName,         		    // Unique plug-in ID value.
			{                               // Per-instance properties.
				width:String(width),                // Width of rectangular region of 
				                            // plug-in area in pixels.
				height:String(height),               // Height of rectangular region of 
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
	*/

}
