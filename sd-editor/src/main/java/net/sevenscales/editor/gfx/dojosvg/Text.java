package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.editor.api.MeasurementPanel;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IText;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

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
  
	Text(IContainer container) {
		rawNode = createText(container.getContainer());
		setFontWeight(WEIGHT_NORMAL);
	}
	
	public int getX() {
		return getX(rawNode);
	}
	public native int getX(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().x);
	}-*/;

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
	
  public String getFontWeight() {
    return nativeGetFontWeight(rawNode);
  }
  private native String nativeGetFontWeight(JavaScriptObject text)/*-{
    return text.getFont().weight;
  }-*/;
  
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
	
	public void setAlignment(String alignment) {
	  setAlignment(rawNode, alignment);
	}
	private native void setAlignment(JavaScriptObject rawNode, String alignment)/*-{
	  rawNode.setShape( {align:alignment} );
	}-*/;
	
  public String getAlignment() {
    return nativeGetAlignment(rawNode);
  }
  private native String nativeGetAlignment(JavaScriptObject rawNode)/*-{
    return rawNode.getShape().align;
  }-*/;

	public void setText(String text) {
		setText(rawNode, text);
	}
	public native void setText(JavaScriptObject rawNode, String text)/*-{
		rawNode.setShape( {text:text} );
	}-*/;
	
	public String getText() {
	  return nativeGetText(rawNode);
	}
	private native String nativeGetText(JavaScriptObject rawNode)/*-{
	  return rawNode.getShape().text;
	}-*/;
	
	public void setShape(int x, int y) {
		setShape(rawNode, x, y);
	}
	public native void setShape(JavaScriptObject rawNode, int x, int y)/*-{
		rawNode.setShape( {x:x, y:y} );
	}-*/;
	
	private native JavaScriptObject createText(JavaScriptObject surface)/*-{
		var text = surface.createText()
//       .setFont({size: "12px"} 
//			 .setFont({family: "Arial", size: "12", weight: "bold"} )
			.setFill("#444444");
			text.setFont({family: "sans-serif", size: "12px"});
//			text.setAttribute("xml:space", "preserve");
	  return text;
	}-*/;

	public double getTextWidth() {
		if ((UiUtils.isSafari() || UiUtils.isFirefox()) && justTextNoTspan(rawNode)) {
			int fontSize = parseFontSize(getFontSize());
			return MeasurementPanel.getOffsetWidth(getText(), fontSize);
		}
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
	
	private native boolean justTextNoTspan(JavaScriptObject rawNode)/*-{
		return rawNode.getShape().text != null;
	}-*/;
	
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
				result += tag.getComputedTextLength();
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
	
	public double getTextHeight() {
		return 0;
//	  return getTextHeight(rawNode);
	}
	  
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
  
  public void addText(JavaScriptObject tokens, int x, int width) {
	  Element r = getRawNode(rawNode);
	  addText(r, tokens, x, width);
  }
  
  private native void addText(JavaScriptObject parent,JavaScriptObject tokens, int x, int width)/*-{
		var svgText = $wnd.svgTextArea(parent, x, width);
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
    
  public String getChildElements(int dx) {
  	String children = getChildElementsEscaped(dx);
  	return children;
  }
  
	private String getChildElementsEscaped(int dx) {
		String result = "";
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
							attributeValue = String.valueOf(Integer.valueOf(attributeValue) + dx);
						}
						attrsStr += attributeName + "='" + attributeValue + "' ";
					}
					result += '<' + node.getNodeName() + ' ' + attrsStr +'>' + SafeHtmlUtils.htmlEscape(getTextContent(node)) + "</" + node.getNodeName() + '>';
				}
			}
		}
		
		return result;
	};

	public static native String getTextContent(Node elem) /*-{
	  return elem.textContent;
	}-*/;

	public static native JsArray<Node> getAttributes(Node elem) /*-{
	  return elem.attributes;
	}-*/;

}
