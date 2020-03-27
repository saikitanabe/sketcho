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

import com.google.gwt.core.client.GWT;

public class PointerEventsSupport {

    public static boolean initDone = false;

    static final PointerEventsSupport impl = GWT
            .create(ModernSupportImpl.class);

    public PointerEventsSupport() {
    }

    public String getNativeEventName(Events events) {
        return events.toString().toLowerCase();
    }

    public static boolean isSupported() {
        return impl.supports();
    }

    boolean supports() {
        return false;
    }

    /**
     * Initializes pointer event support. Most commonly this should not be
     * needed if the entry point of this module gets executed early enough. If
     * there are ordering issues in more complex GWT projects, this method can
     * be called to ensure pointer events setup in earlier phase.
     */
    public static void init() {
        if (!initDone) {
            impl.doInit();
            initDone = true;
        }
    }

    void doInit() {
    };

}