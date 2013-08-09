/**
 * Dual licensed under the Apache License 2.0 and the MIT license.
 * $Revision: 705 $ $Date: 2009-07-21 13:31:39 +0300 (Ti, 21 Hei 2009) $
 */

if (typeof dojo!="undefined")
{
    dojo.provide("org.cometd.TimeStampExtension");
}

/**
 * The timestamp extension adds the optional timestamp field to all outgoing messages.
 */

org.cometd.TimeStampExtension = function()
{
    this.outgoing = function(message)
    {
        message.timestamp = new Date().toUTCString();
        return message;
    };
};
