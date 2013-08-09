package net.sevenscales.sketcho.client.uicomponents;

import java.util.List;

import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.sketcho.client.uicomponents.ConfirmationDialog.ICallback;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class LabelWidget extends SimplePanel {
  public class HTMLEventWidget extends HTML {
    public HTMLEventWidget(String string) {
      super(string);
    }
    public void addMouseEnterHandler(MouseOverHandler handler) {
      addDomHandler(handler, MouseOverEvent.getType());
    }
//    public void addMouseOutHandler(MouseOutHandler handler) {
//      addDomHandler(handler, MouseOutEvent.getType());
//    }
  }
  
  public interface LabelClickHandler {
    public void onClick(ILabel label, IPage page);
  }
  
  private LabelClickHandler clickHandler;
  public LabelWidget(List<ILabel> list, final IPage page, LabelClickHandler clickHandler) {
    this.clickHandler = clickHandler;
    setStyleName("LabelWidget");
    final HorizontalEventPanel panel = new HorizontalEventPanel();
    panel.setSpacing(0);
    
    for (final ILabel l : list) {
      final HTMLEventWidget label = new HTMLEventWidget(l.getValue());
      label.getElement().getStyle().setPadding(2, Unit.PX);
//      label.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//      label.getElement().getStyle().setBorderWidth(1, Unit.PX);
//      label.getElement().getStyle().setBorderColor("black");
//      label.getElement().getStyle().setMargin(3, Unit.PX);
      label.setTitle("Click to remove label");
      panel.add(label);
      label.addMouseEnterHandler(new MouseOverHandler() {
        public void onMouseOver(MouseOverEvent event) {
          label.getElement().getStyle().setBorderColor("blue");
          label.getElement().getStyle().setOpacity(0.7);
        }
      });
      label.addMouseOutHandler(new MouseOutHandler() {
        public void onMouseOut(MouseOutEvent event) {
          label.getElement().getStyle().setBorderColor("black");
          label.getElement().getStyle().setOpacity(1);
        }
      });
      
      if (l.getBackgroundColor() != null) {
        label.getElement().getStyle().setBackgroundColor(l.getBackgroundColor());
      }
      if (l.getTextColor() != null) {
        label.getElement().getStyle().setColor(l.getTextColor());
      }
      
      label.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          DOM.eventCancelBubble(Event.as(event.getNativeEvent()), true);
          ConfirmationDialog cd = new ConfirmationDialog(new ICallback() {
            public void doit() {
              LabelWidget.this.clickHandler.onClick(l, page);
            }
            public void canceled() {
              label.getElement().getStyle().setOpacity(1);
            }
          }, "Remove label from sketch?");
        }
      });

    }

    setWidget(panel);
  }
}
