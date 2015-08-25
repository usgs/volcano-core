package gov.usgs.volcanoes.util.args;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

public class DateStringParser extends StringParser {

    private SimpleDateFormat format;
    
    public DateStringParser(String inputFormat) {
        format = new SimpleDateFormat(inputFormat);
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
