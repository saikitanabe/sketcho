/**
 * 
 */
package net.sevenscales.sketcho.client.uicomponents;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class HorizontalEventPanel extends HorizontalPanel implements HasClickHandlers {
  public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
    return addDomHandler(clickHandler, ClickEvent.getType());
  }
}