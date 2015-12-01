package net.sevenscales.editor.content.ui;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorTarget;
import net.sevenscales.editor.api.impl.FastButton;
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.utils.ColorHelpers;
import net.sevenscales.editor.content.utils.JQuery;
import net.sevenscales.editor.content.utils.Rgb;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.gfx.domain.ElementColor;
import net.sevenscales.editor.gfx.domain.Color;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ColorSelections extends Composite {
	private static final SLogger logger = SLogger.createLogger(ColorSelections.class);

	private static ColorSelectionsUiBinder uiBinder = GWT
			.create(ColorSelectionsUiBinder.class);

	interface ColorSelectionsUiBinder extends UiBinder<Widget, ColorSelections> {
	}

	public interface Style extends CssResource {
	}
	
	public interface SelectionHandler {
		void itemSelected(ElementColor currentColor, ColorTarget colorTarget);
	}

	@UiField Style style;
	@UiField FlexTable colortable;
	// @UiField
	// SimplePanel sampleColor;
	// @UiField TextBox colorValue;
	@UiField Element header;
	@UiField Element defaultColor;
	@UiField Element transparent;
	
	public static native String rgb2hex(int r, int g, int b)/*-{
		function hex(x) {
			return ("0" + parseInt(x).toString(16)).slice(-2);
		}
		return hex(r) + hex(g) + hex(b);
	}-*/;

	private native String rgb2hex(String rgb)/*-{
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		function hex(x) {
			return ("0" + parseInt(x).toString(16)).slice(-2);
		}
		return hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
	}-*/;

	private native int rgb2hexval(String rgb)/*-{
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		function hex(x) {
			return 0x100 | parseInt(x);
		}
		return hex(rgb[1]) | hex(rgb[2]) | hex(rgb[3]);
	}-*/;
	
	private native int red(String rgb)/*-{
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return parseInt(rgb[1]);
	}-*/;
	private native int green(String rgb)/*-{
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return parseInt(rgb[2]);
	}-*/;
	private native int blue(String rgb)/*-{
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return parseInt(rgb[3]);
	}-*/;
	

	private MouseOverHandler mouseOverHandler = new MouseOverHandler() {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			selectCurrentColor(event);
		}
	};
	
	private <H extends EventHandler> void selectCurrentColor(GwtEvent<H> event) {
		Widget widget = (Widget) event.getSource();
		
		String selectedRgb = widget.getElement().getStyle().getBackgroundColor();
		if (selectedRgb.startsWith("#")) {
			// in hex format IE8 at least
			selectedRgb = ColorHelpers.toRgb(selectedRgb).toString();
		}
		String color = rgb2hex(selectedRgb).toUpperCase();
		int r = red(selectedRgb);
		int g = green(selectedRgb);
		int b = blue(selectedRgb);

		switch (colorTarget) {
			case BACKGROUND:
				selectedBackgroundColor(color, selectedRgb, r, g, b);
				break;
			case BORDER:
				selectedBorderColor(color, r, g, b);
				break;
			case TEXT:
				selectedTextColor(color, r, g, b);
				break;
		}

		// colorValue.setText(color);
		// colorValue.getElement().getStyle().setBackgroundColor("#" + color);
		// colorValue.getElement().getStyle().setColor(currentColor.getTextColor().toHexStringWithHash());
	}

	private void selectedBackgroundColor(String color, String selectedRgb, int r, int g, int b) {
		currentColor.setBackgroundColor(new Color(r, g, b, 0.85));
		String textcolor = textColorByBackgroundColor(selectedRgb);
		int tr = Integer.valueOf(textcolor.substring(0, 2), 16);
		int tg = Integer.valueOf(textcolor.substring(2, 4), 16);
		int tb = Integer.valueOf(textcolor.substring(4, 6), 16);

		currentColor.setTextColor(new Color(tr, tg, tb, 1));

		updateColorCheckMark(currentColor.getBackgroundColor());
	}

	private String textColorByBackgroundColor(String bgcolor) {
		String textcolor = "ffffff";
		if (ColorHelpers.isRgbBlack(bgcolor)) {
			textcolor = "444444";
		}
		return textcolor;
	}

	private void selectedBorderColor(String color, int r, int g, int b) {
		currentColor.setBorderColor(new Color(r, g, b, 1));
		updateColorCheckMark(currentColor.getBorderColor());
	}

	private void selectedTextColor(String color, int r, int g, int b) {
		currentColor.setTextColor(new Color(r, g, b, 1));
		updateColorCheckMark(currentColor.getTextColor());
	}
	
	private ClickHandler clickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			selectCurrentColor(event);
			logger.debug2("onClick currentColor: {}", currentColor);
			ElementColor color = (ElementColor) editorContext.get(EditorProperty.CURRENT_COLOR);
			switch (colorTarget) {
			case BORDER:
				color.setBorderColor(currentColor.getBorderColor().create());
				break;
			case BACKGROUND:
				color.setBackgroundColor(currentColor.getBackgroundColor().create());
				color.setTextColor(currentColor.getTextColor().create());
				// set border color based on background
				Color borderColor = ColorHelpers.createBorderColor(currentColor.getBackgroundColor());
				color.setBorderColor(borderColor);
				break;
			case TEXT:
				color.setTextColor(currentColor.getTextColor().create());
				break;
			}
			selectionHandler.itemSelected(color, colorTarget);
		}
	};
	
	private ElementColor currentColor = new ElementColor(Theme.getCurrentColorScheme().getTextColor().create(), 
																											 Theme.getCurrentColorScheme().getBorderColor().create(),
																										   Theme.getCurrentColorScheme().getBackgroundColor().create());

