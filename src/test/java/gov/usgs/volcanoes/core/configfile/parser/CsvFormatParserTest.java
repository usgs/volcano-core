package gov.usgs.volcanoes.core.configfile.parser;

import static org.junit.Assert.assertEquals;

import org.apache.commons.csv.CSVFormat;
import org.junit.Test;

import java.text.ParseException;

public class CsvFormatParserTest {

  @Test
  public void defaultConstructor() throws ParseException {
    CsvFormatParser parser = new CsvFormatParser();
    CSVFormat format = parser.parse(null);
    assertEquals(format, CsvFormatParser.DEFAULT_CSV_FORMAT);
  }

  @Test
  public void oneArgConstructor() throws ParseException {
    CSVFormat format1 = CSVFormat.RFC4180;
    CsvFormatParser parser = new CsvFormatParser(format1);
    CSVFormat format2 = parser.parse(null);
    assertEquals(format1, format2);
  }
  
  @Test
  public void parse() throws ParseException {
    CsvFormatParser parser = new CsvFormatParser();
    CSVFormat format = parser.parse("Excel");
    assertEquals(format, CSVFormat.EXCEL);
  }
}
