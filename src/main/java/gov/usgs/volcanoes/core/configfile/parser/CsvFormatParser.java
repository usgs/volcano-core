/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.configfile.parser;

import gov.usgs.volcanoes.core.configfile.Parser;

import org.apache.commons.csv.CSVFormat;

import java.text.ParseException;

/**
 * Parse a CSVFormat out of a config file string.
 * 
 * @author Tom Parker
 */
public class CsvFormatParser implements Parser<CSVFormat> {

  /** Default format. */
  public static final CSVFormat DEFAULT_CSV_FORMAT = CSVFormat.RFC4180;

  private CSVFormat defaultCsvFormat;

  /**
   * Construct parser using default CSVFormat.
   */
  public CsvFormatParser() {
    defaultCsvFormat = DEFAULT_CSV_FORMAT;
  }


  /**
   * Construct parser using provided CSVFormat as a default, if needed.
   * 
   * @param csvFormat default format
   */
  public CsvFormatParser(CSVFormat csvFormat) {
    this.defaultCsvFormat = csvFormat;
  }

  @Override
  public CSVFormat parse(String value) throws ParseException {
    if (value == null) {
      return defaultCsvFormat;
    } else {
      return CSVFormat.valueOf(value);
    }
  }

}
