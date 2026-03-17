package android.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible streaming JSON reader shim.
 * Stack-based state machine with manual character parsing.
 * No regex, no external libs, no String.split, no String.format.
 */
public final class JsonReader implements Closeable {

    // Scope constants
    private static final int SCOPE_EMPTY_DOC    = 0;
    private static final int SCOPE_NON_EMPTY_DOC = 1;
    private static final int SCOPE_EMPTY_ARRAY  = 2;
    private static final int SCOPE_NON_EMPTY_ARRAY = 3;
    private static final int SCOPE_EMPTY_OBJECT = 4;
    private static final int SCOPE_NON_EMPTY_OBJECT = 5;
    private static final int SCOPE_DANGLING_NAME = 6;

    // Peeked token constants
    private static final int PEEKED_NONE           = 0;
    private static final int PEEKED_BEGIN_OBJECT   = 1;
    private static final int PEEKED_END_OBJECT     = 2;
    private static final int PEEKED_BEGIN_ARRAY    = 3;
    private static final int PEEKED_END_ARRAY      = 4;
    private static final int PEEKED_TRUE           = 5;
    private static final int PEEKED_FALSE          = 6;
    private static final int PEEKED_NULL           = 7;
    private static final int PEEKED_SINGLE_QUOTED  = 8;
    private static final int PEEKED_DOUBLE_QUOTED  = 9;
    private static final int PEEKED_UNQUOTED       = 10;
    private static final int PEEKED_NUMBER         = 11;
    private static final int PEEKED_EOF            = 12;

    private final Reader in;
    private boolean lenient = false;

    // Scope stack
    private int[] scopeStack = new int[32];
    private int stackSize = 0;

    // Read-ahead buffer
    private final char[] buffer = new char[1024];
    private int pos = 0;
    private int limit = 0;

    // Current peeked token
    private int peeked = PEEKED_NONE;

    // Buffer for peeked number literal
    private String peekedString;
    private long peekedLong;
    private boolean peekedLongExact;

    public JsonReader(Reader in) {
        if (in == null) throw new NullPointerException("in == null");
        this.in = in;
        scopeStack[stackSize++] = SCOPE_EMPTY_DOC;
    }

