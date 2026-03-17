package android.content;

public interface DialogInterface {

    public static final int BUTTON_POSITIVE = -1;
    public static final int BUTTON_NEGATIVE = -2;
    public static final int BUTTON_NEUTRAL = -3;

    void cancel();
    void dismiss();

    public interface OnClickListener {
        void onClick(DialogInterface dialog, int which);
    }

    public interface OnMultiChoiceClickListener {
        void onClick(DialogInterface dialog, int which, boolean isChecked);
    }

    public interface OnCancelListener {
        void onCancel(DialogInterface dialog);
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }

    public interface OnShowListener {
        void onShow(DialogInterface dialog);
    }

    public interface OnKeyListener {
        boolean onKey(DialogInterface dialog, int keyCode, android.view.KeyEvent event);
    }
}
