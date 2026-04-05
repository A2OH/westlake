package androidx.fragment.app;

import java.util.ArrayList;
import java.util.List;

/**
 * AndroidX FragmentTransaction stub. Abstract class defining the transaction API.
 * The concrete implementation is {@link FragmentTransactionImpl}.
 */
public abstract class FragmentTransaction {

    static final int OP_ADD = 1;
    static final int OP_REPLACE = 2;
    static final int OP_REMOVE = 3;
    static final int OP_HIDE = 4;
    static final int OP_SHOW = 5;
    static final int OP_DETACH = 6;
    static final int OP_ATTACH = 7;

    public static final int TRANSIT_NONE = 0;
    public static final int TRANSIT_FRAGMENT_OPEN = 1 | 0x2000;
    public static final int TRANSIT_FRAGMENT_CLOSE = 2 | 0x2000;
    public static final int TRANSIT_FRAGMENT_FADE = 3 | 0x2000;

    static class Op {
        int cmd;
        Fragment fragment;
        String tag;
        int containerId;
        List<Fragment> removed;
    }

    // ── add ──

    public abstract FragmentTransaction add(int containerViewId, Fragment fragment);

    public abstract FragmentTransaction add(int containerViewId, Fragment fragment, String tag);

    public FragmentTransaction add(Fragment fragment, String tag) {
        return add(0, fragment, tag);
    }

    // ── replace ──

    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment);

    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment, String tag);

    // ── remove ──

    public abstract FragmentTransaction remove(Fragment fragment);

    // ── hide / show ──

    public abstract FragmentTransaction hide(Fragment fragment);

    public abstract FragmentTransaction show(Fragment fragment);

    // ── attach / detach ──

    public abstract FragmentTransaction attach(Fragment fragment);

    public abstract FragmentTransaction detach(Fragment fragment);

    // ── back stack ──

    public abstract FragmentTransaction addToBackStack(String name);

    // ── commit ──

    public abstract int commit();

    public abstract int commitAllowingStateLoss();

    public abstract void commitNow();

    public void commitNowAllowingStateLoss() { commitNow(); }

    // ── Animation / transition stubs ──

    public FragmentTransaction setTransition(int transit) { return this; }

    public FragmentTransaction setCustomAnimations(int enter, int exit) { return this; }

    public FragmentTransaction setCustomAnimations(int enter, int exit,
                                                    int popEnter, int popExit) {
        return this;
    }

    public FragmentTransaction setPrimaryNavigationFragment(Fragment fragment) { return this; }

    public FragmentTransaction setReorderingAllowed(boolean reorderingAllowed) { return this; }

    public FragmentTransaction setMaxLifecycle(Fragment fragment, Object state) { return this; }

    public FragmentTransaction runOnCommit(Runnable runnable) {
        if (runnable != null) runnable.run();
        return this;
    }

    public boolean isAddToBackStackAllowed() { return true; }

    public FragmentTransaction disallowAddToBackStack() { return this; }

    public boolean isEmpty() { return true; }
}
