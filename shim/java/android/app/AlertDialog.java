package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.View;

/**
 * Shim: android.app.AlertDialog
 * Provides Builder pattern for constructing dialogs.
 */
public class AlertDialog implements DialogInterface {
    private Context mContext;
    private int mThemeResId;
    private CharSequence mTitle;
    private CharSequence mMessage;
    private View mView;
    private CharSequence mPositiveText;
    private CharSequence mNegativeText;
    private CharSequence mNeutralText;
    private DialogInterface.OnClickListener mPositiveListener;
    private DialogInterface.OnClickListener mNegativeListener;
    private DialogInterface.OnClickListener mNeutralListener;
    private DialogInterface.OnClickListener mItemListener;
    private DialogInterface.OnMultiChoiceClickListener mMultiChoiceListener;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private DialogInterface.OnShowListener mOnShowListener;
    private DialogInterface.OnKeyListener mOnKeyListener;
    private CharSequence[] mItems;
    private boolean[] mCheckedItems;
    private android.widget.ListAdapter mListAdapter;
    private android.widget.ListView mListView;
    private int mCheckedItem = -1;
    private boolean mCancelable = true;
    private boolean mShowing = false;

    public AlertDialog() {}

    public AlertDialog(Context context) {
        mContext = context;
    }

    public AlertDialog(Context context, int themeResId) {
        mContext = context;
        mThemeResId = themeResId;
    }

    public Context getContext() { return mContext; }
    public CharSequence getTitle() { return mTitle; }
    public CharSequence getMessage() { return mMessage; }
    public boolean isCancelable() { return mCancelable; }

