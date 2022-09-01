package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.EventListener;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorTarget;
import net.sevenscales.editor.api.event.hammer.Hammer2;
import net.sevenscales.editor.api.event.hammer.Hammer2TapEventHandler;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorSetType;
import net.sevenscales.editor.api.impl.FastButton;
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.utils.ColorHelpers;
import net.sevenscales.editor.content.utils.JQuery;
import net.sevenscales.editor.content.utils.Rgb;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ElementColor;

public class ColorSelections extends Composite {
	private static final SLogger logger = SLogger.createLogger(ColorSelections.class);

	private static ColorSelectionsUiBinder uiBinder = GWT
			.create(ColorSelectionsUiBinder.class);

	interface ColorSelectionsUiBinder extends UiBinder<Widget, ColorSelections> {
	}

	public interface Style extends CssResource {
	}
	
	public interface SelectionHandler {
		void itemSelected(ElementColor currentColor, ColorTarget colorTarget, ColorSetType colorSetType);
	}

	@UiField Style style;
	@UiField FlexTable colortable;
	// @UiField
	// SimplePanel sampleColor;
	// @UiField TextBox colorValue;
	@UiField Element header;
	@UiField Element defaultColor;
	// @UiField Element transparent;
  @UiField Element opacitySection;
  @UiField Element opacityBtn;
  @UiField InputElement opacityInput;
	@UiField Element customColorBtn;
  @UiField InputElement customColorInput;

  private static final String defaultCustomColor = "#000000";
  private static final int defaultBackgroundOpacity = 85;

  private int opacity = defaultBackgroundOpacity;

	// private int currentRememberIndex = 0;
	
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
	
