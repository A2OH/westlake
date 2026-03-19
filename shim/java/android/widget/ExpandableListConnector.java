package android.widget;

import java.util.ArrayList;

/** Stub for AOSP ExpandableListConnector. */
public class ExpandableListConnector {
    public ExpandableListConnector() {}

    public static class GroupMetadata {
        public int gPos;
        public GroupMetadata() {}
    }

    public static class PositionMetadata {
        public ExpandableListPosition position;
        public int groupInsertIndex;
        public PositionMetadata() {}
        public boolean isExpanded() { return false; }
        public void recycle() {}
    }

    public PositionMetadata getUnflattenedPos(int flatPos) { return null; }
    public ExpandableListAdapter getAdapter() { return null; }
    public void setExpandedGroupMetadataList(ArrayList<GroupMetadata> list) {}
    public ArrayList<GroupMetadata> getExpandedGroupMetadataList() { return new ArrayList<>(); }
    public boolean isGroupExpanded(int groupPosition) { return false; }
    public boolean collapseGroup(int groupPosition) { return false; }
    public boolean expandGroup(int groupPosition) { return false; }
    public boolean expandGroup(ExpandableListPosition pos) { return false; }
    public int getFlattenedPos(ExpandableListPosition pos) { return 0; }
    public void setMaxExpGroupCount(int max) {}
}
