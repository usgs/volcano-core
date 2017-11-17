package gov.usgs.volcanoes.core.xml;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Quick and Dirty xml parser. This parser is, like the SAX parser, an event
 * based parser, but with much less functionality.
 * 
 * <p>Reformatted and Java 1.5-ized from article:
 * http://www.javaworld.com/javatips/jw-javatip128_p.html
 * 
 * @author Steven R. Brandt, Dan Cervelli
 */
public class SimpleXmlParser {
  /**
   * Return popped value from st if non-empty, else PRE.
   * @param st Stack
   * @return popped value or, if none, PRE
   */
  private static int popMode(Stack<Integer> st) {
    if (!st.empty()) {
      return ((Integer) st.pop()).intValue();
    } else {
      return PRE;
    }
  }

  private static final int TEXT = 1;
  private static final int ENTITY = 2;
  private static final int OPEN_TAG = 3;
  private static final int CLOSE_TAG = 4;
  private static final int START_TAG = 5;
  private static final int ATTRIBUTE_LVALUE = 6;
  private static final int ATTRIBUTE_EQUAL = 9;
  private static final int ATTRIBUTE_RVALUE = 10;
  private static final int QUOTE = 7;
  private static final int IN_TAG = 8;
  private static final int SINGLE_TAG = 12;
  private static final int COMMENT = 13;
  private static final int DONE = 11;
  private static final int DOCTYPE = 14;
  private static final int PRE = 15;
  private static final int CDATA = 16;