//	private String currentBackgroundColor = "#3366FF";
//	private String currentTextColor = "#FFFFFF";
	private SelectionHandler selectionHandler; 
	private ColorTarget colorTarget = ColorTarget.BACKGROUND;
	
	@UiField AnchorElement border;
	@UiField AnchorElement background;
	@UiField AnchorElement textColor;
	private EditorContext editorContext;
	

	public ColorSelections(EditorContext editorContext) {
		this.editorContext = editorContext;
		initWidget(uiBinder.createAndBindUi(this));
		
		// colorValue.getElement().getStyle().setBackgroundColor(currentColor.getBackgroundColor().toHexStringWithHash());
		// colorValue.getElement().getStyle().setColor(currentColor.getTextColor().toHexStringWithHash());

		colortable.setWidget(0, 0, createColorButton("#000000"));
		colortable.setWidget(1, 0, createColorButton("#333333"));
		colortable.setWidget(2, 0, createColorButton("#666666"));
		colortable.setWidget(3, 0, createColorButton("#999999"));
		colortable.setWidget(4, 0, createColorButton("#CCCCCC"));
		colortable.setWidget(5, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(6, 0, createColorButton("#FF0000"));
		colortable.setWidget(7, 0, createColorButton("#00FF00"));
		colortable.setWidget(8, 0, createColorButton("#0000FF"));
		colortable.setWidget(9, 0, createColorButton("#FFFF00"));
		colortable.setWidget(10, 0, createColorButton("#00FFFF"));
		colortable.setWidget(11, 0, createColorButton("#FF00FF"));
		
		colortable.setWidget(0, 1, createColorButton("#000000"));
		colortable.setWidget(1, 1, createColorButton("#000000"));
		colortable.setWidget(2, 1, createColorButton("#000000"));
		colortable.setWidget(3, 1, createColorButton("#000000"));
		colortable.setWidget(4, 1, createColorButton("#000000"));
		colortable.setWidget(5, 1, createColorButton("#000000"));
		colortable.setWidget(6, 1, createColorButton("#000000"));
		colortable.setWidget(7, 1, createColorButton("#000000"));
		colortable.setWidget(8, 1, createColorButton("#000000"));
		colortable.setWidget(9, 1, createColorButton("#000000"));
		colortable.setWidget(10, 1, createColorButton("#000000"));
		colortable.setWidget(11, 1, createColorButton("#000000"));
		
		colorBox(0, 0, 0x000000);
		colorBox(0, 6, 0x330000);
		colorBox(0, 12, 0x660000);
		colorBox(6, 0, 0x990000);
		colorBox(6, 6, 0xCC0000);
		colorBox(6, 12, 0xFF0000);
		
		new FastElementButton(border).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				borderMode();
			}
		});
		
		new FastElementButton(background).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				backgroundMode();
			}
		});

		new FastElementButton(textColor).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textMode();
			}
		});

		tapDefaultColor(defaultColor, this);
		tapTransparent(transparent, this);
	}

	private native void tapDefaultColor(Element e, ColorSelections me)/*-{
		$wnd.Hammer(e, {preventDefault: true}).on('tap', function() {
			me.@net.sevenscales.editor.content.ui.ColorSelections::onRestoreDefaults()();
		})
	}-*/;

	private native void tapTransparent(Element e, ColorSelections me)/*-{
		$wnd.Hammer(e, {preventDefault: true}).on('tap', function() {
			me.@net.sevenscales.editor.content.ui.ColorSelections::onTransparent()();
		})
	}-*/;

	public void backgroundMode() {
		colorTarget = ColorTarget.BACKGROUND;
		updateColorCheckMark();
		JQuery.tab(background, "show");
	}

	public void borderMode() {
		colorTarget = ColorTarget.BORDER;
		updateColorCheckMark();
		JQuery.tab(border, "show");
	}

	public void textMode() {
		colorTarget = ColorTarget.TEXT;
		updateColorCheckMark();
		JQuery.tab(textColor, "show");
	}

	public void hideHeader() {
		borderMode();
		// colorValue.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.NONE);
		transparent.getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.NONE);
		header.getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.NONE);
	}
	public void showHeader() {
		// backgroundMode();
		// colorValue.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE);
		transparent.getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);
		header.getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE);
	}
		
	private void colorBox(int baserow, int basecol, int basecolor) {
		for (int row = baserow; row < baserow + 6; ++row) {
			for (int col = basecol; col < basecol + 6; ++col) {
				colortable.setWidget(row, col + 2, createColorButton(colorHex(row, col, basecolor))); // + 2 two extra cols in the beginning
			}
		}
	}

	private Widget createColorButton(String hexcolor) {
		FocusPanel focus = new FocusPanel();
		FastButton color = new FastButton(focus);
//		color.addMouseOverHandler(mouseOverHandler);
		color.addClickHandler(clickHandler);
		color.setStyleName("focuspanel");
		
		
		int width = 10;
		int height = 10;
		if (TouchHelpers.isSupportsTouch()) {
			width = 20;
			height = 20;
		}
		color.setPixelSize(width, height);
//		String hex = colorHex(row, col, basecolor);
//		System.out.println(hex);
		color.getElement().setAttribute("data-hexcolor", hexcolor);
		color.getElement().getStyle().setBackgroundColor(hexcolor);
		return color;
	}

	private String colorHex(int row, int col, int basecolor) {
		int add = basecolor | add(row, col);
		String result = Integer.toHexString(0x1000000 | add).substring(1)
				.toUpperCase();
		return "#" + result;
	}

	private int add(int row, int col) {
		int rowadd = 0x000033;
		int coladd = 0x003300;
		return (rowadd * (row % 6)) | (coladd * (col % 6));
	}
	
	public void setSelectionHandler(SelectionHandler selectionHandler) {
		this.selectionHandler = selectionHandler;
	}

	public static int lighter(int value) {
		if (255 - value < 255) {
			return 255 - value + 51;
		}
		return 255;
	}

	private void onRestoreDefaults() {
		currentColor.setBackgroundColor(Theme.getCurrentColorScheme().getBackgroundColor().create());
		currentColor.setTextColor(Theme.getCurrentColorScheme().getTextColor().create());
		currentColor.setBorderColor(Theme.getCurrentColorScheme().getBorderColor().create());

		updateColorCheckMark();

		selectionHandler.itemSelected(currentColor, ColorTarget.ALL);
	}
	
	public void onTransparent() {
		switch (colorTarget) {
			case TEXT:
				// not possible to set text as transparent
				// currentColor.setTextColor(Theme.getCurrentColorScheme().getTextColor().create());
				break;
			case BORDER:
				currentColor.getBorderColor().opacity = 0;
				break;
			case BACKGROUND:
				currentColor.getBackgroundColor().opacity = 0;
				break;
		}
		
		updateColorCheckMark();
		selectionHandler.itemSelected(currentColor, colorTarget);
	}
	
	public void setCurrentDiagramColor(Color textColor, Color backgroundColor, Color borderColor) {
		// colorValue.setText(backgroundColor.toHexStringWithHash().toUpperCase());
		// colorValue.getElement().getStyle().setColor(textColor.toHexStringWithHash());
		// colorValue.getElement().getStyle().setBackgroundColor(backgroundColor.toHexStringWithHash());

		currentColor.setTextColor(textColor.create());
		currentColor.setBorderColor(borderColor.create());
		currentColor.setBackgroundColor(backgroundColor.create());

		updateColorCheckMark();
	}

	private Color pickColorByTab(Color textColor, Color backgroundColor, Color borderColor) {
		Color result = null;

		switch (colorTarget) {
			case TEXT:
				result = textColor;
				break;
			case BORDER:
				result = borderColor;
				break;
			case BACKGROUND:
				result = backgroundColor;
				break;
			default:
				result = backgroundColor;
				break;
		}

		return result;
	}

	private void updateColorCheckMark() {
		Color color = pickColorByTab(currentColor.getTextColor(), currentColor.getBackgroundColor(), currentColor.getBorderColor());
		updateColorCheckMark(color);
	}

	private void updateColorCheckMark(Color color) {
		for (int row = 0; row < colortable.getRowCount(); ++row) {
			for (int col = 0; col < colortable.getCellCount(row); ++col) {
				Widget w = colortable.getWidget(row, col);

				if (color != null && color.getOpacity() > 0.0 && w.getElement().getAttribute("data-hexcolor").equals(color.toHexStringWithHash().toUpperCase())) {
					w.getElement().setInnerText("✓");

					String tc = textColorByBackgroundColor(ColorHelpers.toRgb(color.toHexStringWithHash()).toString());
					w.getElement().getStyle().setColor("#"+tc.toUpperCase());
				} else {
					w.getElement().setInnerText("");
				}
			}
		}
	}
}
