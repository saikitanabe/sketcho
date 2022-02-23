package net.sevenscales.editor.gfx.dojosvg;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IText;
import net.sevenscales.editor.gfx.domain.Promise;
import net.sevenscales.editor.gfx.domain.ElementSize;

class TextForeignObject extends Shape implements IText {
  public static final String ALIGN_LEFT = "start";
  public static final String ALIGN_CENTER = "middle";
  public static final String ALIGN_RIGHT = "end";
  
  public static final String WEIGHT_BOLD = "Bold";
  public static final String WEIGHT_NORMAL = "Normal";
  
	final String svgNS = "http://www.w3.org/2000/svg";
  private Element prevTspanElement;
	private boolean prevTspanWeight;
	private double currentLineLength;
	private boolean startsNewline;
	private boolean tspanMode = false;
	private boolean baselineBottom = false;
  
	TextForeignObject(IContainer container, boolean baselineBottom) {
		this.baselineBottom = baselineBottom;
    rawNode = createText(svgNS, container.getContainer());
		setFontWeight(WEIGHT_NORMAL);
	}
	
 @Override
	public int getX() {
		return getX(rawNode);
	}
	public native int getX(JavaScriptObject rawNode)/*-{
    var rect = rawNode.getBoundingClientRect();
		return parseInt(rect.left);
	}-*/;

 @Override
	public int getY() {
		return getY(rawNode);
	}
	public native int getY(JavaScriptObject rawNode)/*-{
    var rect = rawNode.getBoundingClientRect();
		return parseInt(rect.top);
	}-*/;
	
	@Override
	public String getFontSize() {
    return "12px";
		// return _getFontSize(rawNode);
	}
	private native String _getFontSize(JavaScriptObject rawNode)/*-{
		return rawNode.getFont().size;
	}-*/;
	
	@Override
	public void setFontSize(String fontSize) {
		_setFontSize(rawNode, fontSize);
	}
	private native void _setFontSize(JavaScriptObject rawNode, String fontSize)/*-{
    rawNode.setFontSize(fontSize);
	}-*/;
	
  @Override
  public String getFontWeight() {
    return WEIGHT_NORMAL;
    // return nativeGetFontWeight(rawNode);
  }
  private native String nativeGetFontWeight(JavaScriptObject text)/*-{
    return text.getFont().weight;
  }-*/;
  
 @Override
	public void setFontWeight(String weight) {
	  // nativeSetFontWeight(rawNode, weight);
	}
	private native void nativeSetFontWeight(JavaScriptObject text, String weight)/*-{
		//  	rawNode.setFont({family: family, size: "14px", weight: "bold"});
		var f = text.getFont();
		if (f == null) {
	  	text.setFont({family: "sans-serif", weight: weight});
		} else {
			f.weight = weight;
			text.setFont(f);
		}
	}-*/;
	
 @Override
	public void setAlignment(String alignment) {
	  // setAlignment(rawNode, alignment);
	}
	private native void setAlignment(JavaScriptObject rawNode, String alignment)/*-{
	  rawNode.setShape( {align:alignment} );
	}-*/;
	
  @Override
  public String getAlignment() {
    // return nativeGetAlignment(rawNode);
    return ALIGN_LEFT;
  }
  private native String nativeGetAlignment(JavaScriptObject rawNode)/*-{
    return rawNode.getShape().align;
  }-*/;

  @Override
	protected native void nativeSetFill(
			JavaScriptObject rawNode,
      int red,
      int green, 
      int blue,
      double opacity
  )/*-{
		// var color = new $wnd.dojo.Color( {r:red,g:green,b:blue,a:opacity} );
		// rawNode.setFill(color);
	}-*/;  

  @Override
  protected native void setAttribute(
    JavaScriptObject rawNode,
    String name,
    String value
  )/*-{
		// rawNode.rawNode.setAttribute(name, value);
	}-*/;

  @Override
  public void setTextTspanAlignCenter() {
		// setAttribute("class", "svg-txt-center");
  }
  @Override  
  public void setTextTspanAlignRight() {
		// setAttribute("class", "svg-txt-right");
  }

 @Override
	public void setText(String text) {
		setText(rawNode, text);
	}
	public native void setText(JavaScriptObject rawNode, String text)/*-{
    rawNode.setText(text);
	}-*/;
	
 @Override
	public String getText() {
	  return nativeGetText(rawNode);
	}
	private native String nativeGetText(JavaScriptObject rawNode)/*-{
    return rawNode.getText();
	}-*/;
	
 @Override
	public void setShape(int x, int y) {
		// TODO if created as tspan child nodes => update x attribute to all that have x attribute!!
		// if (justTextNoTspan(rawNode)) {
			// setShape(rawNode, x, y);
		// } else {
			// updateTspanX(x);
		// }
	}
	public native void setShape(JavaScriptObject rawNode, int x, int y)/*-{
		rawNode.setShape( {x:x, y:y} );
	}-*/;

