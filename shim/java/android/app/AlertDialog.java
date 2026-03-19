package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Shim: android.app.AlertDialog
 * Provides Builder pattern for constructing dialogs.
 */
public class AlertDialog {
    private CharSequence mTitle;
    private CharSequence mMessage;
    private View mView;
    private CharSequence mPositiveText;
    private CharSequence mNegativeText;
    private CharSequence mNeutralText;
    private boolean mCancelable = true;

    public AlertDialog() {}

    public CharSequence getTitle() { return mTitle; }
    public CharSequence getMessage() { return mMessage; }
    public boolean isCancelable() { return mCancelable; }

    public Object getButton(Object p0) { return null; }
    public android.widget.ListView getListView() { return null; }
    public void setButton(Object p0, Object p1, Object p2) {}
    public void setCustomTitle(Object p0) {}
    public void setIcon(Object p0) {}
    public void setIconAttribute(Object p0) {}
    public void setInverseBackgroundForced(Object p0) {}
    public void setMessage(Object p0) { if (p0 instanceof CharSequence) mMessage = (CharSequence) p0; }
    public void setView(Object p0) {}
    public void setView(Object p0, Object p1, Object p2, Object p3, Object p4) {}
    public boolean isShowing() { return false; }
    public void show() {}
    public void dismiss() {}
    public void cancel() {}
    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {}
    public android.view.Window getWindow() { return null; }

    public static class Builder {
        private Context mContext;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private View mView;
        private CharSequence mPositiveText;
        private CharSequence mNegativeText;
        private CharSequence mNeutralText;
        private boolean mCancelable = true;

        public Builder(Context context) { mContext = context; }

        public Builder setTitle(CharSequence title) { mTitle = title; return this; }
        public Builder setTitle(int titleId) { return this; }
        public Builder setMessage(CharSequence message) { mMessage = message; return this; }
        public Builder setMessage(int messageId) { return this; }
        public Builder setView(View view) { mView = view; return this; }
        public Builder setIcon(int iconId) { return this; }
        public Builder setCancelable(boolean cancelable) { mCancelable = cancelable; return this; }

        public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            mPositiveText = text; return this;
        }
        public Builder setPositiveButton(int textId, DialogInterface.OnClickListener listener) { return this; }
        public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            mNegativeText = text; return this;
        }
        public Builder setNegativeButton(int textId, DialogInterface.OnClickListener listener) { return this; }
        public Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
            mNeutralText = text; return this;
        }
        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener listener) { return this; }
        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener) { return this; }
        public Builder setSingleChoiceItems(android.widget.ListAdapter adapter, int checkedItem, DialogInterface.OnClickListener listener) { return this; }
        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) { return this; }

        public AlertDialog create() {
            AlertDialog d = new AlertDialog();
            d.mTitle = mTitle;
            d.mMessage = mMessage;
            d.mView = mView;
            d.mPositiveText = mPositiveText;
            d.mNegativeText = mNegativeText;
            d.mNeutralText = mNeutralText;
            d.mCancelable = mCancelable;
            return d;
        }

        public AlertDialog show() {
            AlertDialog d = create();
            d.show();
            return d;
        }
    }
}
