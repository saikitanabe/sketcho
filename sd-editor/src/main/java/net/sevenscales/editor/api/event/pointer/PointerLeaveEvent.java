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
 * Represents a native PointerLeaveEvent event.
 */
public class PointerLeaveEvent extends PointerEvent<PointerLeaveHandler> {

  /**
   * Event type for PointerLeaveEvent. Represents the meta-data associated with
   * this event.
   */
  private static final Type<PointerLeaveHandler> TYPE = new Type<PointerLeaveHandler>(
      Events.PointerLeave.getNativeEventName(), new PointerLeaveEvent());

  /**
   * Gets the event type associated with PointerLeaveEvent.
   *
   * @return the handler type
   */
  public static Type<PointerLeaveHandler> getType() {
    return TYPE;
  }

  /**
   * Protected constructor, use
   * {@link DomEvent#fireNativeEvent(com.google.gwt.dom.client.NativeEvent, com.google.gwt.event.shared.HasHandlers)}
   * to fire pointer down events.
   */
  protected PointerLeaveEvent() {
  }

  @Override
  public final Type<PointerLeaveHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(PointerLeaveHandler handler) {
    handler.onPointerLeave(this);
  }

}
