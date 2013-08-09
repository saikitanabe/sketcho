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

import org.springframework.beans.factory.InitializingBean;

import org.springframework.util.Assert;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Controller to delete a contact.
 *
 * @author Ben Alex
 * @version $Id: DeleteController.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class DeleteController implements Controller, InitializingBean {
    //~ Instance fields ================================================================================================

    private ContactManager contactManager;

    //~ Methods ========================================================================================================

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(contactManager, "A ContactManager implementation is required");
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        int id = RequestUtils.getRequiredIntParameter(request, "contactId");
        Contact contact = contactManager.getById(new Long(id));
        contactManager.delete(contact);

        return new ModelAndView("deleted", "contact", contact);
    }

    public void setContactManager(ContactManager contact) {
        this.contactManager = contact;
    }
}