	protected native void nativeSetStroke(
		JavaScriptObject rawNode,
    int red,
    int green,
    int blue,
    double opacity
  )/*-{
	  rawNode.setStroke({r:red, g:green, b:blue, a: opacity});
	}-*/;  

	@Override
	public void updateTspanX(int x) {
		// Element raw = getRawNode(rawNode);
		// for (int count = 0; count < raw.getChildCount(); ++count) {
		// 	Node node = raw.getChild(count);
		// 	if (node.getNodeName().equals("tspan")) { //   tag.tagName
		// 		JsArray<Node> attributes = getAttributes(node);
		// 		if (attributes != null) {
		// 			String attrsStr = "";
		// 			for (int i = 0; i < attributes.length(); ++i) {
		// 				Node attr = attributes.get(i);
		// 				String attributeName = attr.getNodeName();
		// 				String attributeValue = attr.getNodeValue();

		// 				if ("x".equals(attributeName)) {
		// 					attr.setNodeValue(String.valueOf(x));
		// 				}
		// 			}
		// 		}
		// 	}
		// }
	}
	
	private native JavaScriptObject createText(
    String ns,
    JavaScriptObject surface
  )/*-{
    return $wnd.createTextForeignObject(surface);
	}-*/;

//  @Override
// 	public double getTextWidth() {
// 		// if ((UiUtils.isSafari() || UiUtils.isFirefox() || UiUtils.isIE()) && !tspanMode) {
// 		// 	int fontSize = parseFontSize(getFontSize());
// 		// 	return MeasurementPanel.getOffsetWidth(getText(), fontSize);
// 		// }
// 		return getTextWidth(rawNode);
// 	}

  @Override
  // public void getTextSize(Promise.FunctionParam<ElementSize> promise) {
  //   _getTextSize(rawNode).then(promise);
  // }
  public Promise getTextSize() {
    return _getTextSize(rawNode);
  }
  private native Promise _getTextSize(
    JavaScriptObject rawNode
  )/*-{
    return rawNode.getTextSize();
  }-*/;

	private int parseFontSize(String fontSize) {
		int result = 12;
		if (fontSize.length() - 2 > 0) {
			try {
				return Integer.valueOf(fontSize.substring(0, fontSize.length() - 2));
			} catch (NumberFormatException e) {
				// return default value
			}
		}
		return result;
	}
	
 @Override
	public double getLastSpanWidth() {
		return _lastSpanWidth(rawNode);
	}
	
	private native double _lastSpanWidth(JavaScriptObject text)/*-{
		return text.rawNode.lastChild.getComputedTextLength();
	}-*/;

	// public native double getTextWidth(JavaScriptObject rawNode)/*-{
  //   return rawNode.getTextWidth();
	// }-*/;
	
//  @Override
// 	public double getTextHeight() {
// 		// return 0;
// 	  return getTextHeight(rawNode);
// 	}

	// private native double getTextHeight(JavaScriptObject rawNode)/*-{
  //   return rawNode.getTextHeight();
	// }-*/;
	  
  @Override
  public void applyTransformToShape(int dx, int dy) {
		setShape(getX() + dx, getY() + dy);
  }
  
  @Override
  public void setFontFamily(String family) {
  	// _setFontFamily(rawNode, family);
  }
  private native void _setFontFamily(JavaScriptObject rawNode, String family)/*-{
  	var f = rawNode.getFont();
  	if (f != null) {
  		f.family = family;
  		rawNode.setFont(f);
  	} else {
  		rawNode.setFont({family: family});
  	}
  }-*/;
  
  @Override
  public String getFontFamily() {
  	// return _getFontFamily(rawNode);
    return "";
  }
  private native String _getFontFamily(JavaScriptObject rawNode)/*-{
  	return rawNode.getFont().family;
  }-*/;
  
  @Override
  public void addText(
    JavaScriptObject tokens,
    int x,
    int width,
    boolean textAlignCenter,
    boolean textAlignRight
  ) {
	  // Element r = getRawNode(rawNode);

	  // if (textAlignCenter) {
	  // 	x += width / 2;
	  // } else if (textAlignRight) {
	  // 	x += width;
    // }

	  // addText(rawNode, tokens, x, width, baselineBottom, UiUtils.isFirefox(), UiUtils.isIE());
	  // tspanMode = true;
  }
  
  private native void addText(
    JavaScriptObject parent,
    JavaScriptObject tokens,
    int x,
    int width,
    boolean baselineBottom,
    boolean firefox,
    boolean ie
  )/*-{
  	var computeLength = null
		var svgText = $wnd.svgTextArea(parent, x, width, baselineBottom, computeLength, firefox, ie);
    var elements = svgText.addTokens(tokens);
  }-*/;
  
