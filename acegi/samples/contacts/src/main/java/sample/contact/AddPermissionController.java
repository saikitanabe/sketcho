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

import org.acegisecurity.acls.Permission;
import org.acegisecurity.acls.domain.BasePermission;
import org.acegisecurity.acls.sid.PrincipalSid;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.dao.DataAccessException;

import org.springframework.util.Assert;

import org.springframework.validation.BindException;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Controller for adding an ACL permission.
 *
 * @author Ben Alex
 * @version $Id: AddPermissionController.java 1754 2006-11-17 02:01:21Z benalex $
 */
public class AddPermissionController extends SimpleFormController implements InitializingBean {
    //~ Instance fields ================================================================================================

    private ContactManager contactManager;

    //~ Methods ========================================================================================================

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(contactManager, "A ContactManager implementation is required");
    }

    protected ModelAndView disallowDuplicateFormSubmission(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        BindException errors = new BindException(formBackingObject(request), getCommandName());
        errors.reject("err.duplicateFormSubmission", "Duplicate form submission. *");

        return showForm(request, response, errors);
    }

    protected Object formBackingObject(HttpServletRequest request)
        throws Exception {
        int contactId = RequestUtils.getRequiredIntParameter(request, "contactId");

        Contact contact = contactManager.getById(new Long(contactId));

        AddPermission addPermission = new AddPermission();
        addPermission.setContact(contact);

        return addPermission;
    }

    protected ModelAndView handleInvalidSubmit(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return disallowDuplicateFormSubmission(request, response);
    }

    private Map listPermissions(HttpServletRequest request) {
        Map map = new LinkedHashMap();
        map.put(new Integer(BasePermission.ADMINISTRATION.getMask()),
            getApplicationContext().getMessage("select.administer", null, "Administer", request.getLocale()));
        map.put(new Integer(BasePermission.READ.getMask()),
            getApplicationContext().getMessage("select.read", null, "Read", request.getLocale()));
        map.put(new Integer(BasePermission.DELETE.getMask()),
            getApplicationContext().getMessage("select.delete", null, "Delete", request.getLocale()));

        return map;
    }

    private Map listRecipients(HttpServletRequest request) {
        Map map = new LinkedHashMap();
        map.put("",
            getApplicationContext().getMessage("select.pleaseSelect", null, "-- please select --", request.getLocale()));

        Iterator recipientsIter = contactManager.getAllRecipients().iterator();

        while (recipientsIter.hasNext()) {
            String recipient = (String) recipientsIter.next();
            map.put(recipient, recipient);
        }

        return map;
    }

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
        BindException errors) throws Exception {
        AddPermission addPermission = (AddPermission) command;

        PrincipalSid sid = new PrincipalSid(addPermission.getRecipient());
        Permission permission = BasePermission.buildFromMask(addPermission.getPermission().intValue());

        try {
            contactManager.addPermission(addPermission.getContact(), sid, permission);
        } catch (DataAccessException existingPermission) {
            existingPermission.printStackTrace();
            errors.rejectValue("recipient", "err.recipientExistsForContact", "Addition failure.");

            return showForm(request, response, errors);
        }

        return new ModelAndView(new RedirectView(getSuccessView()));
    }

    protected Map referenceData(HttpServletRequest request)
        throws Exception {
        Map model = new HashMap();
        model.put("recipients", listRecipients(request));
        model.put("permissions", listPermissions(request));

        return model;
    }

    public void setContactManager(ContactManager contact) {
        this.contactManager = contact;
    }
}
