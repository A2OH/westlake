package androidx.fragment.app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.westlake.engine.WestlakeLauncher;

import java.util.ArrayList;
import java.util.List;

/**
 * AndroidX FragmentManager stub. Manages a list of Fragments and supports
 * transactions and back-stack operations.
 */
public class FragmentManager {
    private static final String TAG = "WestlakeFragmentMgr";
    private static final int MCD_SIMPLE_PRODUCT_HOLDER_ID = 0x7f0b171c;
    private static final int MCD_PDP_PLUS_ID = 0x7f0b12a5;
    private static final int MCD_PDP_MINUS_ID = 0x7f0b12a2;
    private static final int MCD_PDP_QUANTITY_ID = 0x7f0b12a9;
    private static final String MCD_ORDER_PDP_FRAGMENT =
            "com.mcdonalds.pdpredesign.presentation.ui.OrderPDPFragment";

    final List<Fragment> mAdded = new ArrayList<>();
    private final List<BackStackRecord> mBackStack = new ArrayList<>();
    private final List<OnBackStackChangedListener> mBackStackListeners = new ArrayList<>();
    FragmentActivity mHost;

    public static final int POP_BACK_STACK_INCLUSIVE = 1;

    // ── Inner types ──

    public interface BackStackEntry {
        int getId();
        String getName();
    }

    public interface OnBackStackChangedListener {
        void onBackStackChanged();
    }

