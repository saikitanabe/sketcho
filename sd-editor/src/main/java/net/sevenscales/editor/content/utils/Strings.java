package net.sevenscales.editor.content.utils;
public class Strings {

    public static String format(final String format, final Object... args) {
        String retVal = format;
        for (final Object current : args) {
            retVal = retVal.replaceFirst("[%][s]", current.toString());
        }
        return retVal;
    }

}