    // Convenience: Android's JsonReader takes a Reader, but many use it with strings
    // This matches the Android API.

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return lenient;
    }

    public void beginArray() throws IOException {
        int p = doPeek();
        if (p == PEEKED_BEGIN_ARRAY) {
            push(SCOPE_EMPTY_ARRAY);
            peeked = PEEKED_NONE;
        } else {
            throw syntaxError("Expected BEGIN_ARRAY but was " + tokenName(p));
        }
    }

    public void endArray() throws IOException {
        int p = doPeek();
        if (p == PEEKED_END_ARRAY) {
            stackSize--;
            peeked = PEEKED_NONE;
        } else {
            throw syntaxError("Expected END_ARRAY but was " + tokenName(p));
        }
    }

    public void beginObject() throws IOException {
        int p = doPeek();
        if (p == PEEKED_BEGIN_OBJECT) {
            push(SCOPE_EMPTY_OBJECT);
            peeked = PEEKED_NONE;
        } else {
            throw syntaxError("Expected BEGIN_OBJECT but was " + tokenName(p));
        }
    }

    public void endObject() throws IOException {
        int p = doPeek();
        if (p == PEEKED_END_OBJECT) {
            stackSize--;
            peeked = PEEKED_NONE;
        } else {
            throw syntaxError("Expected END_OBJECT but was " + tokenName(p));
        }
    }

    public boolean hasNext() throws IOException {
        int p = doPeek();
        return p != PEEKED_END_OBJECT && p != PEEKED_END_ARRAY && p != PEEKED_EOF;
    }

    public JsonToken peek() throws IOException {
        int p = doPeek();
        switch (p) {
            case PEEKED_BEGIN_OBJECT: return JsonToken.BEGIN_OBJECT;
            case PEEKED_END_OBJECT:   return JsonToken.END_OBJECT;
            case PEEKED_BEGIN_ARRAY:  return JsonToken.BEGIN_ARRAY;
            case PEEKED_END_ARRAY:    return JsonToken.END_ARRAY;
            case PEEKED_TRUE:
            case PEEKED_FALSE:        return JsonToken.BOOLEAN;
            case PEEKED_NULL:         return JsonToken.NULL;
            case PEEKED_SINGLE_QUOTED:
            case PEEKED_DOUBLE_QUOTED:
            case PEEKED_UNQUOTED:     return JsonToken.STRING;
            case PEEKED_NUMBER:       return JsonToken.NUMBER;
            case PEEKED_EOF:          return JsonToken.END_DOCUMENT;
            default: throw new AssertionError();
        }
    }

    public String nextName() throws IOException {
        int p = doPeek();
        String result;
        if (p == PEEKED_DOUBLE_QUOTED) {
            result = readQuotedString('"');
        } else if (p == PEEKED_SINGLE_QUOTED) {
            result = readQuotedString('\'');
        } else if (p == PEEKED_UNQUOTED) {
            result = readUnquotedString();
        } else {
            throw syntaxError("Expected a name but was " + tokenName(p));
        }
        peeked = PEEKED_NONE;
        // Expect colon after name
        int c = nextNonWhitespace();
        if (c != ':') {
            throw syntaxError("Expected ':' after name");
        }
        return result;
    }

    public String nextString() throws IOException {
        int p = doPeek();
        String result;
        if (p == PEEKED_DOUBLE_QUOTED) {
            result = readQuotedString('"');
        } else if (p == PEEKED_SINGLE_QUOTED) {
            result = readQuotedString('\'');
        } else if (p == PEEKED_UNQUOTED) {
            result = readUnquotedString();
        } else if (p == PEEKED_NUMBER) {
            result = peekedString;
        } else if (p == PEEKED_TRUE) {
            result = "true";
        } else if (p == PEEKED_FALSE) {
            result = "false";
        } else {
            throw syntaxError("Expected a string but was " + tokenName(p));
        }
        peeked = PEEKED_NONE;
        return result;
    }

    public boolean nextBoolean() throws IOException {
        int p = doPeek();
        if (p == PEEKED_TRUE) {
            peeked = PEEKED_NONE;
            return true;
        } else if (p == PEEKED_FALSE) {
            peeked = PEEKED_NONE;
            return false;
        }
        throw syntaxError("Expected a boolean but was " + tokenName(p));
    }

    public void nextNull() throws IOException {
        int p = doPeek();
        if (p == PEEKED_NULL) {
            peeked = PEEKED_NONE;
        } else {
            throw syntaxError("Expected null but was " + tokenName(p));
        }
    }

    public double nextDouble() throws IOException {
        int p = doPeek();
        if (p == PEEKED_NUMBER) {
            peeked = PEEKED_NONE;
            return Double.parseDouble(peekedString);
        }

        String s = nextString();
        double d = Double.parseDouble(s);
        if (!lenient && (Double.isNaN(d) || Double.isInfinite(d))) {
            throw new NumberFormatException("JSON forbids NaN and infinities: " + d);
        }
        return d;
    }

    public long nextLong() throws IOException {
        int p = doPeek();
        if (p == PEEKED_NUMBER) {
            peeked = PEEKED_NONE;
            if (peekedLongExact) {
                return peekedLong;
            }
            double d = Double.parseDouble(peekedString);
            long l = (long) d;
            if (l != d) {
                throw new NumberFormatException("Expected a long but was " + peekedString);
            }
            return l;
        }

        String s = nextString();
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            double d = Double.parseDouble(s);
            long l = (long) d;
            if (l != d) {
                throw new NumberFormatException("Expected a long but was " + s);
            }
            return l;
        }
    }

    public int nextInt() throws IOException {
        int p = doPeek();
        if (p == PEEKED_NUMBER) {
            peeked = PEEKED_NONE;
            if (peekedLongExact) {
                int i = (int) peekedLong;
                if (i != peekedLong) {
                    throw new NumberFormatException("Expected an int but was " + peekedString);
                }
                return i;
            }
            double d = Double.parseDouble(peekedString);
            int i = (int) d;
            if (i != d) {
                throw new NumberFormatException("Expected an int but was " + peekedString);
            }
            return i;
        }

        String s = nextString();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            double d = Double.parseDouble(s);
            int i = (int) d;
            if (i != d) {
                throw new NumberFormatException("Expected an int but was " + s);
            }
            return i;
        }
    }

    public void skipValue() throws IOException {
        int count = 0;
        do {
            int p = doPeek();
            if (p == PEEKED_BEGIN_ARRAY) {
                push(SCOPE_EMPTY_ARRAY);
                count++;
            } else if (p == PEEKED_BEGIN_OBJECT) {
                push(SCOPE_EMPTY_OBJECT);
                count++;
            } else if (p == PEEKED_END_ARRAY) {
                stackSize--;
                count--;
            } else if (p == PEEKED_END_OBJECT) {
                stackSize--;
                count--;
            } else if (p == PEEKED_DOUBLE_QUOTED) {
                readQuotedString('"');
                consumeColonIfName();
            } else if (p == PEEKED_SINGLE_QUOTED) {
                readQuotedString('\'');
                consumeColonIfName();
            } else if (p == PEEKED_UNQUOTED) {
                readUnquotedString();
                consumeColonIfName();
            } else if (p == PEEKED_NUMBER) {
                // already consumed
            } else if (p == PEEKED_EOF) {
                return;
            }
            // TRUE, FALSE, NULL need no extra reading
            peeked = PEEKED_NONE;
        } while (count > 0);
    }

    /**
     * If doPeek returned a name (scope is DANGLING_NAME), consume the colon separator.
     */
    private void consumeColonIfName() throws IOException {
        if (stackSize > 0 && scopeStack[stackSize - 1] == SCOPE_DANGLING_NAME) {
            int c = nextNonWhitespace();
            if (c != ':') {
                throw syntaxError("Expected ':' after name in skipValue");
            }
        }
    }

    @Override
    public void close() throws IOException {
        peeked = PEEKED_NONE;
        stackSize = 0;
        in.close();
    }

    @Override
    public String toString() {
        return "JsonReader";
    }

    // -----------------------------------------------------------------------
    // Peek logic
    // -----------------------------------------------------------------------

    private int doPeek() throws IOException {
        if (peeked != PEEKED_NONE) {
            return peeked;
        }

        int scope = scopeStack[stackSize - 1];

        if (scope == SCOPE_EMPTY_ARRAY) {
            scopeStack[stackSize - 1] = SCOPE_NON_EMPTY_ARRAY;
        } else if (scope == SCOPE_NON_EMPTY_ARRAY) {
            // Expect comma or end of array
            int c = nextNonWhitespace();
            if (c == ']') {
                return peeked = PEEKED_END_ARRAY;
            } else if (c == ',') {
                // fine, continue to read the next value
            } else {
                throw syntaxError("Unterminated array");
            }
        } else if (scope == SCOPE_EMPTY_OBJECT || scope == SCOPE_NON_EMPTY_OBJECT) {
            scopeStack[stackSize - 1] = SCOPE_DANGLING_NAME;
            if (scope == SCOPE_NON_EMPTY_OBJECT) {
                int c = nextNonWhitespace();
                if (c == '}') {
                    return peeked = PEEKED_END_OBJECT;
                } else if (c == ',') {
                    // continue
                } else {
                    throw syntaxError("Unterminated object");
                }
            }
            // Read name
            int c = nextNonWhitespace();
            if (c == '"') {
                return peeked = PEEKED_DOUBLE_QUOTED;
            } else if (c == '\'') {
                if (!lenient) throw syntaxError("Expected name at " + (char) c);
                return peeked = PEEKED_SINGLE_QUOTED;
            } else if (c == '}') {
                if (scope == SCOPE_NON_EMPTY_OBJECT) {
                    throw syntaxError("Expected name");
                }
                return peeked = PEEKED_END_OBJECT;
            } else {
                if (!lenient) throw syntaxError("Expected name at " + (char) c);
                pushBack((char) c);
                return peeked = PEEKED_UNQUOTED;
            }
        } else if (scope == SCOPE_DANGLING_NAME) {
            scopeStack[stackSize - 1] = SCOPE_NON_EMPTY_OBJECT;
            // Name was already read; colon consumed by nextName()
            // Now peek the value
        } else if (scope == SCOPE_EMPTY_DOC) {
            scopeStack[stackSize - 1] = SCOPE_NON_EMPTY_DOC;
        } else if (scope == SCOPE_NON_EMPTY_DOC) {
            int c = nextNonWhitespace();
            if (c == -1) {
                return peeked = PEEKED_EOF;
            }
            if (!lenient) throw syntaxError("Expected EOF");
            pushBack((char) c);
        }

        // Read a value
        int c = nextNonWhitespace();
        if (c == -1) {
            return peeked = PEEKED_EOF;
        }

        switch (c) {
            case '{': return peeked = PEEKED_BEGIN_OBJECT;
            case '[': return peeked = PEEKED_BEGIN_ARRAY;
            case ']':
                if (scope == SCOPE_EMPTY_ARRAY) {
                    return peeked = PEEKED_END_ARRAY;
                }
                throw syntaxError("Unexpected ']'");
            case '"': return peekStringValue('"');
            case '\'':
                if (!lenient) throw syntaxError("Unexpected '\\'");
                return peekStringValue('\'');
            default:
                pushBack((char) c);
                return peekKeyword();
        }
    }

    private int peekStringValue(char quote) throws IOException {
        // Already consumed the opening quote char in doPeek
        if (quote == '"') {
            return peeked = PEEKED_DOUBLE_QUOTED;
        } else {
            return peeked = PEEKED_SINGLE_QUOTED;
        }
    }

    private int peekKeyword() throws IOException {
        // Read ahead to identify true/false/null or a number
        int c = nextChar();
        if (c == 't' || c == 'T') {
            readExpected("rue", c == 'T');
            return peeked = PEEKED_TRUE;
        } else if (c == 'f' || c == 'F') {
            readExpected("alse", c == 'F');
            return peeked = PEEKED_FALSE;
        } else if (c == 'n' || c == 'N') {
            readExpected("ull", c == 'N');
            return peeked = PEEKED_NULL;
        } else {
            // Must be a number
            pushBack((char) c);
            return peekNumber();
        }
    }

    private void readExpected(String rest, boolean uppercase) throws IOException {
        for (int i = 0; i < rest.length(); i++) {
            int c = nextChar();
            char expected = rest.charAt(i);
            if (c != expected && c != Character.toUpperCase(expected)) {
                throw syntaxError("Unexpected character");
            }
        }
    }

    private int peekNumber() throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean isLongCompatible = true;
        long longValue = 0;
        boolean negative = false;
        boolean hasDecimalOrExp = false;

        int c = nextChar();
        if (c == '-') {
            sb.append('-');
            negative = true;
            c = nextChar();
        }

        if (c < '0' || c > '9') {
            throw syntaxError("Expected digit");
        }

        sb.append((char) c);
        if (!negative) {
            longValue = c - '0';
        } else {
            longValue = -(c - '0');
        }

        // Read remaining integer digits
        while (true) {
            c = peekChar();
            if (c >= '0' && c <= '9') {
                nextChar();
                sb.append((char) c);
                if (isLongCompatible) {
                    long next = longValue * 10 + (negative ? -(c - '0') : (c - '0'));
                    // Overflow check
                    if ((!negative && next < longValue) || (negative && next > longValue)) {
                        isLongCompatible = false;
                    }
                    longValue = next;
                }
            } else {
                break;
            }
        }

        // Decimal part
        c = peekChar();
        if (c == '.') {
            nextChar();
            sb.append('.');
            hasDecimalOrExp = true;
            isLongCompatible = false;
            // Must have at least one digit
            c = nextChar();
            if (c < '0' || c > '9') {
                throw syntaxError("Expected digit after decimal point");
            }
            sb.append((char) c);
            while (true) {
                c = peekChar();
                if (c >= '0' && c <= '9') {
                    nextChar();
                    sb.append((char) c);
                } else {
                    break;
                }
            }
        }

        // Exponent part
        c = peekChar();
        if (c == 'e' || c == 'E') {
            nextChar();
            sb.append((char) c);
            hasDecimalOrExp = true;
            isLongCompatible = false;
            c = peekChar();
            if (c == '+' || c == '-') {
                nextChar();
                sb.append((char) c);
            }
            c = nextChar();
            if (c < '0' || c > '9') {
                throw syntaxError("Expected digit in exponent");
            }
            sb.append((char) c);
            while (true) {
                c = peekChar();
                if (c >= '0' && c <= '9') {
                    nextChar();
                    sb.append((char) c);
                } else {
                    break;
                }
            }
        }

        peekedString = sb.toString();
        peekedLong = longValue;
        peekedLongExact = isLongCompatible;
        return peeked = PEEKED_NUMBER;
    }

    // -----------------------------------------------------------------------
    // String reading
    // -----------------------------------------------------------------------

    private String readQuotedString(char quote) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int c = nextChar();
            if (c == quote) {
                return sb.toString();
            } else if (c == '\\') {
                c = nextChar();
                switch (c) {
                    case '"':  sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/'); break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    case 'u':
                        char unicode = 0;
                        for (int i = 0; i < 4; i++) {
                            c = nextChar();
                            unicode <<= 4;
                            if (c >= '0' && c <= '9') unicode += c - '0';
                            else if (c >= 'a' && c <= 'f') unicode += c - 'a' + 10;
                            else if (c >= 'A' && c <= 'F') unicode += c - 'A' + 10;
                            else throw syntaxError("Bad hex digit in \\u escape");
                        }
                        sb.append(unicode);
                        break;
                    case '\'':
                        sb.append('\'');
                        break;
                    default:
                        if (lenient) {
                            sb.append((char) c);
                        } else {
                            throw syntaxError("Invalid escape sequence: \\" + (char) c);
                        }
                }
            } else if (c == -1) {
                throw syntaxError("Unterminated string");
            } else {
                sb.append((char) c);
            }
        }
    }

    private String readUnquotedString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int c = peekChar();
            if (c == -1 || c == ':' || c == ',' || c == '}' || c == ']'
                    || c == ' ' || c == '\t' || c == '\n' || c == '\r'
                    || c == '/' || c == '\\' || c == '{' || c == '[') {
                break;
            }
            nextChar();
            sb.append((char) c);
        }
        return sb.toString();
    }

    // -----------------------------------------------------------------------
    // Low-level I/O
    // -----------------------------------------------------------------------

    private int pushBackChar = -1;

    private void pushBack(char c) {
        pushBackChar = c;
    }

    private int nextChar() throws IOException {
        if (pushBackChar != -1) {
            int c = pushBackChar;
            pushBackChar = -1;
            return c;
        }
        if (pos >= limit) {
            if (!fillBuffer()) return -1;
        }
        return buffer[pos++];
    }

    private int peekChar() throws IOException {
        if (pushBackChar != -1) {
            return pushBackChar;
        }
        if (pos >= limit) {
            if (!fillBuffer()) return -1;
        }
        return buffer[pos];
    }

    private boolean fillBuffer() throws IOException {
        int n = in.read(buffer, 0, buffer.length);
        if (n <= 0) return false;
        pos = 0;
        limit = n;
        return true;
    }

    private int nextNonWhitespace() throws IOException {
        while (true) {
            int c = nextChar();
            if (c == -1) return -1;
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') continue;
            // Handle comments in lenient mode
            if (c == '/') {
                if (!lenient) return c;
                int next = peekChar();
                if (next == '/') {
                    nextChar();
                    skipToEndOfLine();
                    continue;
                } else if (next == '*') {
                    nextChar();
                    skipBlockComment();
                    continue;
                }
                return c;
            }
            if (c == '#' && lenient) {
                skipToEndOfLine();
                continue;
            }
            return c;
        }
    }

    private void skipToEndOfLine() throws IOException {
        while (true) {
            int c = nextChar();
            if (c == -1 || c == '\n' || c == '\r') return;
        }
    }

    private void skipBlockComment() throws IOException {
        while (true) {
            int c = nextChar();
            if (c == -1) throw syntaxError("Unterminated block comment");
            if (c == '*') {
                int next = peekChar();
                if (next == '/') {
                    nextChar();
                    return;
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Scope stack
    // -----------------------------------------------------------------------

    private void push(int scope) {
        if (stackSize >= scopeStack.length) {
            int[] newStack = new int[scopeStack.length * 2];
            System.arraycopy(scopeStack, 0, newStack, 0, scopeStack.length);
            scopeStack = newStack;
        }
        scopeStack[stackSize++] = scope;
    }

    // -----------------------------------------------------------------------
    // Error helpers
    // -----------------------------------------------------------------------

    private IOException syntaxError(String message) {
        return new IOException(message);
    }

    private String tokenName(int p) {
        switch (p) {
            case PEEKED_BEGIN_OBJECT: return "BEGIN_OBJECT";
            case PEEKED_END_OBJECT:   return "END_OBJECT";
            case PEEKED_BEGIN_ARRAY:  return "BEGIN_ARRAY";
            case PEEKED_END_ARRAY:    return "END_ARRAY";
            case PEEKED_TRUE:
            case PEEKED_FALSE:        return "BOOLEAN";
            case PEEKED_NULL:         return "NULL";
            case PEEKED_SINGLE_QUOTED:
            case PEEKED_DOUBLE_QUOTED:
            case PEEKED_UNQUOTED:     return "STRING";
            case PEEKED_NUMBER:       return "NUMBER";
            case PEEKED_EOF:          return "END_DOCUMENT";
            default: return "UNKNOWN";
        }
    }
}
