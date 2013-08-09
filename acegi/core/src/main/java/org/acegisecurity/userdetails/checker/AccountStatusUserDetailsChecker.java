package org.acegisecurity.userdetails.checker;


import org.springframework.context.support.MessageSourceAccessor;

import org.acegisecurity.LockedException;
import org.acegisecurity.CredentialsExpiredException;
import org.acegisecurity.AccountExpiredException;
import org.acegisecurity.DisabledException;
import org.acegisecurity.AcegiMessageSource;
import org.acegisecurity.userdetails.UserDetailsChecker;
import org.acegisecurity.userdetails.UserDetails;

/**
 * @author Luke Taylor
 * @version $Id: AccountStatusUserDetailsChecker.java 2654 2008-02-18 20:44:09Z luke_t $
 * @since 1.0.7
 */
public class AccountStatusUserDetailsChecker implements UserDetailsChecker {

    protected MessageSourceAccessor messages = AcegiMessageSource.getAccessor();

    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new LockedException(messages.getMessage("UserDetailsService.locked", "User account is locked"), user);
        }

        if (!user.isEnabled()) {
            throw new DisabledException(messages.getMessage("UserDetailsService.disabled", "User is disabled"), user);
        }

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException(messages.getMessage("UserDetailsService.expired",
                    "User account has expired"), user);
        }

        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(messages.getMessage("UserDetailsService.credentialsExpired",
                    "User credentials have expired"), user);
        }
    }
}
