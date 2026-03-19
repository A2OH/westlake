package android.app;

/**
 * Android-compatible SearchableInfo shim.
 * Provides metadata about a searchable activity, including
 * its suggestion content provider configuration.
 * Stub implementation — returns sensible defaults.
 */
public class SearchableInfo {

    /** Returns the authority of the suggestions content provider, or null. */
    public String getSuggestAuthority() {
        return null;
    }

    /** Returns the path within the suggestions provider, or null. */
    public String getSuggestPath() {
        return null;
    }

    /** Returns the selection clause for suggestion queries, or null. */
    public String getSuggestSelection() {
        return null;
    }

    /** Returns the resource id for the label of the searchable activity. */
    public int getLabelId() {
        return 0;
    }

    /** Returns the resource id for the hint text. */
    public int getHintId() {
        return 0;
    }

    /** Returns the resource id for the search icon. */
    public int getIconId() {
        return 0;
    }

    /** Returns the suggest threshold (minimum number of characters before suggestions). */
    public int getSuggestThreshold() {
        return 0;
    }

    /** Returns whether voice search is enabled for this searchable. */
    public boolean getVoiceSearchEnabled() {
        return false;
    }

    /** Returns whether voice search should launch a web search. */
    public boolean getVoiceSearchLaunchWebSearch() {
        return false;
    }

    /** Returns whether voice search should launch the recognizer. */
    public boolean getVoiceSearchLaunchRecognizer() {
        return false;
    }

    /** Returns the IME options for the search field. */
    public int getImeOptions() { return 0; }

    /** Returns the input type for the search field. */
    public int getInputType() { return 0; }

    /** Returns the ComponentName of the searchable activity. */
    public android.content.ComponentName getSearchActivity() { return null; }

    /** Returns the intent action to use for suggestions. */
    public String getSuggestIntentAction() { return null; }

    /** Returns the intent data to use for suggestions. */
    public String getSuggestIntentData() { return null; }

    /** Returns the voice language ID. */
    public int getVoiceLanguageId() { return 0; }

    /** Returns the voice language mode ID. */
    public int getVoiceLanguageModeId() { return 0; }

    /** Returns the max number of voice results. */
    public int getVoiceMaxResults() { return 1; }

    /** Returns the voice prompt text ID. */
    public int getVoicePromptTextId() { return 0; }

    /** Returns an action key info for the given keycode. */
    public ActionKeyInfo findActionKey(int keyCode) { return null; }

    /** Inner class for action key metadata. */
    public static class ActionKeyInfo {
        public int getKeyCode() { return 0; }
        public String getQueryActionMsg() { return null; }
        public String getSuggestActionMsg() { return null; }
        public String getSuggestActionMsgColumn() { return null; }
    }

    @Override
    public String toString() {
        return "SearchableInfo{authority=" + getSuggestAuthority() + "}";
    }
}
