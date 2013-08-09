/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acegisecurity.captcha;

/**
 * DOCUMENT ME!
 *
 * @author marc antoine Garrigue
 * @version $Id: MockCaptchaServiceProxy.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class MockCaptchaServiceProxy implements CaptchaServiceProxy {
    //~ Instance fields ================================================================================================

    public boolean hasBeenCalled = false;
    public boolean valid = false;

    //~ Methods ========================================================================================================

    public boolean validateReponseForId(String id, Object response) {
        hasBeenCalled = true;

        return valid;
    }
}
