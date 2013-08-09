package net.sevenscales.sketcho.client.uicomponents;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmationDialog extends SimplePanel {
  interface MyUiBinder extends UiBinder<Widget, ConfirmationDialog> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
  
  @UiField DialogBox dialog;
  @UiField VerticalPanel verticalPanel;
  @UiField HorizontalPanel buttonPanel;
  @UiField Button cancelButton;
  @UiField Button okButton;
  @UiField Label text;
  private ICallback callback;

  public interface ICallback {
    public void doit();
    public void canceled();
  }
  
  public ConfirmationDialog(ICallback callback, String text) {
    this.callback = callback;
    setWidget(uiBinder.createAndBindUi(this));
    
    verticalPanel.setCellHorizontalAlignment(buttonPanel, VerticalPanel.ALIGN_RIGHT);
    this.text.setText(text);
    dialog.center();
    dialog.show();
  }
  
  @UiHandler("cancelButton")
  public void cancel(ClickEvent event) {
    dialog.hide();
    callback.canceled();
  }
  @UiHandler("okButton")
  public void ok(ClickEvent event) {
    dialog.hide();
    callback.doit();
  }
  @UiHandler("dialog")
  public void onClose(CloseEvent<PopupPanel> event) {
    callback.canceled();
  }
  
}
