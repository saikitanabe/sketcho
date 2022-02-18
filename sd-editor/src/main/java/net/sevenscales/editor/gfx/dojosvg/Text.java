package net.sevenscales.editor.gfx.dojosvg;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IText;
import net.sevenscales.editor.gfx.domain.Color;


class Text extends Shape implements IText {
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
  
	Text(IContainer container, boolean baselineBottom) {
		this.baselineBottom = baselineBottom;
    rawNode = createText(container.getContainer());
		setFontWeight(WEIGHT_NORMAL);
	}
	
 @Override
	public int getX() {
		return getX(rawNode);
	}
	public native int getX(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().x);
	}-*/;

 @Override
	public int getY() {
		return getY(rawNode);
	}
	public native int getY(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().y);
	}-*/;
	
	@Override
	public String getFontSize() {
		return _getFontSize(rawNode);
	}
	private native String _getFontSize(JavaScriptObject rawNode)/*-{
		return rawNode.getFont().size;
	}-*/;
	
	@Override
	public void setFontSize(String fontSize) {
		_setFontSize(rawNode, fontSize);
	}
	private native void _setFontSize(JavaScriptObject rawNode, String fontSize)/*-{
		var f = rawNode.getFont()
		f.size = fontSize
		rawNode.setFont(f)
	}-*/;
	
  @Override
  public String getFontWeight() {
    return nativeGetFontWeight(rawNode);
  }
  private native String nativeGetFontWeight(JavaScriptObject text)/*-{
    return text.getFont().weight;
  }-*/;
  
 @Override
	public void setFontWeight(String weight) {
	  nativeSetFontWeight(rawNode, weight);
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
	  setAlignment(rawNode, alignment);
	}
	private native void setAlignment(JavaScriptObject rawNode, String alignment)/*-{
	  rawNode.setShape( {align:alignment} );
	}-*/;
	
  @Override
  public String getAlignment() {
    return nativeGetAlignment(rawNode);
  }
  private native String nativeGetAlignment(JavaScriptObject rawNode)/*-{
    return rawNode.getShape().align;
  }-*/;

  @Override
  public void setTextTspanAlignCenter() {
		setAttribute("class", "svg-txt-center");
  }
  @Override  
  public void setTextTspanAlignRight() {
		setAttribute("class", "svg-txt-right");
  }

 @Override
	public void setText(String text) {
		setText(rawNode, text);
	}
	public native void setText(JavaScriptObject rawNode, String text)/*-{
		rawNode.setShape( {text:text} );
	}-*/;
	
 @Override
	public String getText() {
	  return nativeGetText(rawNode);
	}
	private native String nativeGetText(JavaScriptObject rawNode)/*-{
	  return rawNode.getShape().text;
	}-*/;
	
 @Override
	public void setShape(int x, int y) {
		// TODO if created as tspan child nodes => update x attribute to all that have x attribute!!
		// if (justTextNoTspan(rawNode)) {
			setShape(rawNode, x, y);
		// } else {
			// updateTspanX(x);
		// }
	}
	public native void setShape(JavaScriptObject rawNode, int x, int y)/*-{
		rawNode.setShape( {x:x, y:y} );
	}-*/;

	@Override
	public void updateTspanX(int x) {
		Element raw = getRawNode(rawNode);
		for (int count = 0; count < raw.getChildCount(); ++count) {
			Node node = raw.getChild(count);
			if (node.getNodeName().equals("tspan")) { //   tag.tagName
				JsArray<Node> attributes = getAttributes(node);
				if (attributes != null) {
					String attrsStr = "";
					for (int i = 0; i < attributes.length(); ++i) {
						Node attr = attributes.get(i);
						String attributeName = attr.getNodeName();
						String attributeValue = attr.getNodeValue();

						if ("x".equals(attributeName)) {
							attr.setNodeValue(String.valueOf(x));
						}
					}
				}
			}
		}
	}
	
	private native JavaScriptObject createText(JavaScriptObject surface)/*-{
		var text = surface.createText()
//       .setFont({size: "12px"} 
//			 .setFont({family: "Arial", size: "12", weight: "bold"} )
			.setFill("#444444");
			text.setFont({family: "sans-serif", size: "12px"});
//			text.setAttribute("xml:space", "preserve");    
	  return text;
	}-*/;

 @Override
	public double getTextWidth() {
		// if ((UiUtils.isSafari() || UiUtils.isFirefox() || UiUtils.isIE()) && !tspanMode) {
		// 	int fontSize = parseFontSize(getFontSize());
		// 	return MeasurementPanel.getOffsetWidth(getText(), fontSize);
		// }
		return getTextWidth(rawNode);
	}

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

	public native double getTextWidth(JavaScriptObject rawNode)/*-{
		if (rawNode.getShape().text) {
			// possible this is just text content set through setShape({text:text});
			return rawNode.getTextWidth();
		}
		
		if (!rawNode.rawNode || !rawNode.rawNode.childNodes) {
			return 0;
		} 
		
		var result = 0;
		for (var count = 0; count < rawNode.rawNode.childNodes.length; ++count) {
			var tag = rawNode.rawNode.childNodes[count];
			if (tag && tag.tagName == "tspan" && $wnd.jQuery.trim(tag.firstChild.data) != "") {
				var len = tag.getComputedTextLength()
				if (len > result) {
					result = len
				}
			}
		}
		return result;
		
//		return rawNode.getTextWidth();
//		return rawNode.rawNode.getBBox().width;
//		var o = rawNode;
// 		if (o.getShape().text)
//			return parseInt(o.getTextWidth());
//		return 0;
	}-*/;
	
 @Override
	public double getTextHeight() {
		// return 0;
	  return getTextHeight(rawNode);
	}

	private native double getTextHeight(JavaScriptObject rawNode)/*-{
		return rawNode.rawNode.getBBox().height;
		// if (!rawNode.rawNode || !rawNode.rawNode.childNodes) {
		// 	return 0;
		// } 
		
		// var result = 0;
		// for (var count = 0; count < rawNode.rawNode.childNodes.length; ++count) {
		// 	var tag = rawNode.rawNode.childNodes[count];
		// 	if (tag && tag.tagName == "tspan" && $wnd.jQuery.trim(tag.firstChild.data) != "") {
		// 		var len = tag.getBBox().height
		// 		result += len
		// 	}
		// }
		// return result;
	}-*/;
	  
  @Override
  public void applyTransformToShape(int dx, int dy) {
		setShape(getX() + dx, getY() + dy);
  }
  
  @Override
  public void setFontFamily(String family) {
  	_setFontFamily(rawNode, family);
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
  	return _getFontFamily(rawNode);
  }
  private native String _getFontFamily(JavaScriptObject rawNode)/*-{
  	return rawNode.getFont().family;
  }-*/;
  
  @Override
  public void addText(JavaScriptObject tokens, int x,
    int width, boolean textAlignCenter, boolean textAlignRight
  ) {
	  Element r = getRawNode(rawNode);

	  if (textAlignCenter) {
	  	x += width / 2;
	  } else if (textAlignRight) {
	  	x += width;
    }

	  addText(r, tokens, x, width, baselineBottom, UiUtils.isFirefox(), UiUtils.isIE());
	  tspanMode = true;
  }
  
  private native void addText(JavaScriptObject parent,JavaScriptObject tokens, int x, int width, boolean baselineBottom, boolean firefox, boolean ie)/*-{
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
		for (int count = 0; count < raw.getChildCount(); ++count) {
			Node node = raw.getChild(count);
			// recursive node formatting until a text node
			if (node.getNodeType() == 3) {
				result += SafeHtmlUtils.htmlEscape(node.getNodeValue());
			} else {
				JsArray<Node> attributes = getAttributes(node);
				String attrsStr = "";
				if (attributes != null) {
					for (int i = 0; i < attributes.length(); ++i) {
						Node attr = attributes.get(i);
						String attributeName = attr.getNodeName();
						String attributeValue = attr.getNodeValue();

						// TODO text should be relative to group and moved along with the group
						// then no extra calculation would not be needed
						if ("x".equals(attributeName)) {
							attributeValue = String.valueOf(Integer.valueOf(attributeValue) + dx);
						}

						if (attributeName.equals("xlink:href") && 
							 !attributeValue.startsWith("http://") &&
							 !attributeValue.startsWith("https://") &&
							 !attributeValue.startsWith("mailto:")) {
							attributeValue = "";
						}
						attrsStr += attributeName + "='" + attributeValue + "' ";
					}
				}

				result += '<' + node.getNodeName() + ' ' + attrsStr +'>' + 
									formatNodes(node, dx) + 
									"</" + node.getNodeName() + '>';
			}
		}
		return result;
	}

	public static native JsArray<Node> getAttributes(Node elem) /*-{
	  return elem.attributes;
	}-*/;

  public void setShapeSize(int widht, int height) {
  }
  public void initializeText(String text) {
  }
  public void setHorizontal(boolean horizontal) {
  }
  public void setColor(Color color) {
  }
  public void setBorderColor(Color color) {
  }
  public void setShapeProperties(int properties, String parentType) {
  }
}
