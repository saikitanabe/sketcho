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
package sample.contact;

import org.acegisecurity.acl.basic.SimpleAclEntry;


/**
 * Model object for add permission use case.
 *
 * @author Ben Alex
 * @version $Id: AddPermission.java 1754 2006-11-17 02:01:21Z benalex $
 */
public class AddPermission {
    //~ Instance fields ================================================================================================

    public Contact contact;
    public Integer permission = new Integer(SimpleAclEntry.READ);
    public String recipient;

    //~ Methods ========================================================================================================

    public Contact getContact() {
        return contact;
    }

    public Integer getPermission() {
        return permission;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