    /**
     * Callback interface for listening to fragment lifecycle events.
     * This is the static inner class referenced as FragmentManager.FragmentLifecycleCallbacks.
     */
    public static abstract class FragmentLifecycleCallbacks {
        public void onFragmentAttached(FragmentManager fm, Fragment f, android.content.Context context) {}
        public void onFragmentCreated(FragmentManager fm, Fragment f, android.os.Bundle savedInstanceState) {}
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, android.view.View v, android.os.Bundle savedInstanceState) {}
        public void onFragmentStarted(FragmentManager fm, Fragment f) {}
        public void onFragmentResumed(FragmentManager fm, Fragment f) {}
        public void onFragmentPaused(FragmentManager fm, Fragment f) {}
        public void onFragmentStopped(FragmentManager fm, Fragment f) {}
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {}
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {}
        public void onFragmentDetached(FragmentManager fm, Fragment f) {}
    }

    static class BackStackRecord implements BackStackEntry {
        int mId;
        String mName;
        List<FragmentTransaction.Op> mOps;

        BackStackRecord(int id, String name, List<FragmentTransaction.Op> ops) {
            mId = id;
            mName = name;
            mOps = ops;
        }

        public int getId() { return mId; }
        public String getName() { return mName; }
    }

    // ── Construction ──

    public FragmentManager() {}

    void setHost(FragmentActivity host) { mHost = host; }

    // ── Transaction ──

    public FragmentTransaction beginTransaction() {
        FragmentTransaction tx = new FragmentTransactionImpl(this);
        note("FragmentManager beginTransaction managerId=" + System.identityHashCode(this)
                + " host=" + (mHost != null)
                + " txId=" + System.identityHashCode(tx)
                + " txClass=" + tx.getClass().getName());
        return tx;
    }

    // R8/minified AndroidX FragmentManager aliases used by the real McD APK.
    public FragmentTransaction s() {
        note("FragmentManager alias s/beginTransaction");
        return beginTransaction();
    }

    private static void note(String marker) {
        try {
            WestlakeLauncher.noteMarker("CV " + marker);
        } catch (Throwable ignored) {
        }
    }

    // ── Lookup ──

    public Fragment findFragmentById(int id) {
        for (int i = mAdded.size() - 1; i >= 0; i--) {
            Fragment f = mAdded.get(i);
            if (f == null) {
                continue;
            }
            if (f.getId() == id) return f;
        }
        return null;
    }

    public Fragment p0(int id) {
        note("FragmentManager alias p0/findFragmentById id=0x" + Integer.toHexString(id));
        return findFragmentById(id);
    }

    public Fragment findFragmentByTag(String tag) {
        note("FragmentManager findFragmentByTag enter");
        if (tag == null) {
            note("FragmentManager findFragmentByTag null tag");
            return null;
        }
        List<Fragment> added = mAdded;
        note("FragmentManager findFragmentByTag before size");
        int count = added != null ? added.size() : -1;
        note("FragmentManager findFragmentByTag size=" + count);
        for (int i = count - 1; i >= 0; i--) {
            note("FragmentManager findFragmentByTag before get i=" + i);
            Fragment f = added.get(i);
            note("FragmentManager findFragmentByTag after get null=" + (f == null));
            if (f == null) {
                continue;
            }
            note("FragmentManager findFragmentByTag before compare");
            if (sameTag(tag, f.mTag)) {
                note("FragmentManager findFragmentByTag matched");
                return f;
            }
        }
        note("FragmentManager findFragmentByTag missing");
        return null;
    }

    public Fragment q0(String tag) {
        note("FragmentManager alias q0/findFragmentByTag");
        return findFragmentByTag(tag);
    }

    private static boolean sameTag(String requested, String existing) {
        note("FragmentManager sameTag enter");
        if (requested == existing) {
            note("FragmentManager sameTag identity");
            return true;
        }
        if (requested == null || existing == null) {
            note("FragmentManager sameTag null");
            return false;
        }
        int length = requested.length();
        if (length != existing.length()) {
            note("FragmentManager sameTag length mismatch");
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (requested.charAt(i) != existing.charAt(i)) {
                note("FragmentManager sameTag char mismatch");
                return false;
            }
        }
        note("FragmentManager sameTag content");
        return true;
    }

    public List<Fragment> getFragments() {
        return new ArrayList<>(mAdded);
    }

    public List<Fragment> G0() {
        note("FragmentManager alias G0/getFragments size=" + mAdded.size());
        return getFragments();
    }

    // ── Back stack ──

    public int getBackStackEntryCount() { return mBackStack.size(); }

    public int z0() { return getBackStackEntryCount(); }

    public BackStackEntry getBackStackEntryAt(int index) {
        return mBackStack.get(index);
    }

    public BackStackEntry y0(int index) { return getBackStackEntryAt(index); }

    public void popBackStack() {
        if (mBackStack.isEmpty()) return;
        BackStackRecord record = mBackStack.remove(mBackStack.size() - 1);
        if (record.mOps != null) {
            for (int i = record.mOps.size() - 1; i >= 0; i--) {
                FragmentTransaction.Op op = record.mOps.get(i);
                switch (op.cmd) {
                    case FragmentTransaction.OP_ADD:
                        removeFragmentInternal(op.fragment);
                        break;
                    case FragmentTransaction.OP_REMOVE:
                        addFragmentInternal(op.fragment, op.tag, op.containerId);
                        break;
                    case FragmentTransaction.OP_REPLACE:
                        removeFragmentInternal(op.fragment);
                        if (op.removed != null) {
                            for (Fragment old : op.removed) {
                                addFragmentInternal(old, old.mTag, old.mContainerId);
                            }
                        }
                        break;
                    case FragmentTransaction.OP_HIDE:
                        op.fragment.mHidden = false;
                        op.fragment.onHiddenChanged(false);
                        break;
                    case FragmentTransaction.OP_SHOW:
                        op.fragment.mHidden = true;
                        op.fragment.onHiddenChanged(true);
                        break;
                    case FragmentTransaction.OP_DETACH:
                        op.fragment.mDetached = false;
                        break;
                    case FragmentTransaction.OP_ATTACH:
                        op.fragment.mDetached = true;
                        break;
                }
            }
        }
        notifyBackStackChanged();
    }

    public void p1() { popBackStack(); }

    public void popBackStack(String name, int flags) {
        if (name == null && flags == POP_BACK_STACK_INCLUSIVE) {
            while (!mBackStack.isEmpty()) {
                popBackStack();
            }
        } else {
            popBackStack();
        }
    }

    public void r1(String name, int flags) { popBackStack(name, flags); }

    public void popBackStack(int id, int flags) {
        popBackStack();
    }

    public boolean popBackStackImmediate() {
        if (mBackStack.isEmpty()) return false;
        popBackStack();
        return true;
    }

    public boolean popBackStackImmediate(String name, int flags) {
        if (mBackStack.isEmpty()) return false;
        popBackStack(name, flags);
        return true;
    }

    public boolean u1(String name, int flags) { return popBackStackImmediate(name, flags); }

    public boolean popBackStackImmediate(int id, int flags) {
        return popBackStackImmediate();
    }

    // ── Listeners ──

    public void addOnBackStackChangedListener(OnBackStackChangedListener listener) {
        mBackStackListeners.add(listener);
    }

    public void removeOnBackStackChangedListener(OnBackStackChangedListener listener) {
        mBackStackListeners.remove(listener);
    }

    private void notifyBackStackChanged() {
        for (int i = 0; i < mBackStackListeners.size(); i++) {
            mBackStackListeners.get(i).onBackStackChanged();
        }
    }

    // ── Status ──

    public boolean isDestroyed() { return false; }

    public boolean isStateSaved() { return false; }

    public boolean b1() { return isStateSaved(); }

    public boolean T0() { return isDestroyed(); }

    public boolean executePendingTransactions() { return true; }

    // ── Lifecycle callbacks registration ──

    private final List<FragmentLifecycleCallbacks> mLifecycleCallbacks = new ArrayList<>();

    public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks cb, boolean recursive) {
        mLifecycleCallbacks.add(cb);
    }

    public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks cb) {
        mLifecycleCallbacks.remove(cb);
    }

    // ── Internal fragment management ──

    private ViewGroup resolveContainer(int containerId) {
        if (mHost == null || containerId == 0) {
            return null;
        }
        try {
            View container = mHost.findViewById(containerId);
            if (container instanceof ViewGroup) {
                return (ViewGroup) container;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private LayoutInflater resolveInflater(Fragment f) {
        if (f == null) {
            return mHost != null ? mHost.getLayoutInflater() : null;
        }
        try {
            LayoutInflater inflater = f.onGetLayoutInflater(null);
            if (inflater != null) {
                return inflater;
            }
        } catch (Throwable ignored) {
        }
        return mHost != null ? mHost.getLayoutInflater() : null;
    }

    void addFragmentInternal(Fragment f, String tag, int containerId) {
        note("FragmentManager addFragmentInternal enter managerId=" + System.identityHashCode(this)
                + " fragment=" + (f != null)
                + " tag=" + tag
                + " containerId=0x" + Integer.toHexString(containerId));
        if (mHost == null) {
            note("FragmentManager addFragmentInternal host null");
            throw new IllegalStateException("FragmentManager.addFragmentInternal host=null for " + f);
        }
        f.mTag = tag;
        f.mContainerId = containerId;
        f.mActivity = mHost;
        f.mHost = mHost;
        f.mFragmentManager = this;
        f.mAdded = true;
        mAdded.add(f);
        ViewGroup container = resolveContainer(containerId);
        boolean mcdPdpTarget = isMcdPdpTarget(f, containerId);
        if (mcdPdpTarget) {
            proof("MCD_PDP_FRAGMENT_ADD_BEGIN fragment=" + safeToken(fragmentName(f))
                    + " tag=" + safeToken(tag)
                    + " container=0x" + Integer.toHexString(containerId)
                    + " host=" + safeToken(mHost.getClass().getName()));
            proof("MCD_PDP_FRAGMENT_CONTAINER_RESOLVED container=0x"
                    + Integer.toHexString(containerId)
                    + " found=" + (container != null)
                    + " view=" + safeToken(viewSummary(container)));
        }
        try {
            note("FragmentManager addFragmentInternal before performAttach");
            Log.i(TAG, "addFragmentInternal before performAttach fragment=" + f
                    + " host=" + mHost + " containerId=0x" + Integer.toHexString(containerId));
            f.performAttach(mHost);
            note("FragmentManager addFragmentInternal after performAttach");
            Log.i(TAG, "addFragmentInternal after performAttach fragment=" + f);
        } catch (Throwable t) {
            note("FragmentManager addFragmentInternal performAttach err "
                    + t.getClass().getName());
            throw new RuntimeException("FragmentManager.addFragmentInternal:performAttach " + f, t);
        }
        LayoutInflater inflater;
        try {
            note("FragmentManager addFragmentInternal before resolveInflater");
            Log.i(TAG, "addFragmentInternal before resolveInflater fragment=" + f);
            inflater = resolveInflater(f);
            note("FragmentManager addFragmentInternal after resolveInflater inflater="
                    + (inflater != null));
            Log.i(TAG, "addFragmentInternal after resolveInflater fragment=" + f
                    + " inflater=" + inflater);
        } catch (Throwable t) {
            note("FragmentManager addFragmentInternal resolveInflater err "
                    + t.getClass().getName());
            throw new RuntimeException("FragmentManager.addFragmentInternal:resolveInflater " + f, t);
        }
        try {
            note("FragmentManager addFragmentInternal before performCreate");
            Log.i(TAG, "addFragmentInternal before performCreate fragment=" + f);
            f.performCreate(null);
            note("FragmentManager addFragmentInternal after performCreate");
            Log.i(TAG, "addFragmentInternal after performCreate fragment=" + f);
        } catch (Throwable t) {
            note("FragmentManager addFragmentInternal performCreate err "
                    + t.getClass().getName());
            throw new RuntimeException("FragmentManager.addFragmentInternal:performCreate " + f, t);
        }
        try {
            note("FragmentManager addFragmentInternal before performCreateView container="
                    + (container != null));
            Log.i(TAG, "addFragmentInternal before performCreateView fragment=" + f
                    + " container=" + container);
            f.performCreateView(inflater, container, null);
            note("FragmentManager addFragmentInternal after performCreateView view="
                    + (f.mView != null));
            if (mcdPdpTarget) {
                proof("MCD_PDP_FRAGMENT_VIEW_CREATED fragment=" + safeToken(fragmentName(f))
                        + " container=0x" + Integer.toHexString(containerId)
                        + " view=" + safeToken(viewSummary(f.mView)));
            }
            Log.i(TAG, "addFragmentInternal after performCreateView fragment=" + f
                    + " view=" + f.mView);
        } catch (Throwable t) {
            note("FragmentManager addFragmentInternal performCreateView err "
                    + t.getClass().getName());
            throw new RuntimeException("FragmentManager.addFragmentInternal:performCreateView " + f, t);
        }
        if (container != null && f.mView != null) {
            try {
                note("FragmentManager addFragmentInternal before addView");
                if (f.mView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) f.mView.getParent()).removeView(f.mView);
                }
                if (mcdPdpTarget) {
                    try {
                        container.setVisibility(View.VISIBLE);
                    } catch (Throwable ignored) {
                    }
                    proof("MCD_PDP_FRAGMENT_ATTACH_ATTEMPT fragment=" + safeToken(fragmentName(f))
                            + " container=0x" + Integer.toHexString(containerId)
                            + " containerView=" + safeToken(viewSummary(container))
                            + " fragmentView=" + safeToken(viewSummary(f.mView)));
                }
                prepareFragmentViewForContainer(f.mView, container);
                container.addView(f.mView, fullSizeLayoutParamsFor(container));
                note("FragmentManager addFragmentInternal after addView childCount="
                        + container.getChildCount());
                Log.i(TAG, "addFragmentInternal attached view fragment=" + f
                        + " childCount=" + container.getChildCount());
                proof("MCD_FRAGMENT_VIEW_ATTACHED fragment=" + safeToken(fragmentName(f))
                        + " container=" + safeToken(viewSummary(container))
                        + " view=" + safeToken(viewSummary(f.mView))
                        + " childCount=" + container.getChildCount());
                if (mcdPdpTarget) {
                    proof("MCD_PDP_FRAGMENT_VIEW_ATTACHED fragment=" + safeToken(fragmentName(f))
                            + " container=0x" + Integer.toHexString(containerId)
                            + " parentMatches=" + (f.mView.getParent() == container)
                            + " childCount=" + container.getChildCount()
                            + " containerView=" + safeToken(viewSummary(container))
                            + " fragmentView=" + safeToken(viewSummary(f.mView)));
                }
            } catch (Throwable t) {
                note("FragmentManager addFragmentInternal addView err "
                        + t.getClass().getName());
                try {
                    prepareFragmentViewForContainer(f.mView, container);
                    container.installStandaloneChild(f.mView);
                    note("FragmentManager addFragmentInternal standalone child fallback childCount="
                            + container.getChildCount());
                    Log.i(TAG, "addFragmentInternal attached view via standalone fallback fragment=" + f
                            + " childCount=" + container.getChildCount());
                    proof("MCD_FRAGMENT_VIEW_STANDALONE fragment=" + safeToken(fragmentName(f))
                            + " container=" + safeToken(viewSummary(container))
                            + " view=" + safeToken(viewSummary(f.mView))
                            + " childCount=" + container.getChildCount());
                    if (mcdPdpTarget) {
                        proof("MCD_PDP_FRAGMENT_VIEW_ATTACHED_STANDALONE fragment="
                                + safeToken(fragmentName(f))
                                + " container=0x" + Integer.toHexString(containerId)
                                + " parentMatches=" + (f.mView.getParent() == container)
                                + " childCount=" + container.getChildCount()
                                + " containerView=" + safeToken(viewSummary(container))
                                + " fragmentView=" + safeToken(viewSummary(f.mView)));
                    }
                } catch (Throwable fallback) {
                    note("FragmentManager addFragmentInternal standalone child fallback err "
                            + fallback.getClass().getName());
                    if (mcdPdpTarget) {
                        proof("MCD_PDP_FRAGMENT_VIEW_NOT_ATTACHED fragment="
                                + safeToken(fragmentName(f))
                                + " container=0x" + Integer.toHexString(containerId)
                                + " reason=" + safeToken(t.getClass().getName())
                                + " fallback=" + safeToken(fallback.getClass().getName())
                                + " containerView=" + safeToken(viewSummary(container))
                                + " fragmentView=" + safeToken(viewSummary(f.mView)));
                    }
                    throw new RuntimeException("FragmentManager.addFragmentInternal:addView " + f, t);
                }
            }
        } else if (mcdPdpTarget) {
            proof("MCD_PDP_FRAGMENT_VIEW_NOT_ATTACHED fragment=" + safeToken(fragmentName(f))
                    + " container=0x" + Integer.toHexString(containerId)
                    + " reason=" + (container == null ? "missing_container" : "missing_view")
                    + " containerView=" + safeToken(viewSummary(container))
                    + " fragmentView=" + safeToken(viewSummary(f != null ? f.mView : null)));
        }
        try {
            note("FragmentManager addFragmentInternal before lifecycleResume");
            f.performActivityCreated(null);
            f.performStart();
            if (mcdPdpTarget && !prepareMcdPdpFragmentForResume(f)) {
                proof("MCD_PDP_FRAGMENT_RESUME_DEFERRED fragment="
                        + safeToken(fragmentName(f))
                        + " container=0x" + Integer.toHexString(containerId)
                        + " reason=missing_bound_views"
                        + " containerView=" + safeToken(viewSummary(container))
                        + " fragmentView=" + safeToken(viewSummary(f.mView)));
                return;
            }
            try {
                f.performResume();
            } catch (Throwable resumeError) {
                if (mcdPdpTarget) {
                    proof("MCD_PDP_FRAGMENT_RESUME_DEFERRED fragment="
                            + safeToken(fragmentName(f))
                            + " container=0x" + Integer.toHexString(containerId)
                            + " reason=" + safeToken(resumeError.getClass().getName())
                            + " message=" + safeToken(resumeError.getMessage())
                            + " containerView=" + safeToken(viewSummary(container))
                            + " fragmentView=" + safeToken(viewSummary(f.mView)));
                    return;
                }
                throw new RuntimeException(
                        "FragmentManager.addFragmentInternal:performResume " + f,
                        resumeError);
            }
            note("FragmentManager addFragmentInternal after lifecycleResume");
            if (mcdPdpTarget) {
                proof("MCD_PDP_FRAGMENT_RESUMED fragment=" + safeToken(fragmentName(f))
                        + " mode=performResume"
                        + " container=0x" + Integer.toHexString(containerId)
                        + " containerView=" + safeToken(viewSummary(container))
                        + " fragmentView=" + safeToken(viewSummary(f.mView)));
            }
            Log.i(TAG, "addFragmentInternal resumed fragment=" + f);
        } catch (Throwable t) {
            note("FragmentManager addFragmentInternal lifecycleResume err "
                    + t.getClass().getName());
            throw new RuntimeException("FragmentManager.addFragmentInternal:lifecycleResume " + f, t);
        }
    }

    private static boolean prepareMcdPdpFragmentForResume(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        View root = fragment.mView;
        View plus = root != null ? root.findViewById(MCD_PDP_PLUS_ID) : null;
        View minus = root != null ? root.findViewById(MCD_PDP_MINUS_ID) : null;
        View quantity = root != null ? root.findViewById(MCD_PDP_QUANTITY_ID) : null;
        boolean plusOk = setFragmentFieldIfNull(fragment, "M0", plus);
        boolean minusOk = setFragmentFieldIfNull(fragment, "N0", minus);
        boolean quantityOk = setFragmentFieldIfNull(fragment, "mDisplayQuantity", quantity);
        boolean attached = root != null && root.getParent() != null;
        String marker = "fragment=" + safeToken(fragmentName(fragment))
                + " attached=" + attached
                + " plus=" + safeToken(viewSummary(plus))
                + " minus=" + safeToken(viewSummary(minus))
                + " quantity=" + safeToken(viewSummary(quantity))
                + " plusOk=" + plusOk
                + " minusOk=" + minusOk
                + " quantityOk=" + quantityOk;
        proof("MCD_PDP_FRAGMENT_RESUME_PREP " + marker);
        return attached && plusOk && minusOk && quantityOk;
    }

    private static boolean setFragmentFieldIfNull(Fragment fragment, String name, Object value) {
        if (fragment == null || name == null || value == null) {
            return false;
        }
        try {
            java.lang.reflect.Field field = findFieldOnHierarchy(fragment.getClass(), name);
            if (field == null) {
                return false;
            }
            field.setAccessible(true);
            Object current = field.get(fragment);
            if (current == null) {
                field.set(fragment, value);
            }
            return field.get(fragment) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static java.lang.reflect.Field findFieldOnHierarchy(Class<?> type, String name) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    private static ViewGroup.LayoutParams fullSizeLayoutParamsFor(ViewGroup parent) {
        if (parent instanceof android.widget.FrameLayout) {
            return new android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (parent instanceof android.widget.LinearLayout) {
            return new android.widget.LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (parent instanceof android.widget.RelativeLayout) {
            return new android.widget.RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private static void prepareFragmentViewForContainer(View view, ViewGroup container) {
        if (view == null) {
            return;
        }
        try {
            view.setVisibility(View.VISIBLE);
            view.setMinimumHeight(720);
        } catch (Throwable ignored) {
        }
        try {
            view.setLayoutParams(fullSizeLayoutParamsFor(container));
        } catch (Throwable ignored) {
        }
    }

    private static void proof(String marker) {
        try {
            WestlakeLauncher.marker(marker);
            WestlakeLauncher.appendCutoffCanaryMarker(marker);
        } catch (Throwable ignored) {
        }
    }

    private static String fragmentName(Fragment fragment) {
        if (fragment == null) {
            return "null";
        }
        try {
            return fragment.getClass().getName();
        } catch (Throwable ignored) {
            return "unknown";
        }
    }

    private static boolean isMcdPdpTarget(Fragment fragment, int containerId) {
        return containerId == MCD_SIMPLE_PRODUCT_HOLDER_ID || isMcdPdpFragment(fragment);
    }

    private static boolean isMcdPdpFragment(Fragment fragment) {
        String name = fragmentName(fragment);
        return MCD_ORDER_PDP_FRAGMENT.equals(name) || name.endsWith(".OrderPDPFragment");
    }

    private static String viewSummary(View view) {
        if (view == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(96);
        try {
            sb.append(view.getClass().getName());
        } catch (Throwable ignored) {
            sb.append("unknown");
        }
        try {
            int id = view.getId();
            if (id != View.NO_ID) {
                sb.append("#0x").append(Integer.toHexString(id));
            }
        } catch (Throwable ignored) {
        }
        if (view instanceof ViewGroup) {
            try {
                sb.append("[c=").append(((ViewGroup) view).getChildCount()).append(']');
            } catch (Throwable ignored) {
            }
        }
        try {
            sb.append("[v=").append(view.getVisibility())
                    .append(",w=").append(view.getWidth())
                    .append(",h=").append(view.getHeight()).append(']');
        } catch (Throwable ignored) {
        }
        try {
            sb.append("[mw=").append(view.getMeasuredWidth())
                    .append(",mh=").append(view.getMeasuredHeight()).append(']');
        } catch (Throwable ignored) {
        }
        try {
            sb.append("[lp=").append(layoutParamsSummary(view.getLayoutParams())).append(']');
        } catch (Throwable ignored) {
        }
        try {
            sb.append("[parent=").append(view.getParent() != null).append(']');
        } catch (Throwable ignored) {
        }
        return sb.toString();
    }

    private static String layoutParamsSummary(ViewGroup.LayoutParams lp) {
        if (lp == null) {
            return "null";
        }
        return lp.getClass().getName() + ":" + lp.width + "x" + lp.height;
    }

    private static String safeToken(String value) {
        if (value == null || value.length() == 0) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9') || ch == '.' || ch == '_'
                    || ch == '-' || ch == '$' || ch == '#' || ch == '='
                    || ch == ',' || ch == '[' || ch == ']') {
                sb.append(ch);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    void removeFragmentInternal(Fragment f) {
        if (f != null && f.mView != null && f.mView.getParent() instanceof ViewGroup) {
            try {
                ((ViewGroup) f.mView.getParent()).removeView(f.mView);
            } catch (Throwable ignored) {
            }
        }
        f.performPause();
        f.performStop();
        f.performDestroyView();
        f.performDestroy();
        f.performDetach();
        f.mAdded = false;
        f.mFragmentManager = null;
        mAdded.remove(f);
    }

    int addBackStack(String name, List<FragmentTransaction.Op> ops) {
        int id = mBackStack.size();
        mBackStack.add(new BackStackRecord(id, name, ops));
        notifyBackStackChanged();
        return id;
    }

    List<Fragment> removeFragmentsAtContainer(int containerId) {
        List<Fragment> removed = new ArrayList<>();
        for (int i = mAdded.size() - 1; i >= 0; i--) {
            Fragment f = mAdded.get(i);
            if (f == null) {
                continue;
            }
            if (f.mContainerId == containerId) {
                removed.add(f);
                removeFragmentInternal(f);
                i = Math.min(i, mAdded.size());
            }
        }
        return removed;
    }

    // ── SavedState ──

    public Fragment.SavedState saveFragmentInstanceState(Fragment f) { return null; }

    public void putFragment(android.os.Bundle bundle, String key, Fragment fragment) {
        if (fragment.mTag != null) {
            bundle.putString(key, fragment.mTag);
        }
    }

    public Fragment getFragment(android.os.Bundle bundle, String key) {
        String tag = bundle.getString(key);
        if (tag != null) {
            return findFragmentByTag(tag);
        }
        return null;
    }

    // ── Factory ──

    private FragmentFactory mFragmentFactory;

    public FragmentFactory getFragmentFactory() {
        if (mFragmentFactory == null) {
            mFragmentFactory = new FragmentFactory();
        }
        return mFragmentFactory;
    }

    public void setFragmentFactory(FragmentFactory factory) {
        mFragmentFactory = factory;
    }

    // ── Dump ──

    public void dump(String prefix, java.io.FileDescriptor fd,
                     java.io.PrintWriter writer, String[] args) {
        writer.print(prefix);
        writer.println("FragmentManager{" + Integer.toHexString(System.identityHashCode(this)) + "}");
        writer.print(prefix);
        writer.println("  Added fragments:");
        for (int i = 0; i < mAdded.size(); i++) {
            writer.print(prefix);
            writer.println("    " + mAdded.get(i));
        }
    }
}
