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

package org.acegisecurity.ldap.ppolicy;

import netscape.ldap.ber.stream.BERChoice;
import netscape.ldap.ber.stream.BERElement;
import netscape.ldap.ber.stream.BEREnumerated;
import netscape.ldap.ber.stream.BERInteger;

//import com.novell.ldap.asn1.LBERDecoder;
//import com.novell.ldap.asn1.ASN1Sequence;
//import com.novell.ldap.asn1.ASN1Tagged;
//import com.novell.ldap.asn1.ASN1OctetString;
import netscape.ldap.ber.stream.BERSequence;
import netscape.ldap.ber.stream.BERTag;
import netscape.ldap.ber.stream.BERTagDecoder;

import org.acegisecurity.ldap.LdapDataAccessException;
import org.acegisecurity.ldap.ppolicy.PasswordPolicyControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Represent the response control received when a <tt>PasswordPolicyControl</tt> is used when binding to a
 * directory. Currently tested with the OpenLDAP 2.3.19 implementation of the LDAP Password Policy Draft.  It extends
 * the request control with the control specific data. This is accomplished by the properties timeBeforeExpiration,
 * graceLoginsRemaining and errorCodes. getEncodedValue returns the unchanged value of the response control as a byte
 * array.
 *
 * @author Stefan Zoerner
 * @author Luke Taylor
 * @version $Id: PasswordPolicyResponseControl.java 1496 2006-05-23 13:38:33Z benalex $
 *
 * @see org.acegisecurity.ldap.ppolicy.PasswordPolicyControl
 * @see <a href="http://www.ibm.com/developerworks/tivoli/library/t-ldap-controls/">Stefan Zoerner's IBM developerworks
 *      article on LDAP controls.</a>
 */
public class PasswordPolicyResponseControl extends PasswordPolicyControl {
    //~ Static fields/initializers =====================================================================================

    private static final Log logger = LogFactory.getLog(PasswordPolicyResponseControl.class);
    public static final int ERROR_NONE = -1;
    public static final int ERROR_PASSWORD_EXPIRED = 0;
    public static final int ERROR_ACCOUNT_LOCKED = 1;
    public static final int WARNINGS_DEFAULT = -1;
    private static final String[] errorText = {
            "password expired", "account locked", "change after reset", "password mod not allowed",
            "must supply old password", "invalid password syntax", "password too short", "password too young",
            "password in history"
        };

    //~ Instance fields ================================================================================================

    private byte[] encodedValue;
    private int errorCode = ERROR_NONE;
    private int graceLoginsRemaining = WARNINGS_DEFAULT;
    private int timeBeforeExpiration = WARNINGS_DEFAULT;

    //~ Constructors ===================================================================================================

    /**
     * Decodes the Ber encoded control data. The ASN.1 value of the control data is:<pre>
     *    PasswordPolicyResponseValue ::= SEQUENCE {       warning [0] CHOICE {
     *           timeBeforeExpiration [0] INTEGER (0 .. maxInt),
     *           graceAuthNsRemaining [1] INTEGER (0 .. maxInt) } OPTIONAL,       error   [1] ENUMERATED {
     *           passwordExpired             (0),          accountLocked               (1),
     *           changeAfterReset            (2),          passwordModNotAllowed       (3),
     *           mustSupplyOldPassword       (4),          insufficientPasswordQuality (5),
     *           passwordTooShort            (6),          passwordTooYoung            (7),
     *           passwordInHistory           (8) } OPTIONAL }</pre>
     *
     */    
    public PasswordPolicyResponseControl(byte[] encodedValue) {
        this.encodedValue = encodedValue;

        //PPolicyDecoder decoder = new JLdapDecoder();
        PPolicyDecoder decoder = new NetscapeDecoder();

        try {
            decoder.decode();
        } catch (IOException e) {
            throw new LdapDataAccessException("Failed to parse control value", e);
        }
    }

    //~ Methods ========================================================================================================

    /**
     * Returns the unchanged value of the response control.  Returns the unchanged value of the response
     * control as byte array.
     *
     * @return DOCUMENT ME!
     */
    public byte[] getEncodedValue() {
        return encodedValue;
    }

    /**
     * Returns the error code, or ERROR_NONE, if no error is present.
     *
     * @return the error code (0-8), or ERROR_NONE
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the graceLoginsRemaining.
     *
     * @return Returns the graceLoginsRemaining.
     */
    public int getGraceLoginsRemaining() {
        return graceLoginsRemaining;
    }

    /**
     * Returns the timeBeforeExpiration.
     *
     * @return Returns the time before expiration in seconds
     */
    public int getTimeBeforeExpiration() {
        return timeBeforeExpiration;
    }

    /**
     * Checks whether an error is present.
     *
     * @return true, if an error is present
     */
    public boolean hasError() {
        return this.getErrorCode() != ERROR_NONE;
    }

    /**
     * Checks whether a warning is present.
     *
     * @return true, if a warning is present
     */
    public boolean hasWarning() {
        return (graceLoginsRemaining != WARNINGS_DEFAULT) || (timeBeforeExpiration != WARNINGS_DEFAULT);
    }

    public boolean isExpired() {
        return errorCode == ERROR_PASSWORD_EXPIRED;
    }

    /**
     * Determines whether an account locked error has been returned.
     *
     * @return true if the account is locked.
     */
    public boolean isLocked() {
        return errorCode == ERROR_ACCOUNT_LOCKED;
    }

