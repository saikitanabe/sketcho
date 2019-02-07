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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.impl.DOMImplStandard;

public class ModernSupportImpl extends PointerEventsSupport {
    
    @Override
    protected boolean supports() {
        return pointerEventsSupported();
    }

    private native boolean pointerEventsSupported()/*-{
        return $wnd.PointerEvent;
    }-*/;

    void doInit() {
        if (supports()) {
          JavaScriptObject eventDispatcherMapExtensions = JavaScriptObject
            .createObject();
          JavaScriptObject captureEventDispatcherMapExtensions = JavaScriptObject
            .createObject();
          for (Events e : Events.values()) {
            addEventDispatcher(e.getNativeEventName(), eventDispatcherMapExtensions);
            getPointerEventCaptureDispatchers(e.getNativeEventName(),
                    captureEventDispatcherMapExtensions);
          }
          DOMImplStandard
            .addBitlessEventDispatchers(eventDispatcherMapExtensions);
          DOMImplStandard
            .addCaptureEventDispatchers(captureEventDispatcherMapExtensions);
        }
    }

    private static native void addEventDispatcher(String eventName,
            JavaScriptObject jso)
    /*-{
        jso[eventName] = @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent(*);
    }-*/;

    private static native void getPointerEventCaptureDispatchers(
            String eventName, JavaScriptObject jso)
    /*-{
        jso[eventName] = @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent(*);
    }-*/;

}
