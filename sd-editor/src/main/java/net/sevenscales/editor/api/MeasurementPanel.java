package net.sevenscales.editor.api;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.editor.content.utils.TokenParser;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;

public class MeasurementPanel {
	private static SimplePanel measurementPanel;
	private static SimplePanel measurementHorizontalPanel;
	
	static {
		measurementPanel = createMeasurementPanel();
		measurementHorizontalPanel = createMeasurementPanel();
		measurementHorizontalPanel.setStyleName("properties-MeasurementPanel-Horizontal");
	}
	
	public static void init() {
	}
	
	private static SimplePanel createMeasurementPanel() {
		SimplePanel result = new SimplePanel();
  	result.setStyleName("properties-MeasurementPanel");
  	result.getElement().getStyle().setVisibility(Visibility.HIDDEN);
//	 	measurementPanel.setWidth(hasTextElement.getWidth() + "px");
	 	result.getElement().getStyle().setPosition(Position.FIXED);

  	RootPanel.get().add(result);
  	return result;
	}

	private MeasurementPanel() {
	}
	
	public static SimplePanel getMeasurementPanel() {
		return measurementPanel;
	}
	
	public static void setText(String text, int width) {
    JavaScriptObject tokens = TokenParser.parse2(text);
    setTokens(tokens, width);
	}

	public static void setPlainTextAsHtml(String plainText, int width) {
		setWidth(width);
		String tmphtml = SafeHtmlUtils.htmlEscape(plainText).replaceAll("\\n", "<br>&nbsp;");
		MeasurementPanel.getMeasurementPanel().getElement().setInnerHTML(tmphtml);
	}

	public static void setTokens(JavaScriptObject tokens, int width) {
		_setText(TokenParser.formatHtml(tokens), width);
	}

	private static void setWidth(int width) {
		if (width > 0) {
			MeasurementPanel.getMeasurementPanel().setWidth(width + "px");
		}
	}

	private static void _setText(String html, int width) {
		setWidth(width);
		
		// add also html space entity to increase measurement panel height
		// it will lead for one extra space in measuremnt div but it doesn't
		// make too much failure.
		// String tmphtml = SafeHtmlUtils.htmlEscape(text).replaceAll("\\n", "<br>&nbsp;");
		// String tmphtml = text.replaceAll("\\n", "<br>&nbsp;");
		MeasurementPanel.getMeasurementPanel().getElement().setInnerHTML(html);

	  // this is just for debugging, to show measurement panel right next to the element
//			  System.out.println("offsetHeight: " + measurementPanel.getOffsetHeight());
		// selectedDiagram.setHeight(measurementPanel.getOffsetHeight() + TextElementFormatUtil.DEFAULT_MARGIN_TOP + TextElementFormatUtil.DEFAULT_MARGIN_BOTTOM);
		// selectedDiagram.setHeight(measurementPanel.getOffsetHeight() + TextElementFormatUtil.DEFAULT_MARGIN_TOP + TextElementFormatUtil.DEFAULT_MARGIN_BOTTOM);
	}

	public static int getOffsetHeight() {
		return measurementPanel.getOffsetHeight();
	}
	
	public static int getOffsetWidth(String text, int fontSize) {
		measurementHorizontalPanel.getElement().getStyle().setFontSize((double) fontSize, Unit.PX);
		return calcWidth(measurementHorizontalPanel.getElement(), SafeHtmlUtils.htmlEscape(text));
	}
	
	public static native int calcWidth(Element measure, String text)/*-{
	  var html_calc = '<span>' + text + '</span>';
	  $wnd.jQuery(measure).html(html_calc);
	  var width = $wnd.jQuery(measure).find('span:first').width();
	  return width;
	}-*/;

	public static void setPosition(int left, int top) {
		MeasurementPanel.getMeasurementPanel().getElement().getStyle().setLeft(left, Unit.PX);
		MeasurementPanel.getMeasurementPanel().getElement().getStyle().setTop(top, Unit.PX);
	}
}
