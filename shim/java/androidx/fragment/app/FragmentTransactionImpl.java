package androidx.fragment.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete FragmentTransaction implementation that modifies the FragmentManager's
 * fragment list on commit.
 */
class FragmentTransactionImpl extends FragmentTransaction {

    final FragmentManager mManager;
    final ArrayList<Op> mOps = new ArrayList<>();
    String mName;
    boolean mAddToBackStack;

    FragmentTransactionImpl(FragmentManager manager) {
        mManager = manager;
    }

    @Override
    public FragmentTransaction add(int containerViewId, Fragment fragment) {
        return add(containerViewId, fragment, null);
    }

    @Override
    public FragmentTransaction add(int containerViewId, Fragment fragment, String tag) {
        Op op = new Op();
        op.cmd = OP_ADD;
        op.fragment = fragment;
        op.tag = tag;
        op.containerId = containerViewId;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction replace(int containerViewId, Fragment fragment) {
        return replace(containerViewId, fragment, null);
    }

    @Override
    public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) {
        Op op = new Op();
        op.cmd = OP_REPLACE;
        op.fragment = fragment;
        op.tag = tag;
        op.containerId = containerViewId;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction remove(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_REMOVE;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction hide(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_HIDE;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction show(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_SHOW;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction detach(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_DETACH;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction attach(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_ATTACH;
        op.fragment = fragment;
        mOps.add(op);
        return this;
    }

    @Override
    public FragmentTransaction addToBackStack(String name) {
        mAddToBackStack = true;
        mName = name;
        return this;
    }

    @Override
    public int commit() { return commitInternal(false); }

    @Override
    public int commitAllowingStateLoss() { return commitInternal(true); }

    @Override
    public void commitNow() { commitInternal(false); }

    @Override
    public void commitNowAllowingStateLoss() { commitInternal(true); }

    @Override
    public boolean isEmpty() { return mOps.isEmpty(); }

    private int commitInternal(boolean allowStateLoss) {
        if (mManager == null) return -1;

        for (int i = 0; i < mOps.size(); i++) {
            Op op = mOps.get(i);
            switch (op.cmd) {
                case OP_ADD:
                    mManager.addFragmentInternal(op.fragment, op.tag, op.containerId);
                    break;
                case OP_REPLACE:
                    List<Fragment> removed = mManager.removeFragmentsAtContainer(op.containerId);
                    op.removed = removed;
                    mManager.addFragmentInternal(op.fragment, op.tag, op.containerId);
                    break;
                case OP_REMOVE:
                    mManager.removeFragmentInternal(op.fragment);
                    break;
                case OP_HIDE:
                    op.fragment.mHidden = true;
                    op.fragment.onHiddenChanged(true);
                    break;
                case OP_SHOW:
                    op.fragment.mHidden = false;
                    op.fragment.onHiddenChanged(false);
                    break;
                case OP_DETACH:
                    op.fragment.mDetached = true;
                    break;
                case OP_ATTACH:
                    op.fragment.mDetached = false;
                    break;
            }
        }

        int backStackId = -1;
        if (mAddToBackStack) {
            backStackId = mManager.addBackStack(mName, new ArrayList<>(mOps));
        }
        return backStackId;
    }
}
