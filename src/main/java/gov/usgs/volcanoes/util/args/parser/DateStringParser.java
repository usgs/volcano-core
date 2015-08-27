package gov.usgs.volcanoes.util.args.parser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

/**
 * 
 * @author Tom Parker
 * 
 *         I waive copyright and related rights in the this work worldwide
 *         through the CC0 1.0 Universal public domain dedication.
 *         https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
public class DateStringParser extends StringParser {

    private SimpleDateFormat format;
    
    public DateStringParser(String inputFormat) {
        format = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public Object parse(String arg) throws ParseException {
        Date result = null;
        try {
            result = format.parse(arg);
        } catch (java.text.ParseException e) {
            throw new ParseException("Unable to convert '" + arg + "' to a Date.");
        }
        return result;
    }
}
