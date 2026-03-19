package android.view.inputmethod;

/**
 * Android-compatible BaseInputConnection shim.
 * Base class for implementors of the InputConnection interface.
 */
public class BaseInputConnection implements InputConnection {
    private boolean mDummyMode;

    public BaseInputConnection(boolean fullEditor) {
        mDummyMode = !fullEditor;
    }

    public BaseInputConnection(android.view.View targetView, boolean fullEditor) {
        mDummyMode = !fullEditor;
    }

    @Override
    public CharSequence getTextBeforeCursor(int n, int flags) {
        return "";
    }

    @Override
    public CharSequence getTextAfterCursor(int n, int flags) {
        return "";
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        return false;
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        return false;
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        return false;
    }

    @Override
    public boolean finishComposingText() {
        return false;
    }

    @Override
    public boolean beginBatchEdit() {
        return false;
    }

    @Override
    public boolean endBatchEdit() {
        return false;
    }

    @Override
    public int getCursorCapsMode(int reqModes) {
        return 0;
    }

    public static void removeComposingSpans(android.text.Spannable text) {}

    public boolean reportFullscreenMode(boolean enabled) { return false; }
    public boolean sendKeyEvent(android.view.KeyEvent event) { return false; }
    public CharSequence getSelectedText(int flags) { return null; }
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) { return null; }
    public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) { return false; }
    public boolean setComposingRegion(int start, int end) { return false; }
    public boolean commitCompletion(CompletionInfo text) { return false; }
    public boolean commitCorrection(CorrectionInfo correctionInfo) { return false; }
    public boolean setSelection(int start, int end) { return false; }
    public boolean performContextMenuAction(int id) { return false; }
    public boolean clearMetaKeyStates(int states) { return false; }
    public boolean performPrivateCommand(String action, android.os.Bundle data) { return false; }
    public boolean requestCursorUpdates(int cursorUpdateMode) { return false; }
    public android.os.Handler getHandler() { return null; }
    public void closeConnection() {}
    public boolean commitContent(InputContentInfo inputContentInfo, int flags, android.os.Bundle opts) { return false; }
    public boolean performEditorAction(int editorAction) { return false; }
}
