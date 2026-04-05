package androidx.fragment.app;

import android.app.Dialog;
import android.os.Bundle;

/**
 * AndroidX DialogFragment stub. Extends Fragment and manages a Dialog instance.
 */
public class DialogFragment extends Fragment {

    /** Style constants matching the real DialogFragment. */
    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_NO_TITLE = 1;
    public static final int STYLE_NO_FRAME = 2;
    public static final int STYLE_NO_INPUT = 3;

    private Dialog mDialog;
    private boolean mDismissed;
    private boolean mShownByMe;
    private int mStyle = STYLE_NORMAL;
    private int mTheme = 0;
    private boolean mCancelable = true;

    public DialogFragment() {}

    /**
     * Set the style and theme for the dialog.
     */
    public void setStyle(int style, int theme) {
        mStyle = style;
        mTheme = theme;
    }

    /**
     * Display the dialog, adding the fragment to the given FragmentManager.
     */
    public void show(FragmentManager manager, String tag) {
        mDismissed = false;
        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    /**
     * Display the dialog using the provided FragmentTransaction.
     */
    public int show(FragmentTransaction transaction, String tag) {
        mDismissed = false;
        mShownByMe = true;
        transaction.add(this, tag);
        return transaction.commit();
    }

    /**
     * Display the dialog immediately.
     */
    public void showNow(FragmentManager manager, String tag) {
        mDismissed = false;
        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitNow();
    }

    /**
     * Dismiss the dialog and remove this fragment.
     */
    public void dismiss() {
        dismissInternal(false);
    }

    /**
     * Dismiss the dialog allowing state loss.
     */
    public void dismissAllowingStateLoss() {
        dismissInternal(true);
    }

    private void dismissInternal(boolean allowStateLoss) {
        if (mDismissed) return;
        mDismissed = true;
        if (mDialog != null) {
            mDialog.dismiss();
        }
        FragmentManager fm = null;
        try {
            fm = getParentFragmentManager();
        } catch (IllegalStateException e) {
            // Not attached
        }
        if (fm != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(this);
            if (allowStateLoss) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commitNow();
            }
        }
    }

    /**
     * Override to build your own Dialog container.
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog();
    }

    /**
     * Returns the Dialog managed by this fragment.
     */
    public Dialog getDialog() {
        return mDialog;
    }

    public boolean isCancelable() { return mCancelable; }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
    }

    public boolean getShowsDialog() { return true; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = onCreateDialog(savedInstanceState);
        if (mDialog != null) {
            mDialog.setCancelable(mCancelable);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDialog != null && !mDismissed) {
            mDialog.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.hide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
