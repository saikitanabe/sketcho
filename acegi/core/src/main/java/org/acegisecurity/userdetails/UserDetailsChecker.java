package org.acegisecurity.userdetails;

/**
 * @author Luke Taylor
 * @version $Id: UserDetailsChecker.java 2648 2008-02-18 12:21:29Z luke_t $
 * @since 1.0.7
 */
public interface UserDetailsChecker {
    void check(UserDetails toCheck);
}
