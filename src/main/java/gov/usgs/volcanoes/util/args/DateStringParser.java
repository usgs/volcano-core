package gov.usgs.volcanoes.util.args;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;
import com.martiansoftware.jsap.stringparsers.StringStringParser;

/**
 * A {@link com.martiansoftware.jsap.StringParser} for parsing Strings.  This is the simplest possible
 * StringParser, simply returning
 * the specified argument in all cases.  This class never throws a
 * ParseException under any circumstances.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see com.martiansoftware.jsap.StringParser
 * @see java.lang.String
 */
public class DateStringParser extends StringParser {
    private static final DateStringParser INSTANCE = new DateStringParser();    

    public static final String INPUT_TIME_FORMAT = "yyyyMMddHHmm";
    private SimpleDateFormat format;

    /** Returns a {@link StringStringParser}.
     * 
     * <p>Convenient access to the only instance returned by
     * this method is available through
     * {@link com.martiansoftware.jsap.JSAP#STRING_PARSER}.
     *  
     * @return a {@link StringStringParser}.
     */

    public static DateStringParser getParser() {
        return INSTANCE;
    }

    /**
     * Creates a new StringStringParser.
     */
    private DateStringParser() {
        super();
        format = new SimpleDateFormat(INPUT_TIME_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Returns the specified argument as a String.
     *
     * @param arg the argument to parse
     * @return the specified argument as a String.
     * @throws ParseException 
     * @see java.lang.String
     * @see com.martiansoftware.jsap.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        try {
            return format.parse(arg);
        } catch (java.text.ParseException e) {
            throw new ParseException("Can't parse " + arg);
        }
    }
}