  private double getComputedTextLength(Element tspanElement) {
  	if (!tspanElement.getFirstChild().getNodeValue().equals("")) {
  		return _getComputedTextLength(tspanElement);
  	}
  	return 0;
  }
  
  private native double _getComputedTextLength(Element tspanElement)/*-{
  	return tspanElement.getComputedTextLength();
  }-*/;
  
  private native Element getRawNode(JavaScriptObject rawNode)/*-{
  	return rawNode.rawNode;
  }-*/;
    
  @Override
  public String getChildElements(int dx) {
  	String children = getChildElementsEscaped(dx);
  	return children;
  }
  
	private String getChildElementsEscaped(int dx) {
		Element raw = getRawNode(rawNode);
		return formatNodes(raw, dx);
	}

	private String formatNodes(Node raw, int dx) {
		String result = "";
		// for (int count = 0; count < raw.getChildCount(); ++count) {
		// 	Node node = raw.getChild(count);
		// 	// recursive node formatting until a text node
		// 	if (node.getNodeType() == 3) {
		// 		result += SafeHtmlUtils.htmlEscape(node.getNodeValue());
		// 	} else {
		// 		JsArray<Node> attributes = getAttributes(node);
		// 		String attrsStr = "";
		// 		if (attributes != null) {
		// 			for (int i = 0; i < attributes.length(); ++i) {
		// 				Node attr = attributes.get(i);
		// 				String attributeName = attr.getNodeName();
		// 				String attributeValue = attr.getNodeValue();

		// 				// TODO text should be relative to group and moved along with the group
		// 				// then no extra calculation would not be needed
		// 				if ("x".equals(attributeName)) {
		// 					attributeValue = String.valueOf(Integer.valueOf(attributeValue) + dx);
		// 				}

		// 				if (attributeName.equals("xlink:href") && 
		// 					 !attributeValue.startsWith("http://") &&
		// 					 !attributeValue.startsWith("https://") &&
		// 					 !attributeValue.startsWith("mailto:")) {
		// 					attributeValue = "";
		// 				}
		// 				attrsStr += attributeName + "='" + attributeValue + "' ";
		// 			}
		// 		}

		// 		result += '<' + node.getNodeName() + ' ' + attrsStr +'>' + 
		// 							formatNodes(node, dx) + 
		// 							"</" + node.getNodeName() + '>';
		// 	}
		// }
		return result;
	}

	public static native JsArray<Node> getAttributes(Node elem) /*-{
	  return elem.attributes;
	}-*/;

  public void setShapeSize(int width, int height) {
    _setShapeSize(rawNode, width, height);
  }

  private native void _setShapeSize(
    JavaScriptObject rawNode,
    int width,
    int height
  )/*-{
    rawNode.setShapeSize(width, height);
  }-*/;

  public void initializeText(String text) {
    _initializeText(rawNode, text);
  }
  private native void _initializeText(
    JavaScriptObject rawNode,
    String text
  )/*-{
    rawNode.initializeText(text)
  }-*/;

  public void setHorizontal(boolean horizontal) {
    _setHorizontal(rawNode, horizontal);
  }

  private native void _setHorizontal(
    JavaScriptObject rawNode,
    boolean horizontal
  )/*-{
    rawNode.setHorizontal(horizontal);
  }-*/;

  public void setColor(Color color) {
    _setColor(rawNode, color.red, color.green, color.blue, color.opacity);
  }
  private native void _setColor(
    JavaScriptObject rawNode,
    int red,
    int green,
    int blue,
    double opacity
  )/*-{
    rawNode.setColor({
      r: red,
      g: green,
      b: blue,
      a: opacity
    });
  }-*/;

  public void setBorderColor(Color color) {
    _setBorderColor(rawNode, color.red, color.green, color.blue, color.opacity);
  }
  private native void _setBorderColor(
    JavaScriptObject rawNode,
    int red,
    int green,
    int blue,
    double opacity
  )/*-{
    rawNode.setBorderColor({
      r: red,
      g: green,
      b: blue,
      a: opacity
    });
  }-*/;

  public void setShapeProperties(int properties, String parentType, boolean awesome) {
    _setShapeProperties(rawNode, properties, parentType, awesome);
  }  
  private native void _setShapeProperties(
    JavaScriptObject rawNode,
    int properties,
    String parentType,
    boolean awesome
  )/*-{
    rawNode.setShapeProperties({
      properties: properties,
      parentType: parentType,
      awesome: awesome,
    });
  }-*/;

  @Override
  public void setRotate(int degrees) {
    _setRotate(rawNode, degrees);
  }
  private native void _setRotate(
    JavaScriptObject rawNode,
    int degrees
  )/*-{
    rawNode.setRotate(degrees);
  }-*/;

}
