package android.view;

/**
 * Shim: android.view.MenuItem interface.
 * Represents a single item in a Menu.
 */
public interface MenuItem {

    int getItemId();
    int getGroupId();
    int getOrder();
    CharSequence getTitle();
    MenuItem setTitle(CharSequence title);
    MenuItem setTitle(int title);
    MenuItem setIcon(int iconResId);
    MenuItem setIcon(android.graphics.drawable.Drawable icon);
    android.graphics.drawable.Drawable getIcon();
    MenuItem setVisible(boolean visible);
    boolean isVisible();
    MenuItem setEnabled(boolean enabled);
    boolean isEnabled();
    boolean isChecked();
    MenuItem setChecked(boolean checked);
    MenuItem setCheckable(boolean checkable);
    boolean isCheckable();
    boolean hasSubMenu();
    SubMenu getSubMenu();
    android.content.Intent getIntent();
    MenuItem setIntent(android.content.Intent intent);
    char getAlphabeticShortcut();
    MenuItem setAlphabeticShortcut(char alphaChar);
    char getNumericShortcut();
    MenuItem setNumericShortcut(char numericChar);
    MenuItem setShortcut(char numericChar, char alphaChar);
    ContextMenu.ContextMenuInfo getMenuInfo();
    void setShowAsAction(int actionEnum);
    MenuItem setShowAsActionFlags(int actionEnum);
    MenuItem setActionView(View view);
    MenuItem setActionView(int resId);
    View getActionView();
    MenuItem setActionProvider(ActionProvider provider);
    ActionProvider getActionProvider();
    boolean expandActionView();
    boolean collapseActionView();
    boolean isActionViewExpanded();
    MenuItem setOnMenuItemClickListener(OnMenuItemClickListener listener);
    MenuItem setOnActionExpandListener(OnActionExpandListener listener);
    MenuItem setContentDescription(CharSequence contentDescription);
    CharSequence getContentDescription();
    MenuItem setTooltipText(CharSequence tooltipText);
    CharSequence getTooltipText();
    MenuItem setIconTintList(android.content.res.ColorStateList tint);
    android.content.res.ColorStateList getIconTintList();
    MenuItem setIconTintMode(android.graphics.PorterDuff.Mode mode);
    android.graphics.PorterDuff.Mode getIconTintMode();
    default MenuItem setAlphabeticShortcut(char alphaChar, int alphaModifiers) { return setAlphabeticShortcut(alphaChar); }
    default int getAlphabeticModifiers() { return 0; }
    default MenuItem setNumericShortcut(char numericChar, int numericModifiers) { return setNumericShortcut(numericChar); }
    default int getNumericModifiers() { return 0; }
    default MenuItem setShortcut(char numericChar, char alphaChar, int numericModifiers, int alphaModifiers) { return setShortcut(numericChar, alphaChar); }

    public static final int SHOW_AS_ACTION_NEVER = 0;
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;
    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;
    public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem item);
    }

    public interface OnActionExpandListener {
        boolean onMenuItemActionExpand(MenuItem item);
        boolean onMenuItemActionCollapse(MenuItem item);
    }
}
