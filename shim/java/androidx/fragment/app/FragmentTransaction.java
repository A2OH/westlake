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

    // R8/minified AndroidX aliases used by the real McD APK.
    public FragmentTransaction b(int containerViewId, Fragment fragment) {
        return add(containerViewId, fragment);
    }

    public FragmentTransaction c(int containerViewId, Fragment fragment, String tag) {
        return add(containerViewId, fragment, tag);
    }

    public FragmentTransaction e(Fragment fragment, String tag) {
        return add(fragment, tag);
    }

    // ── replace ──

    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment);

    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment, String tag);

    public FragmentTransaction t(int containerViewId, Fragment fragment) {
        return replace(containerViewId, fragment);
    }

    public FragmentTransaction u(int containerViewId, Fragment fragment, String tag) {
        return replace(containerViewId, fragment, tag);
    }

    // ── remove ──

    public abstract FragmentTransaction remove(Fragment fragment);

    public FragmentTransaction s(Fragment fragment) {
        return remove(fragment);
    }

    // ── hide / show ──

    public abstract FragmentTransaction hide(Fragment fragment);

    public abstract FragmentTransaction show(Fragment fragment);

    public FragmentTransaction q(Fragment fragment) {
        return hide(fragment);
    }

    public FragmentTransaction B(Fragment fragment) {
        return show(fragment);
    }

    // ── attach / detach ──

    public abstract FragmentTransaction attach(Fragment fragment);

    public abstract FragmentTransaction detach(Fragment fragment);

    public FragmentTransaction i(Fragment fragment) {
        return attach(fragment);
    }

    public FragmentTransaction n(Fragment fragment) {
        return detach(fragment);
    }

    // ── back stack ──

    public abstract FragmentTransaction addToBackStack(String name);

    public FragmentTransaction h(String name) {
        return addToBackStack(name);
    }

    // ── commit ──

    public abstract int commit();

    public abstract int commitAllowingStateLoss();

    public abstract void commitNow();

    public void commitNowAllowingStateLoss() { commitNow(); }

    public int j() { return commit(); }

    public int k() { return commitAllowingStateLoss(); }

    public void l() { commitNow(); }

    public void m() { commitNowAllowingStateLoss(); }

    // ── Animation / transition stubs ──

    public FragmentTransaction setTransition(int transit) { return this; }

    public FragmentTransaction setCustomAnimations(int enter, int exit) { return this; }

    public FragmentTransaction w(int enter, int exit) {
        return setCustomAnimations(enter, exit);
    }

    public FragmentTransaction setCustomAnimations(int enter, int exit,
                                                    int popEnter, int popExit) {
        return this;
    }

    public FragmentTransaction x(int enter, int exit, int popEnter, int popExit) {
        return setCustomAnimations(enter, exit, popEnter, popExit);
    }

    public FragmentTransaction setPrimaryNavigationFragment(Fragment fragment) { return this; }

    public FragmentTransaction z(Fragment fragment) {
        return setPrimaryNavigationFragment(fragment);
    }

    public FragmentTransaction setReorderingAllowed(boolean reorderingAllowed) { return this; }

    public FragmentTransaction A(boolean reorderingAllowed) {
        return setReorderingAllowed(reorderingAllowed);
    }

    public FragmentTransaction setMaxLifecycle(Fragment fragment, Object state) { return this; }

    public FragmentTransaction y(Fragment fragment, Object state) {
        return setMaxLifecycle(fragment, state);
    }

    public FragmentTransaction runOnCommit(Runnable runnable) {
        if (runnable != null) runnable.run();
        return this;
    }

    public FragmentTransaction v(boolean allowStateLoss, Runnable runnable) {
        return runOnCommit(runnable);
    }

    public FragmentTransaction g(android.view.View view, String name) { return this; }

    public boolean isAddToBackStackAllowed() { return true; }

    public FragmentTransaction disallowAddToBackStack() { return this; }

    public FragmentTransaction o() { return disallowAddToBackStack(); }

    public boolean isEmpty() { return true; }

    public boolean r() { return isEmpty(); }
}