    /**
     * Create a textual representation containing error and warning messages, if any are present.
     *
     * @return error and warning messages
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("PasswordPolicyResponseControl");

        if (hasError()) {
            sb.append(", error: ").append(errorText[errorCode]);
        }

        if (graceLoginsRemaining != WARNINGS_DEFAULT) {
            sb.append(", warning: ").append(graceLoginsRemaining).append(" grace logins remain");
        }

        if (timeBeforeExpiration != WARNINGS_DEFAULT) {
            sb.append(", warning: time before expiration is ").append(timeBeforeExpiration);
        }

        if (!hasError() && !hasWarning()) {
            sb.append(" (no error, no warning)");
        }

        return sb.toString();
    }

    //~ Inner Interfaces ===============================================================================================

    private interface PPolicyDecoder {
        void decode() throws IOException;
    }

    //~ Inner Classes ==================================================================================================
    
    /**
     * Decoder based on Netscape ldapsdk library
     */
    private class NetscapeDecoder implements PPolicyDecoder {
        public void decode() throws IOException {
            int[] bread = {0};
            BERSequence seq = (BERSequence) BERElement.getElement(new SpecificTagDecoder(),
                    new ByteArrayInputStream(encodedValue), bread);

            int size = seq.size();

            if (logger.isDebugEnabled()) {
                logger.debug("PasswordPolicyResponse, ASN.1 sequence has " + size + " elements");
            }

            for (int i = 0; i < seq.size(); i++) {
                BERTag elt = (BERTag) seq.elementAt(i);

                int tag = elt.getTag() & 0x1F;

                if (tag == 0) {
                    BERChoice warning = (BERChoice) elt.getValue();

                    BERTag content = (BERTag) warning.getValue();
                    int value = ((BERInteger) content.getValue()).getValue();

                    if ((content.getTag() & 0x1F) == 0) {
                        timeBeforeExpiration = value;
                    } else {
                        graceLoginsRemaining = value;
                    }
                } else if (tag == 1) {
                    BEREnumerated error = (BEREnumerated) elt.getValue();
                    errorCode = error.getValue();
                }
            }
        }

        class SpecificTagDecoder extends BERTagDecoder {
            /** Allows us to remember which of the two options we're decoding */
            private Boolean inChoice = null;

            public BERElement getElement(BERTagDecoder decoder, int tag, InputStream stream, int[] bytesRead,
                boolean[] implicit) throws IOException {
                tag &= 0x1F;
                implicit[0] = false;

                if (tag == 0) {
                    // Either the choice or the time before expiry within it
                    if (inChoice == null) {
                        setInChoice(true);

                        // Read the choice length from the stream (ignored)
                        BERElement.readLengthOctets(stream, bytesRead);

                        int[] componentLength = new int[1];
                        BERElement choice = new BERChoice(decoder, stream, componentLength);
                        bytesRead[0] += componentLength[0];

                        // inChoice = null;
                        return choice;
                    } else {
                        // Must be time before expiry
                        return new BERInteger(stream, bytesRead);
                    }
                } else if (tag == 1) {
                    // Either the graceLogins or the error enumeration.
                    if (inChoice == null) {
                        // The enumeration
                        setInChoice(false);

                        return new BEREnumerated(stream, bytesRead);
                    } else {
                        if (inChoice.booleanValue()) {
                            // graceLogins
                            return new BERInteger(stream, bytesRead);
                        }
                    }
                }

                throw new LdapDataAccessException("Unexpected tag " + tag);
            }

            private void setInChoice(boolean inChoice) {
                this.inChoice = new Boolean(inChoice);
            }
        }
    }

/** Decoder based on the OpenLDAP/Novell JLDAP library */

//    private class JLdapDecoder implements PPolicyDecoder {
//
//        public void decode() throws IOException {
//
//            LBERDecoder decoder = new LBERDecoder();
//
//            ASN1Sequence seq = (ASN1Sequence)decoder.decode(encodedValue);
//
//            if(seq == null) {
//
//            }
//
//            int size = seq.size();
//
//            if(logger.isDebugEnabled()) {
//                logger.debug("PasswordPolicyResponse, ASN.1 sequence has " +
//                        size + " elements");
//            }
//
//            for(int i=0; i < size; i++) {
//
//                ASN1Tagged taggedObject = (ASN1Tagged)seq.get(i);
//
//                int tag = taggedObject.getIdentifier().getTag();
//
//                ASN1OctetString value = (ASN1OctetString)taggedObject.taggedValue();
//                byte[] content = value.byteValue();
//
//                if(tag == 0) {
//                    parseWarning(content, decoder);
//
//                } else if(tag == 1) {
//                    // Error: set the code to the value
//                    errorCode = content[0];
//                }
//            }
//        }
//
//        private void parseWarning(byte[] content, LBERDecoder decoder) {
//            // It's the warning (choice). Parse the number and set either the
//            // expiry time or number of logins remaining.
//            ASN1Tagged taggedObject = (ASN1Tagged)decoder.decode(content);
//            int contentTag = taggedObject.getIdentifier().getTag();
//            content = ((ASN1OctetString)taggedObject.taggedValue()).byteValue();
//            int number;
//
//            try {
//                number = ((Long)decoder.decodeNumeric(new ByteArrayInputStream(content), content.length)).intValue();
//            } catch(IOException e) {
//                throw new LdapDataAccessException("Failed to parse number ", e);
//            }
//
//            if(contentTag == 0) {
//                timeBeforeExpiration = number;
//            } else if (contentTag == 1) {
//                graceLoginsRemaining = number;
//            }
//        }
//    }
}
