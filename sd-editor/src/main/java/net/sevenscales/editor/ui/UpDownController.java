package net.sevenscales.editor.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UpDownController extends Composite {

  private static ImageButtonUiBinder uiBinder = GWT.create(ImageButtonUiBinder.class);
  
  @UiField HTML up;
  @UiField HTML down;

  private TextBox valueField;

  private ISizeCallback callback;
//  private PushButton upb;
  
//  public class ImageButton extends HTML implements HasClickHandlers {
//    
//  }

  interface ImageButtonUiBinder extends UiBinder<Widget, UpDownController> {
  }
  
  public interface ISizeCallback {
    void setSize();
  }

  public UpDownController(TextBox valueField, ISizeCallback callback) {
    this.valueField = valueField;
    this.callback = callback;
    initWidget(uiBinder.createAndBindUi(this));

    up.addStyleName("up-img");
    down.addStyleName("down-img");
//    up.getElement().getStyle().setBackgroundImage("images/up.png");
    
//    background-image: url("images/down.png");

//    Image upimg = new Image(Resources.INSTANCE.up());
//    upb.set
  }
  
  @UiHandler("up")
  public void upClick(ClickEvent event) {
    Integer value = Integer.valueOf(valueField.getText());
    value += 15;
    if (value >= 100) {
      valueField.setText(value.toString());
      callback.setSize();
    }
  }
  
  @UiHandler("down")
  public void downClick(ClickEvent event) {
    Integer value = Integer.valueOf(valueField.getText());
    value -= 15;
    
    if (value >= 100) {
      valueField.setText(value.toString());
      callback.setSize();
    }
  }

}
