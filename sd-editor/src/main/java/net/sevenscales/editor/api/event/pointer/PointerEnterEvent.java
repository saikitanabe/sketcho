/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sevenscales.editor.api.event.pointer;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * Represents a native PointerEnterEvent event.
 */
public class PointerEnterEvent extends PointerEvent<PointerEnterHandler> {

  /**
   * Event type for PointerEnterEvent. Represents the meta-data associated with
   * this event.
   */
  private static final Type<PointerEnterHandler> TYPE = new Type<PointerEnterHandler>(
      Events.PointerEnter.getNativeEventName(), new PointerEnterEvent());

  /**
   * Gets the event type associated with PointerEnterEvent.
   *
   * @return the handler type
   */
  public static Type<PointerEnterHandler> getType() {
    return TYPE;
  }

  /**
   * Protected constructor, use
   * {@link DomEvent#fireNativeEvent(com.google.gwt.dom.client.NativeEvent, com.google.gwt.event.shared.HasHandlers)}
   * to fire pointer down events.
   */
  protected PointerEnterEvent() {
  }

  @Override
  public final Type<PointerEnterHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(PointerEnterHandler handler) {
    handler.onPointerEnter(this);
  }

}
