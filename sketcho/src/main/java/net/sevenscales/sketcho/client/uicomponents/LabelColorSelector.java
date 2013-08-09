package net.sevenscales.sketcho.client.uicomponents;

import net.sevenscales.sketcho.client.uicomponents.ColorSelector.ICallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class LabelColorSelector extends SimplePanel implements ICallback {
  interface MyUiBinder extends UiBinder<Widget, LabelColorSelector> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
  
  @UiField Label pushLabel;

  private ICallback callback;
  

//  private DeckPanel deck = new DeckPanel();
  public LabelColorSelector(String backgroundColor, String textColor, ICallback callback) {
    this.callback = callback;
    setWidget(uiBinder.createAndBindUi(this));
    if (backgroundColor != null) {
      pushLabel.getElement().getStyle().setBackgroundColor(backgroundColor);
    }
    if (textColor != null) {
      pushLabel.getElement().getStyle().setColor(textColor);
    }
//    pushLabel.getElement().getStyle().setBorderWidth(1, Unit.PX);
//    pushLabel.getElement().getStyle().setBorderColor("black");
//    pushLabel.getElement().getStyle().setBackgroundColor("SkyBlue");
  }
  
  @UiHandler("pushLabel")
  void onClick(ClickEvent e) {
    new ColorSelector(this, this);
  }

  public void changeColor(String bc, String color) {
    callback.changeColor(bc, color);
    pushLabel.getElement().getStyle().setBackgroundColor(bc);
    pushLabel.getElement().getStyle().setColor(color);
  }
}
