package android.text.method;

public class DialerKeyListener extends NumberKeyListener {
    public static final char[] CHARACTERS = new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '#', '*', '+', '-', '(', ')', ',', '/', 'N', '.', ' ', ';'
    };

    public DialerKeyListener() {}

    public char[] getAcceptedChars() { return new char[0]; }
    public int getInputType() { return 0; }
    public static DialerKeyListener getInstance() { return null; }
    public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}
}