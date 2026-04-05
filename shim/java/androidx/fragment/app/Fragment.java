package androidx.fragment.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * AndroidX Fragment stub. Standalone class (does not extend android.app.Fragment)
 * to match the real Jetpack Fragment which is also standalone.
 */
public class Fragment {

    String mTag;
    Bundle mArguments;
    public FragmentActivity mActivity;
    Object mHost;  // FragmentHostCallback in real impl, just Object for stub
    public View mView;
    int mContainerId;
    boolean mAdded;
    boolean mDetached;
    boolean mHidden;
    boolean mResumed;
    boolean mCreated;
    boolean mStarted;
    FragmentManager mFragmentManager;       // parent
    FragmentManager mChildFragmentManager;
    private boolean mRetainInstance;
    private boolean mHasMenu;
    private boolean mMenuVisible = true;
    private boolean mUserVisibleHint = true;

    public Fragment() {}

    // ── Lifecycle callbacks ──

    public void onAttach(Context context) {}

    public void onCreate(Bundle savedInstanceState) {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {}

    public void onActivityCreated(Bundle savedInstanceState) {}

    public void onStart() {}

    public void onResume() {}

    public void onPause() {}

    public void onStop() {}

    public void onDestroyView() {}

    public void onDestroy() {}

    public void onDetach() {}

    // State
    public void onSaveInstanceState(Bundle outState) {}
    public void onViewStateRestored(Bundle savedInstanceState) {}
    public void onHiddenChanged(boolean hidden) {}

    // ── Accessors ──

    public FragmentActivity getActivity() { return mActivity; }

    public Context getContext() { return mActivity; }

    public Bundle getArguments() { return mArguments; }

    public void setArguments(Bundle args) { mArguments = args; }

    public String getTag() { return mTag; }

    public int getId() { return mContainerId; }

    public View getView() { return mView; }

    public boolean isAdded() { return mAdded; }

    public boolean isDetached() { return mDetached; }

    public boolean isHidden() { return mHidden; }

    public boolean isResumed() { return mResumed; }

    public boolean isVisible() { return mAdded && !mHidden && mView != null; }

    public boolean isRemoving() { return false; }

    public boolean isStateSaved() { return false; }

    public void setRetainInstance(boolean retain) { mRetainInstance = retain; }

    public boolean getRetainInstance() { return mRetainInstance; }

    public void setHasOptionsMenu(boolean hasMenu) { mHasMenu = hasMenu; }

    public void setMenuVisibility(boolean menuVisible) { mMenuVisible = menuVisible; }

    public void setUserVisibleHint(boolean isVisibleToUser) { mUserVisibleHint = isVisibleToUser; }

    public boolean getUserVisibleHint() { return mUserVisibleHint; }

    // ── require* methods ──

    public FragmentActivity requireActivity() {
        if (mActivity == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to an activity.");
        }
        return mActivity;
    }

    public Context requireContext() {
        Context ctx = getContext();
        if (ctx == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to a context.");
        }
        return ctx;
    }

    public View requireView() {
        if (mView == null) {
            throw new IllegalStateException(
                "Fragment " + this + " did not return a View from onCreateView() or this was called before onViewCreated().");
        }
        return mView;
    }

    // ── Lifecycle owner stubs ──

    /**
     * Returns a LifecycleOwner scoped to the Fragment's View.
     * Stub returns {@code this} (Fragment is not actually a LifecycleOwner in the stub,
     * but callers just need a non-null reference).
     */
    public Object getViewLifecycleOwner() { return this; }

    /**
     * Returns the Lifecycle of this fragment. Stub returns null.
     */
    public Object getLifecycle() { return null; }

    // ── Fragment managers ──

    public FragmentManager getParentFragmentManager() {
        if (mFragmentManager == null) {
            throw new IllegalStateException("Fragment " + this + " not associated with a fragment manager.");
        }
        return mFragmentManager;
    }

    public FragmentManager getChildFragmentManager() {
        if (mChildFragmentManager == null) {
            mChildFragmentManager = new FragmentManager();
            if (mActivity != null) {
                mChildFragmentManager.mHost = mActivity;
            }
        }
        return mChildFragmentManager;
    }

    // ── Resources ──

    public android.content.res.Resources getResources() {
        return mActivity != null ? mActivity.getResources() : null;
    }

    public String getString(int resId) {
        android.content.res.Resources r = getResources();
        return r != null ? r.getString(resId) : null;
    }

    public String getString(int resId, Object... formatArgs) {
        android.content.res.Resources r = getResources();
        return r != null ? r.getString(resId) : null;
    }

    // ── View finding ──

    public View requireViewById(int id) {
        View v = mView != null ? mView.findViewById(id) : null;
        if (v == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this Fragment");
        }
        return v;
    }

    // ── Lifecycle dispatch (called by FragmentManager) ──

    void performAttach(FragmentActivity host) {
        mActivity = host;
        onAttach((Context) host);
    }

    void performCreate(Bundle savedInstanceState) {
        mCreated = true;
        onCreate(savedInstanceState);
    }

    void performCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        mView = onCreateView(inflater, container, savedInstanceState);
        if (mView != null) {
            onViewCreated(mView, savedInstanceState);
        }
    }

    void performActivityCreated(Bundle savedInstanceState) {
        onActivityCreated(savedInstanceState);
    }

    void performStart() {
        mStarted = true;
        onStart();
    }

    void performResume() {
        mResumed = true;
        onResume();
    }

    void performPause() {
        mResumed = false;
        onPause();
    }

    void performStop() {
        mStarted = false;
        onStop();
    }

    void performDestroyView() {
        onDestroyView();
        mView = null;
    }

    void performDestroy() {
        mCreated = false;
        onDestroy();
    }

    void performDetach() {
        mActivity = null;
        onDetach();
    }

    // ── Static factory helper ──

    public static Fragment instantiate(Context context, String fname) {
        return instantiate(context, fname, null);
    }

    public static Fragment instantiate(Context context, String fname, Bundle args) {
        try {
            Class<?> clazz = context.getClassLoader().loadClass(fname);
            Fragment f = (Fragment) clazz.newInstance();
            if (args != null) {
                f.setArguments(args);
            }
            return f;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate fragment " + fname, e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fragment{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (mTag != null) {
            sb.append(" tag=").append(mTag);
        }
        if (mContainerId != 0) {
            sb.append(" id=0x").append(Integer.toHexString(mContainerId));
        }
        sb.append('}');
        return sb.toString();
    }

    // SavedState for compatibility
    public static class SavedState {
        final Bundle mState;
        public SavedState(Bundle state) { mState = state; }
        public SavedState() { mState = null; }
    }
}
