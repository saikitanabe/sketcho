package net.sevenscales.sketcho.client.uicomponents;

import net.sevenscales.editor.uicomponents.BrowserStyle;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class FloatPanel extends ComplexPanel {
	public static class Constant {
		protected String value;

		protected Constant(String value) {
			this.value = value;
		}
	}
	
	public static final Constant FLOAT_RIGHT = new Constant("right");
	public static final Constant FLOAT_LEFT = new Constant("left");


	public FloatPanel() {
		setElement(DOM.createDiv());
		setWidth("100%");
	    DOM.setStyleAttribute(getElement(), "background", "transparent");
	}

	public void add(Widget widget, Constant constant) {
	    DOM.setStyleAttribute(widget.getElement(), "display", "inline");
		DOM.setStyleAttribute(widget.getElement(), BrowserStyle.floatStyle(), constant.value);
		DOM.setStyleAttribute(widget.getElement(), "margin-right", "30px");
		super.add(widget, getElement());
	}
}
