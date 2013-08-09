package net.sevenscales.sketcho.client.uicomponents;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class ColorSelector extends SimplePanel {
  interface MyUiBinder extends UiBinder<Widget, ColorSelector> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
  
//  public interface MyStyle extends CssResource {
//    String text();
//  }
//  @UiField MyStyle style;

  @UiField PopupPanel popup;
  @UiField FlexTable flexTable;
  private ICallback callback;

//  @UiField Grid colorGrid;
  public interface ICallback {
    public void changeColor(String bc, String color);
  }
  
  public ColorSelector(UIObject parent, ICallback callback) {
    this.callback = callback;
    setWidget(uiBinder.createAndBindUi(this));

    String[] colors0 = new String[] {"#DEE5F2", "#5A6FB0", 
                                    "#E0ECFF", "#2078FF", 
                                    "#DFE2FF", "#0000D7",
                                    "#E0D5F9", "#5243C6", 
                                    "#FDE9F4", "#8558A0", 
                                    "#FFE3E3", "#CC3A87"};

    String[] colors2 = new String[] {"#ec7000", "#FFEDB9", 
                                     "#B36D00", "#FAD993", 
                                     "#AB8B00", "#F3E593",
                                     "#636330", "#FFFFD1", 
                                     "#64992C", "#F9FCC6", 
                                     "#006633", "#F1F2C4"};

    String[] colors3 = new String[] {"#5A6986", "#DEDFD2", 
                                    "#206CFF", "#FAD993", 
                                    "#0000CC", "#FFFFFF",
                                    "#5229A3", "#E0D5EB", 
                                    "#854F61", "#FDE9DF", 
                                    "#CC0000", "#FFE3CC"};

    String[] colors1 = new String[] {"#4e5861", "white", 
        "#ffff00", "black", 
        "#00ff00", "black",
        "#0000ff", "black", 
        "#0080ff", "black", 
        "#ff0000", "black"};

    String[][] samplesets = new String[][]{colors0, colors2, colors3, colors1};
    
    int row = 0;
    int column = 0;
    
    for (String[] sample : samplesets) {
      for (int i = 0; i < sample.length; i += 2) {
        final Label color = new Label();
        color.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            String b = color.getElement().getStyle().getBackgroundColor();
            String c = color.getElement().getStyle().getColor();
            ColorSelector.this.callback.changeColor(b, c);
            popup.hide();
          }
        });
//        color.setText("c");
//        color.setHorizontalAlignment(Label.ALIGN_CENTER);
//        color.setStyleName(style.text());
//        color.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
//        color.getElement().getStyle().
//        color.getElement().getStyle().setMarginBottom(3, Unit.PX);
//        color.setWidth("15px");
//        color.setStyleName(style.text());
        color.setSize("12px", "12px");
        color.getElement().getStyle().setBackgroundColor(sample[i]);
        color.getElement().getStyle().setColor(sample[i+1]);
        flexTable.setWidget(row, column++, color);
      }
      ++row;
      column = 0;
    }
    
    popup.setPopupPosition(parent.getAbsoluteLeft(), parent.getAbsoluteTop());
    popup.show();
  }
  
//  @UiHandler("c1")
//  void onClickC1(ClickEvent e) {
//    Widget w = (Widget) e.getSource();
//    callback.changeColor(w.getElement().getStyle().getBackgroundColor());
//    popup.hide();
//  }
//  @UiHandler("c2")
//  void onClickC2(ClickEvent e) {
//    popup.hide();
//  }
//  @UiHandler("c3")
//  void onClickC3(ClickEvent e) {
//    popup.hide();
//  }
//  @UiHandler("c4")
//  void onClickC4(ClickEvent e) {
//    popup.hide();
//  }
//  @UiHandler("c5")
//  void onClickC5(ClickEvent e) {
//    popup.hide();
//  }
//  @UiHandler("c6")
//  void onClickC6(ClickEvent e) {
//    popup.hide();
//  }
  
}
