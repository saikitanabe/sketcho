package sample.dms.secured;

import sample.dms.DocumentDao;

/**
 * Extends the {@link DocumentDao} and introduces ACL-related methods.
 * 
 * @author Ben Alex
 * @version $Id: SecureDocumentDao.java 1772 2006-12-17 00:54:13Z benalex $
 *
 */
public interface SecureDocumentDao extends DocumentDao {
    /**
     * @return all the usernames existing in the system.
     */
    public String[] getUsers();
}
