package net.sevenscales.sketcho.client.uicomponents;

import net.sevenscales.editor.diagram.utils.UiUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class StyleButton extends Composite implements HasClickHandlers {
  interface MyUiBinder extends UiBinder<Widget, StyleButton> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
  
  public interface MyStyle extends CssResource {
    String floatleft();
    String floatright();
  }
  
  @UiField PushButton pushButton;
  @UiField MyStyle style;

  public StyleButton(final Image icon, String text) {
    
//    HorizontalPanel panel = new HorizontalPanel();
//    Label label = new Label(text);
//    
////    if (UiUtils.isIE()) {
//      icon.getElement().getStyle().setProperty("float", "left");
//      icon.getElement().getStyle().setMarginRight(3, Unit.PX);
////    } else {
//      label.getElement().getStyle().setProperty("float", "right");
////      label.getElement().getStyle().setMarginLeft(3, Unit.PX);
////    }
//    label.setWordWrap(false);
//
//    label.getElement().getStyle().setFontSize(12, Unit.PX);
//    panel.add(icon);
//    panel.add(label);
//    this.button = new PushButton(icon);
////    DOM.appendChild(button.getElement(), panel.getElement());
//    button.setHTML(panel.getElement().getInnerHTML());
    Label label = new Label(text);
//    Element div = Document.get().createDivElement();
//    div.appendChild(icon.getElement());
    
    FlowPanel panel = new FlowPanel();
    panel.add(icon);
    panel.add(label);
    
//    div.getElement().getStyle().setProperty("float", "left");
//    label.getElement().getStyle().setProperty("float", "left");
    initWidget(uiBinder.createAndBindUi(this));
    
    if (UiUtils.isIE()) {
//      icon.getElement().getStyle().setProperty("float", "left");
      icon.addStyleName(style.floatleft());
      icon.getElement().getStyle().setMarginRight(3, Unit.PX);
    } else if (UiUtils.isSafari()) {
//      label.addStyleName(style.floatright());
      icon.getElement().getStyle().setProperty("float", "left");
      label.getElement().getStyle().setProperty("float", "left");

      label.getElement().getStyle().setMarginLeft(3, Unit.PX);
      DeferredCommand.addCommand(new Command() {
        @Override
        public void execute() {
          pushButton.setHeight(icon.getHeight()+"px");
        }
      });
    } else { // firefox...
//      label.getElement().getStyle().setProperty("float", "right");
      icon.addStyleName(style.floatleft());
      label.addStyleName(style.floatright());
      label.getElement().getStyle().setMarginLeft(3, Unit.PX);
      DeferredCommand.addCommand(new Command() {
        @Override
        public void execute() {
          pushButton.setHeight(icon.getHeight()+"px");
        }
      });
    }
    label.getElement().getStyle().setFontSize(12, Unit.PX);

    pushButton.setHTML(panel.getElement().getInnerHTML());
//    pushButton.setHeight(icon.getHeight()+"px");
//    pushButton.setHTML(panel.getElement().getInnerHTML());
//    textSpan.setInnerText(text);
  }

  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return pushButton.addClickHandler(handler);
  }
}
