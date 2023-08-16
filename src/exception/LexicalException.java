package exception;
public class LexicalException {

  public static void badIdentifier(char character, int line, int column) throws Exception {
    String exception = String.format("Bad identifier: expected identifier received '%s' at (line: %d Column: %d)", character, line, column);
    throw new Exception(exception);
  }

  public static void badValue(char character, int line, int column) throws Exception {
    String exception = String.format("Bad value: expected number received '%s' at (line: %d Column: %d)", character, line, column);
    throw new Exception(exception);
  }

  public static void unrecognizedSymbol(char character, int line, int column) throws Exception {
    String exception = String.format("Unrecognized symbol '%s' at (line: %d Column: %d)", character, line, column);
    throw new Exception(exception);
  }

}
