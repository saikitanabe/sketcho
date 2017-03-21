package net.sevenscales.editor.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import net.sevenscales.domain.js.JsShape;

public class SurfaceDefs {
	public static native void addToDefs(JavaScriptObject surface, JsArray<JsShape> icons)/*-{
		function _createElementNS(ns, nodeType){
			// summary:
			//		Internal helper to deal with creating elements that
			//		are namespaced.  Mainly to get SVG markup output
			//		working on IE.
			if($doc.createElementNS){
				return $doc.createElementNS(ns,nodeType);
			}else{
				return $doc.createElement(nodeType);
			}
		}
		
		// function _setAttributeNS(node, ns, attr, value){
		// 	if(node.setAttributeNS){
		// 		return node.setAttributeNS(ns, attr, value);
		// 	}else{
		// 		return node.setAttribute(attr, value);
		// 	}
		// }

		var svgns = "http://www.w3.org/2000/svg"

		for (var i = 0; i < icons.length; ++i) {
			var icon = icons[i]

			var group = _createElementNS(svgns, 'g')
			group.setAttribute('id', 'linkshape')
			group.setAttribute('class', "svglink")

			var rect = _createElementNS(svgns, 'rect')
			rect.setAttribute('width', icon.w)
			rect.setAttribute('height', icon.h)
			rect.setAttribute('x', -1)
			rect.setAttribute('y', -1)
			rect.setAttribute('rx', 9)
			rect.setAttribute('style', 'fill:#fff;stroke:none;')
			group.appendChild(rect)

			for (var x = 0; x < icon.s.length; ++x) {
				var path = _createElementNS(svgns, 'path')
				path.setAttribute('d', icon.s[x].p)
				path.setAttribute('style', icon.s[x].s)
				group.appendChild(path)
			}
		}

		surface.defNode.appendChild(group)
	}-*/;
}