    public android.widget.Button getButton(int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                return createButton(mPositiveText, mPositiveListener, whichButton);
            case DialogInterface.BUTTON_NEGATIVE:
                return createButton(mNegativeText, mNegativeListener, whichButton);
            case DialogInterface.BUTTON_NEUTRAL:
                return createButton(mNeutralText, mNeutralListener, whichButton);
            default:
                return null;
        }
    }
    public Object getButton(Object p0) { return null; }
    public android.widget.ListView getListView() {
        if (mListView == null) {
            mListView = new android.widget.ListView(safeContext(mContext));
            if (mListAdapter != null) {
                mListView.setAdapter(mListAdapter);
            }
        }
        return mListView;
    }
    public void setButton(Object p0, Object p1, Object p2) {}
    public void setButton(int whichButton, CharSequence text,
            DialogInterface.OnClickListener listener) {
        setButtonInternal(whichButton, text, listener);
    }
    public void setButton(int whichButton, CharSequence text, android.os.Message msg) {
        setButtonInternal(whichButton, text, null);
    }
    public void setCustomTitle(Object p0) {}
    public void setIcon(Object p0) {}
    public void setIconAttribute(Object p0) {}
    public void setInverseBackgroundForced(Object p0) {}
    public void setMessage(Object p0) { if (p0 instanceof CharSequence) mMessage = (CharSequence) p0; }
    public void setTitle(CharSequence title) { mTitle = title; }
    public void setView(Object p0) { if (p0 instanceof View) mView = (View) p0; }
    public void setView(Object p0, Object p1, Object p2, Object p3, Object p4) {
        if (p0 instanceof View) mView = (View) p0;
    }
    public boolean isShowing() { return mShowing; }
    public void show() {
        mShowing = true;
        if (mOnShowListener != null) {
            mOnShowListener.onShow(this);
        }
    }
    public void dismiss() {
        boolean wasShowing = mShowing;
        mShowing = false;
        if (wasShowing && mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }
    }
    public void cancel() {
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(this);
        }
        dismiss();
    }
    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mOnCancelListener = listener;
    }
    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }
    public void setOnShowListener(DialogInterface.OnShowListener listener) {
        mOnShowListener = listener;
    }
    public void setOnKeyListener(DialogInterface.OnKeyListener listener) {
        mOnKeyListener = listener;
    }
    public android.view.Window getWindow() { return null; }

    private static Context safeContext(Context context) {
        return context != null ? context : new Context();
    }

    private android.widget.Button createButton(CharSequence text,
            final DialogInterface.OnClickListener listener, final int whichButton) {
        if (text == null && listener == null) {
            return null;
        }
        android.widget.Button button = new android.widget.Button(safeContext(mContext));
        if (text != null) {
            button.setText(text);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(AlertDialog.this, whichButton);
                }
                dismiss();
            }
        });
        return button;
    }

    private void setButtonInternal(int whichButton, CharSequence text,
            DialogInterface.OnClickListener listener) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                mPositiveText = text;
                mPositiveListener = listener;
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mNegativeText = text;
                mNegativeListener = listener;
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                mNeutralText = text;
                mNeutralListener = listener;
                break;
            default:
                break;
        }
    }

    public static class Builder {
        private Context mContext;
        private int mThemeResId;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private View mView;
        private int mViewLayoutResId;
        private CharSequence mPositiveText;
        private CharSequence mNegativeText;
        private CharSequence mNeutralText;
        private DialogInterface.OnClickListener mPositiveListener;
        private DialogInterface.OnClickListener mNegativeListener;
        private DialogInterface.OnClickListener mNeutralListener;
        private DialogInterface.OnClickListener mItemListener;
        private DialogInterface.OnMultiChoiceClickListener mMultiChoiceListener;
        private DialogInterface.OnCancelListener mOnCancelListener;
        private DialogInterface.OnDismissListener mOnDismissListener;
        private DialogInterface.OnShowListener mOnShowListener;
        private DialogInterface.OnKeyListener mOnKeyListener;
        private CharSequence[] mItems;
        private boolean[] mCheckedItems;
        private android.widget.ListAdapter mListAdapter;
        private int mCheckedItem = -1;
        private boolean mCancelable = true;

        public Builder(Context context) {
            this(context, 0);
        }

        public Builder(Context context, int themeResId) {
            mThemeResId = themeResId;
            mContext = themeResId != 0 ? new ContextThemeWrapper(context, themeResId) : context;
        }

        public Context getContext() { return mContext; }

        public Builder setTitle(CharSequence title) { mTitle = title; return this; }
        public Builder setTitle(int titleId) { mTitle = getText(titleId); return this; }
        public Builder setMessage(CharSequence message) { mMessage = message; return this; }
        public Builder setMessage(int messageId) { mMessage = getText(messageId); return this; }
        public Builder setView(View view) { mView = view; return this; }
        public Builder setView(int layoutResId) { mViewLayoutResId = layoutResId; return this; }
        public Builder setCustomTitle(View customTitleView) { return this; }
        public Builder setIcon(int iconId) { return this; }
        public Builder setIcon(android.graphics.drawable.Drawable icon) { return this; }
        public Builder setIconAttribute(int attrId) { return this; }
        public Builder setCancelable(boolean cancelable) { mCancelable = cancelable; return this; }

        public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            mPositiveText = text; mPositiveListener = listener; return this;
        }
        public Builder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
            return setPositiveButton(getText(textId), listener);
        }
        public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            mNegativeText = text; mNegativeListener = listener; return this;
        }
        public Builder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
            return setNegativeButton(getText(textId), listener);
        }
        public Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
            mNeutralText = text; mNeutralListener = listener; return this;
        }
        public Builder setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
            return setNeutralButton(getText(textId), listener);
        }
        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
            mItems = items; mItemListener = listener; return this;
        }
        public Builder setItems(int itemsId, DialogInterface.OnClickListener listener) {
            return setItems(getTextArray(itemsId), listener);
        }
        public Builder setAdapter(android.widget.ListAdapter adapter,
                DialogInterface.OnClickListener listener) {
            mListAdapter = adapter; mItemListener = listener; return this;
        }
        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem,
                DialogInterface.OnClickListener listener) {
            mItems = items; mCheckedItem = checkedItem; mItemListener = listener; return this;
        }
        public Builder setSingleChoiceItems(int itemsId, int checkedItem,
                DialogInterface.OnClickListener listener) {
            return setSingleChoiceItems(getTextArray(itemsId), checkedItem, listener);
        }
        public Builder setSingleChoiceItems(android.widget.ListAdapter adapter, int checkedItem,
                DialogInterface.OnClickListener listener) {
            mListAdapter = adapter; mCheckedItem = checkedItem; mItemListener = listener; return this;
        }
        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                DialogInterface.OnMultiChoiceClickListener listener) {
            mItems = items; mCheckedItems = checkedItems; mMultiChoiceListener = listener; return this;
        }
        public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems,
                DialogInterface.OnMultiChoiceClickListener listener) {
            return setMultiChoiceItems(getTextArray(itemsId), checkedItems, listener);
        }
        public Builder setOnCancelListener(DialogInterface.OnCancelListener listener) {
            mOnCancelListener = listener; return this;
        }
        public Builder setOnDismissListener(DialogInterface.OnDismissListener listener) {
            mOnDismissListener = listener; return this;
        }
        public Builder setOnKeyListener(DialogInterface.OnKeyListener listener) {
            mOnKeyListener = listener; return this;
        }
        public Builder setOnShowListener(DialogInterface.OnShowListener listener) {
            mOnShowListener = listener; return this;
        }

        public AlertDialog create() {
            AlertDialog d = new AlertDialog(mContext, mThemeResId);
            d.mTitle = mTitle;
            d.mMessage = mMessage;
            d.mView = mView;
            d.mThemeResId = mThemeResId;
            d.mPositiveText = mPositiveText;
            d.mNegativeText = mNegativeText;
            d.mNeutralText = mNeutralText;
            d.mPositiveListener = mPositiveListener;
            d.mNegativeListener = mNegativeListener;
            d.mNeutralListener = mNeutralListener;
            d.mItemListener = mItemListener;
            d.mMultiChoiceListener = mMultiChoiceListener;
            d.mOnCancelListener = mOnCancelListener;
            d.mOnDismissListener = mOnDismissListener;
            d.mOnShowListener = mOnShowListener;
            d.mOnKeyListener = mOnKeyListener;
            d.mItems = mItems;
            d.mCheckedItems = mCheckedItems;
            d.mListAdapter = mListAdapter != null ? mListAdapter : createAdapter(mItems);
            d.mCheckedItem = mCheckedItem;
            d.mCancelable = mCancelable;
            return d;
        }

        public AlertDialog show() {
            AlertDialog d = create();
            d.show();
            return d;
        }

        private CharSequence getText(int resId) {
            try {
                Context context = mContext != null ? mContext : new Context();
                return context.getText(resId);
            } catch (Throwable t) {
                return "";
            }
        }

        private CharSequence[] getTextArray(int resId) {
            try {
                Context context = mContext != null ? mContext : new Context();
                return context.getResources().getTextArray(resId);
            } catch (Throwable t) {
                return new CharSequence[0];
            }
        }

        private android.widget.ListAdapter createAdapter(CharSequence[] items) {
            if (items == null) {
                return null;
            }
            Context context = mContext != null ? mContext : new Context();
            return new android.widget.ArrayAdapter<CharSequence>(context, 0, items);
        }
    }
}
