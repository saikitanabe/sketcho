package net.sevenscales.editor.gfx.dojosvg;

import com.google.gwt.core.client.JavaScriptObject;

class ShapeUtils {
	static native void _rotate2(
    JavaScriptObject rawNode, 
    int degree, 
    int a, 
    int b
  )/*-{
    var rotate = 'rotate(' + degree + ' ' + a + ' ' + b + ')'

    var t = rawNode.rawNode.getAttribute('transform')
    if (!t) {
      t = ''
    } else {
      var reg = /rotate\(\d+\s[-]*\d+\s[-]*\d+\)/i
      t = t.replace(reg, '').trim()
      t = t + ' '
    }

    if (degree == 0) {
      // clear rotate
      rotate = ''
      t = t.trim()
    }
    t = t + rotate
    rawNode.rawNode.setAttribute('transform', t)
	}-*/;

}