  /**
   * Parse character stream into xml document.
   * @param doc event handler realizing actions to perform while event happens
   * @param reader character-stream reader with processed xml text
   * @throws Exception when things go wrong
   */
  public static void parse(XmlDocHandler doc, Reader reader) throws Exception {
    Stack<Integer> st = new Stack<Integer>();
    int depth = 0;
    int mode = PRE;
    int quotec = '"';
    depth = 0;
    StringBuffer sb = new StringBuffer();
    StringBuffer etag = new StringBuffer();
    String tagName = null;
    String lvalue = null;
    String rvalue = null;
    Map<String, String> attrs = null;
    st = new Stack<Integer>();
    doc.startDocument();
    int line = 1;
    int col = 0;
    boolean eol = false;
    int ch = 0;
    while ((ch = reader.read()) != -1) {

      // We need to map \r, \r\n, and \n to \n
      // See XML spec section 2.11
      if (ch == '\n' && eol) {
        eol = false;
        continue;
      } else if (eol) {
        eol = false;
      } else if (ch == '\n') {
        line++;
        col = 0;
      } else if (ch == '\r') {
        eol = true;
        ch = '\n';
        line++;
        col = 0;
      } else {
        col++;
      }

      if (mode == DONE) {
        doc.endDocument();
        return;

        // We are between tags collecting text.
      } else if (mode == TEXT) {
        if (ch == '<') {
          st.push(mode);
          mode = START_TAG;
          if (sb.length() > 0) {
            doc.text(sb.toString());
            sb.setLength(0);
          }
        } else if (ch == '&') {
          st.push(mode);
          mode = ENTITY;
          etag.setLength(0);
        } else {
          sb.append((char) ch);
        }
        // we are processing a closing tag: e.g. </foo>
      } else if (mode == CLOSE_TAG) {
        if (ch == '>') {
          mode = popMode(st);
          tagName = sb.toString();
          sb.setLength(0);
          depth--;
          if (depth == 0) {
            mode = DONE;
          }
          doc.endElement(tagName);
        } else {
          sb.append((char) ch);
        }

        // we are processing CDATA
      } else if (mode == CDATA) {
        if (ch == '>' && sb.toString().endsWith("]]")) {
          sb.setLength(sb.length() - 2);
          doc.text(sb.toString());
          sb.setLength(0);
          mode = popMode(st);
        } else {
          sb.append((char) ch);
        }
        // we are processing a comment. We are inside
        // the <!-- .... --> looking for the -->.
      } else if (mode == COMMENT) {
        if (ch == '>' && sb.toString().endsWith("--")) {
          sb.setLength(0);
          mode = popMode(st);
        } else {
          sb.append((char) ch);
        }
        // We are outside the root tag element
      } else if (mode == PRE) {
        if (ch == '<') {
          mode = TEXT;
          st.push(mode);
          mode = START_TAG;
        }

        // We are inside one of these <? ... ?>
        // or one of these <!DOCTYPE ... >
      } else if (mode == DOCTYPE) {
        if (ch == '>') {
          mode = popMode(st);
          if (mode == TEXT) {
            mode = PRE;
          }
        }

        // we have just seen a < and
        // are wondering what we are looking at
        // <foo>, </foo>, <!-- ... --->, etc.
      } else if (mode == START_TAG) {
        mode = popMode(st);
        if (ch == '/') {
          st.push(mode);
          mode = CLOSE_TAG;
        } else if (ch == '?') {
          mode = DOCTYPE;
        } else {
          st.push(mode);
          mode = OPEN_TAG;
          tagName = null;
          attrs = new HashMap<String, String>();
          sb.append((char) ch);
        }

        // we are processing an entity, e.g. &lt;, &#187;, etc.
      } else if (mode == ENTITY) {
        if (ch == ';') {
          mode = popMode(st);
          String cent = etag.toString();
          etag.setLength(0);
          if (cent.equals("lt")) {
            sb.append('<');
          } else if (cent.equals("gt")) {
            sb.append('>');
          } else if (cent.equals("amp")) {
            sb.append('&');
          } else if (cent.equals("quot")) {
            sb.append('"');
          } else if (cent.equals("apos")) {
            sb.append('\'');
          } else if (cent.startsWith("#x")) {
            sb.append((char) Integer.parseInt(cent.substring(2), 16));
          } else if (cent.startsWith("#")) {
            sb.append((char) Integer.parseInt(cent.substring(1)));
            // Insert custom entity definitions here
          } else {
            exc("Unknown entity: &" + cent + ";", line, col);
          }
        } else {
          etag.append((char) ch);
        }

        // we have just seen something like this:
        // <foo a="b"/
        // and are looking for the final >.
      } else if (mode == SINGLE_TAG) {
        if (tagName == null) {
          tagName = sb.toString();
        }
        if (ch != '>') {
          exc("Expected > for tag: <" + tagName + "/>", line, col);
        }
        doc.startElement(tagName, attrs);
        doc.endElement(tagName);
        if (depth == 0) {
          doc.endDocument();
          return;
        }
        sb.setLength(0);
        attrs = new HashMap<String, String>();
        tagName = null;
        mode = popMode(st);

        // we are processing something
        // like this <foo ... >. It could
        // still be a <!-- ... --> or something.
      } else if (mode == OPEN_TAG) {
        if (ch == '>') {
          if (tagName == null) {
            tagName = sb.toString();
          }
          sb.setLength(0);
          depth++;
          doc.startElement(tagName, attrs);
          tagName = null;
          attrs = new HashMap<String, String>();
          mode = popMode(st);
        } else if (ch == '/') {
          mode = SINGLE_TAG;
        } else if (ch == '-' && sb.toString().equals("!-")) {
          mode = COMMENT;
        } else if (ch == '[' && sb.toString().equals("![CDATA")) {
          mode = CDATA;
          sb.setLength(0);
        } else if (ch == 'E' && sb.toString().equals("!DOCTYP")) {
          sb.setLength(0);
          mode = DOCTYPE;
        } else if (Character.isWhitespace((char) ch)) {
          tagName = sb.toString();
          sb.setLength(0);
          mode = IN_TAG;
        } else {
          sb.append((char) ch);
        }

        // We are processing the quoted right-hand side
        // of an element's attribute.
      } else if (mode == QUOTE) {
        if (ch == quotec) {
          rvalue = sb.toString();
          sb.setLength(0);
          attrs.put(lvalue, rvalue);
          mode = IN_TAG;
          // See section the XML spec, section 3.3.3
          // on normalization processing.
        } else if (" \r\n\t".indexOf(ch) >= 0) {
          sb.append(' ');
        } else if (ch == '&') {
          st.push(mode);
          mode = ENTITY;
          etag.setLength(0);
        } else {
          sb.append((char) ch);
        }

      } else if (mode == ATTRIBUTE_RVALUE) {
        if (ch == '"' || ch == '\'') {
          quotec = ch;
          mode = QUOTE;
        } else if (Character.isWhitespace((char) ch)) {
          ;
        } else {
          exc("Error in attribute processing", line, col);
        }

      } else if (mode == ATTRIBUTE_LVALUE) {
        if (Character.isWhitespace((char) ch)) {
          lvalue = sb.toString();
          sb.setLength(0);
          mode = ATTRIBUTE_EQUAL;
        } else if (ch == '=') {
          lvalue = sb.toString();
          sb.setLength(0);
          mode = ATTRIBUTE_RVALUE;
        } else {
          sb.append((char) ch);
        }

      } else if (mode == ATTRIBUTE_EQUAL) {
        if (ch == '=') {
          mode = ATTRIBUTE_RVALUE;
        } else if (Character.isWhitespace((char) ch)) {
          ;
        } else {
          exc("Error in attribute processing.", line, col);
        }

      } else if (mode == IN_TAG) {
        if (ch == '>') {
          mode = popMode(st);
          doc.startElement(tagName, attrs);
          depth++;
          tagName = null;
          attrs = new HashMap<String, String>();
        } else if (ch == '/') {
          mode = SINGLE_TAG;
        } else if (Character.isWhitespace((char) ch)) {
          ;
        } else {
          mode = ATTRIBUTE_LVALUE;
          sb.append((char) ch);
        }
      }
    }
    if (mode == DONE) {
      doc.endDocument();
    } else {
      exc("missing end tag", line, col);
    }
  }

  /**
   * Generates error diagnostic message and throws exception with it.
   * @param str text error description
   * @param line line number
   * @param col column number
   * @throws Exception when things go wrong
   */
  private static void exc(String str, int line, int col) throws Exception {
    throw new Exception(str + " near line " + line + ", column " + col);
  }
}
