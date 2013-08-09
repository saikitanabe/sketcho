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

package org.acegisecurity.annotation.test;

/**
 * An extended version of <code>Entity</code>.
 *
 * @author Ben Alex
 * @version $Id: Person.java 1521 2006-05-29 22:22:50Z benalex $
 */
public class Person extends Entity {
    //~ Instance fields ================================================================================================

    private boolean active = true;

    //~ Constructors ===================================================================================================

    public Person(String name) {
        super(name);
    }

    //~ Methods ========================================================================================================

    void deactive() {
        this.active = true;
    }

    public boolean isActive() {
        return this.active;
    }
}