	private MouseOverHandler mouseOverHandler = new MouseOverHandler() {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			selectCurrentColor(event);
		}
	};

	private ClickHandler clickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			selectCurrentColor(event);
      setColor();
		}
	};

  private void onCustomColor(String value) {
    try {
      Rgb rgb = Rgb.toRgba(value);

      if (value.length() >= 8) {
        // update opacity based on rgb alpha that was provided in hex value
        setOpacity((int) (rgb.a * 100));
      }

      customColorBtn.getStyle().setBackgroundColor(
        rgb.toHex()
      );

      selectRgbColor(rgb);
    } catch (Exception e) {
      // invalid color value
    }

  }

  private void applyCustomColor() {
    String color = customColorInput.getValue();
    if ("".equals(color)) {
      color = defaultCustomColor;
    }
    onCustomColor(color);
    setColor();
  }

  private void applyOpacity() {
    onOpacity(opacityInput.getValue());

    Widget w = colortable.getWidget(0, 0);

    String hexColor = w.getElement().getAttribute("data-hexcolor");

    try {
      Rgb rgb = Rgb.parse(hexColor);
      selectRgbColor(rgb);
      setColor();
    } catch (Exception e) {

    }
  }

  private void onOpacity(String value) {
    if (!"".equals(value)) {
      this.opacity = Integer.parseInt(value);
    } else {
      this.opacity = defaultBackgroundOpacity;
    }
  }

	
	private <H extends EventHandler> void selectCurrentColor(GwtEvent<H> event) {
		Widget widget = (Widget) event.getSource();
		
		String selectedRgb = widget.getElement().getStyle().getBackgroundColor();
		// if (selectedRgb.startsWith("#")) {
		// 	// in hex format IE8 at least
		// 	selectedRgb = Rgb.toRgb(selectedRgb).toString();
		// }

    try {
      Rgb rgb = Rgb.parse(selectedRgb);
      selectRgbColor(rgb);
    } catch (Exception e) {

    }

		// colorValue.setText(color);
		// colorValue.getElement().getStyle().setBackgroundColor("#" + color);
		// colorValue.getElement().getStyle().setColor(currentColor.getTextColor().toHexStringWithHash());
	}

  private void selectRgbColor(Rgb rgb) {
		// int r = red(rgbColor);
		// int g = green(rgbColor);
		// int b = blue(rgbColor);

    // String color = rgb2hex(rgbColor).toUpperCase();

		switch (colorTarget) {
			case BACKGROUND:
				selectedBackgroundColor(rgb);
				break;
			case BORDER:
				selectedBorderColor(rgb);
				break;
			case TEXT:
				selectedTextColor(rgb);
				break;
		}
  }

  private void setColor() {
    logger.debug2("onClick currentColor: {}", currentColor);
    ElementColor color = (ElementColor) editorContext.get(EditorProperty.CURRENT_COLOR);
    switch (colorTarget) {
    case BORDER:
      color.setBorderColor(currentColor.getBorderColor().create());

      rememberColor(currentColor.getBorderColor());
      updateColorCheckMark(currentColor.getBorderColor());
      break;
    case BACKGROUND:
      color.setBackgroundColor(currentColor.getBackgroundColor().create());
      color.setTextColor(currentColor.getTextColor().create());
      // set border color based on background
      Color borderColor = ColorHelpers.createBorderColor(currentColor.getBackgroundColor());
      color.setBorderColor(borderColor);

      rememberColor(currentColor.getBackgroundColor());
      updateColorCheckMark(currentColor.getBackgroundColor());
      break;
    case TEXT:
      color.setTextColor(currentColor.getTextColor().create());

      rememberColor(currentColor.getTextColor());
      updateColorCheckMark(currentColor.getTextColor());
      break;
    }
    selectionHandler.itemSelected(color, colorTarget, ColorSetType.NORMAL);
  }

  private void setOpacity(int value) {
    this.opacity = value;
    opacityInput.setValue(this.opacity + "");
  }

	private void rememberColor(Color color) {
		String hexcolor = color.toHexStringWithHash().toUpperCase();
		if (color.getOpacity() > 0 && !isRemembered(hexcolor)) {
			pushRememberColors();
			// int index = currentRememberIndex++ % colortable.getRowCount();
			Widget w = colortable.getWidget(0, 0);

			w.getElement().setAttribute("data-hexcolor", hexcolor);
			w.getElement().getStyle().setBackgroundColor(hexcolor);
		}
	}

	private void pushRememberColors() {
		for (int row = colortable.getRowCount() - 2; row >= 0 ; --row) {
			Widget w = colortable.getWidget(row, 0);
			Widget w2 = colortable.getWidget(row + 1, 0);
			String hexcolor = w.getElement().getAttribute("data-hexcolor");
			w2.getElement().setAttribute("data-hexcolor", hexcolor);
			w2.getElement().getStyle().setBackgroundColor(hexcolor);
		}
	}

	private boolean isRemembered(String color) {
		for (int row = 0; row < colortable.getRowCount(); ++row) {
			Widget w = colortable.getWidget(row, 0);
			if (w.getElement().getAttribute("data-hexcolor").equals(color)) {
				return true;
			}
		}
		return false;
	}

	private void selectedBackgroundColor(Rgb rgb) {
    rgb.a = this.opacity / 100.0;

		currentColor.setBackgroundColor(new Color(rgb.red, rgb.green, rgb.blue, rgb.a));
		String textcolor = textColorByBackgroundColor(rgb.toRgb());
		int tr = Integer.valueOf(textcolor.substring(0, 2), 16);
		int tg = Integer.valueOf(textcolor.substring(2, 4), 16);
		int tb = Integer.valueOf(textcolor.substring(4, 6), 16);

		currentColor.setTextColor(new Color(tr, tg, tb, 1));
	}

	private String textColorByBackgroundColor(String bgcolor) {
		String textcolor = "ffffff";
		if (ColorHelpers.isRgbWhite(bgcolor)) {
			textcolor = "444444";
		}
		return textcolor;
	}

	private void selectedBorderColor(Rgb rgb) {
		currentColor.setBorderColor(new Color(rgb.red, rgb.green, rgb.blue, 1));
	}

	private void selectedTextColor(Rgb rgb) {
		currentColor.setTextColor(new Color(rgb.red, rgb.green, rgb.blue, 1));
	}
	
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

		colortable.setWidget(0, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(1, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(2, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(3, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(4, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(5, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(6, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(7, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(8, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(9, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(10, 0, createColorButton("#FFFFFF"));
		colortable.setWidget(11, 0, createColorButton("#FFFFFF"));

		colortable.setWidget(0, 1, createColorButton("#000000"));
		colortable.setWidget(1, 1, createColorButton("#333333"));
		colortable.setWidget(2, 1, createColorButton("#666666"));
		colortable.setWidget(3, 1, createColorButton("#999999"));
		colortable.setWidget(4, 1, createColorButton("#CCCCCC"));
		colortable.setWidget(5, 1, createColorButton("#FFFFFF"));
		colortable.setWidget(6, 1, createColorButton("#FF0000"));
		colortable.setWidget(7, 1, createColorButton("#00FF00"));
		colortable.setWidget(8, 1, createColorButton("#0000FF"));
		colortable.setWidget(9, 1, createColorButton("#FFFF00"));
		colortable.setWidget(10, 1, createColorButton("#00FFFF"));
		colortable.setWidget(11, 1, createColorButton("#FF00FF"));
				
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

    // tapDefaultColor(defaultColor, this);
    new Hammer2(defaultColor).on("tap", new Hammer2TapEventHandler(){
      @Override
      public void onHammerTap(Event event) {
        onRestoreDefaults();
      }
    });

    // tapTransparent(transparent, this);
    // new Hammer2(transparent).on("tap", new Hammer2TapEventHandler(){
    //   @Override
    //   public void onHammerTap(Event event) {
    //     onTransparent();
    //   }
    // });


    new Hammer2(customColorBtn).on("tap", new Hammer2TapEventHandler(){
      @Override
      public void onHammerTap(Event event) {
        applyCustomColor();
      }
    });

    customColorInput.setValue(defaultCustomColor);

    addCustomColorInputHandler(customColorInput, this);

		Event.sinkEvents(customColorInput, Event.ONKEYPRESS | Event.ONKEYUP | Event.ONCLICK);
    Event.setEventListener(customColorInput, new EventListener() {
      @Override
      public void onBrowserEvent(Event event) {
				switch (event.getTypeInt()) {
					case Event.ONCLICK:
					customColorInput.select();
					break;
					case Event.ONKEYDOWN:
					break;
					case Event.ONKEYUP:
					if (KeyCodes.KEY_ENTER == event.getKeyCode()) {
						applyCustomColor();
					}
					break;
      	}
    	}
  	});

    new Hammer2(opacityBtn).on("tap", new Hammer2TapEventHandler(){
      @Override
      public void onHammerTap(Event event) {
        applyOpacity();
      }
    });

    opacityInput.setValue(opacity + "");

    addOpacityInput(opacityInput, this);

		Event.sinkEvents(opacityInput, Event.ONKEYPRESS | Event.ONKEYUP | Event.ONCLICK);
    Event.setEventListener(opacityInput, new EventListener() {
      @Override
      public void onBrowserEvent(Event event) {
				switch (event.getTypeInt()) {
					case Event.ONCLICK:
					opacityInput.select();
					break;
					case Event.ONKEYDOWN:
					break;
					case Event.ONKEYUP:
					if (KeyCodes.KEY_ENTER == event.getKeyCode()) {
						applyOpacity();
					}
					break;
      	}
    	}
  	});

	}

  private native void addCustomColorInputHandler(
    Element e,
    ColorSelections me
  )/*-{
    e.addEventListener("keyup", function() {
      me.@net.sevenscales.editor.content.ui.ColorSelections::onCustomColor(Ljava/lang/String;)(this.value);
    })
  }-*/;

  private native void addOpacityInput(
    Element e,
    ColorSelections me
  )/*-{
    e.addEventListener("keyup", function() {
      me.@net.sevenscales.editor.content.ui.ColorSelections::onOpacity(Ljava/lang/String;)(this.value);
    })
  }-*/;


	// private native void tapDefaultColor(Element e, ColorSelections me)/*-{
	// 	$wnd.Hammer2(e, {preventDefault: true}).on('tap', function() {
	// 		me.@net.sevenscales.editor.content.ui.ColorSelections::onRestoreDefaults()();
	// 	})
	// }-*/;

	// private native void tapTransparent(Element e, ColorSelections me)/*-{
	// 	$wnd.Hammer2(e, {preventDefault: true}).on('tap', function() {
	// 		me.@net.sevenscales.editor.content.ui.ColorSelections::onTransparent()();
	// 	})
	// }-*/;

	public void backgroundMode() {
		colorTarget = ColorTarget.BACKGROUND;
		updateColorCheckMark();
		JQuery.tab(background, "show");
    opacitySection.getStyle().setVisibility(com.google.gwt.dom.client.Style.Visibility.VISIBLE);
	}

	public void borderMode() {
		colorTarget = ColorTarget.BORDER;
		updateColorCheckMark();
		JQuery.tab(border, "show");
    opacitySection.getStyle().setVisibility(com.google.gwt.dom.client.Style.Visibility.HIDDEN);
	}

	public void textMode() {
		colorTarget = ColorTarget.TEXT;
		updateColorCheckMark();
		JQuery.tab(textColor, "show");
    opacitySection.getStyle().setVisibility(com.google.gwt.dom.client.Style.Visibility.HIDDEN);
	}

	public void hideHeader() {
		borderMode();
		// colorValue.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.NONE);
		
    opacitySection.getStyle().setVisibility(com.google.gwt.dom.client.Style.Visibility.HIDDEN);
		header.getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.NONE);
	}
	public void showHeader() {
		// backgroundMode();
		// colorValue.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE);
		opacityInput.getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);
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
		color.setStyleName("focuspanel color-btn");
		
		
		int width = 16;
		int height = 16;
		if (TouchHelpers.isSupportsTouch()) {
			width = 20;
			height = 20;
		}
		color.setPixelSize(width, height);
//		String hex = colorHex(row, col, basecolor);
//		System.out.println(hex);
		color.getElement().setAttribute("data-hexcolor", hexcolor);
		color.getElement().getStyle().setBackgroundColor(hexcolor);
		color.getElement().getStyle().setLineHeight(height, Unit.PX);
		color.getElement().getStyle().setFontSize(height - 1, Unit.PX);
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

    setOpacity(defaultBackgroundOpacity);

		currentColor.setBackgroundColor(Theme.getCurrentColorScheme().getBackgroundColor().create());
		currentColor.setTextColor(Theme.getCurrentColorScheme().getTextColor().create());
		currentColor.setBorderColor(Theme.getCurrentColorScheme().getBorderColor().create());

		updateColorCheckMark();

		selectionHandler.itemSelected(
			currentColor,
			ColorTarget.ALL,
			ColorSetType.RESTORE_COLORS
		);
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
		selectionHandler.itemSelected(currentColor, colorTarget, ColorSetType.TRANSPARENT);
	}
	
	public void setCurrentDiagramColor(Color textColor, Color backgroundColor, Color borderColor) {
		// colorValue.setText(backgroundColor.toHexStringWithHash().toUpperCase());
		// colorValue.getElement().getStyle().setColor(textColor.toHexStringWithHash());
		// colorValue.getElement().getStyle().setBackgroundColor(backgroundColor.toHexStringWithHash());

		currentColor.setTextColor(textColor.create());
		currentColor.setBorderColor(borderColor.create());
		currentColor.setBackgroundColor(backgroundColor.create());

		Color color = pickColorByTab(currentColor.getTextColor(), currentColor.getBackgroundColor(), currentColor.getBorderColor());
    rememberColor(color);

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

		// rememberColor(color);
		updateColorCheckMark(color);
	}

	private void updateColorCheckMark(Color color) {
		for (int row = 0; row < colortable.getRowCount(); ++row) {
			for (int col = 0; col < colortable.getCellCount(row); ++col) {
				Widget w = colortable.getWidget(row, col);

				if (color != null && color.getOpacity() > 0.0 && w.getElement().getAttribute("data-hexcolor").equals(color.toHexStringWithHash().toUpperCase())) {
					w.getElement().setInnerText("✓");

					String tc = textColorByBackgroundColor(Rgb.toRgb(color.toHexStringWithHash()).toString());
					w.getElement().getStyle().setColor("#"+tc.toUpperCase());
				} else {
					w.getElement().setInnerText("");
				}
			}
		}
	}
}
