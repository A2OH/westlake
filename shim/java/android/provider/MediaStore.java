package android.provider;

import android.net.Uri;

/**
 * Android-compatible MediaStore shim. Constants and stub query classes.
 */
public class MediaStore {
    public static final String AUTHORITY = "media";
    public static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
    public static final String ACTION_VIDEO_CAPTURE = "android.media.action.VIDEO_CAPTURE";

    public static final String EXTRA_OUTPUT = "output";

    public static class MediaColumns {
        public static final String _ID = "_id";
        public static final String DISPLAY_NAME = "_display_name";
        public static final String MIME_TYPE = "mime_type";
        public static final String SIZE = "_size";
        public static final String DATE_ADDED = "date_added";
        public static final String DATE_MODIFIED = "date_modified";
        public static final String DATA = "_data";
    }

    public static class Images {
        public static class Media {
            public static final Uri EXTERNAL_CONTENT_URI =
                Uri.parse("content://media/external/images/media");
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/image";
        }
    }

    public static class Video {
        public static class Media {
            public static final Uri EXTERNAL_CONTENT_URI =
                Uri.parse("content://media/external/video/media");
        }
    }

    public static class Audio {
        public static class Media {
            public static final Uri EXTERNAL_CONTENT_URI =
                Uri.parse("content://media/external/audio/media");
        }
    }

    public static class Files {
        public static Uri getContentUri(String volumeName) {
            return Uri.parse("content://media/" + volumeName + "/file");
        }

        public static Uri getContentUri(String volumeName, long rowId) {
            return Uri.parse("content://media/" + volumeName + "/file/" + rowId);
        }
    }

    public static class Downloads {
        public static final Uri EXTERNAL_CONTENT_URI =
            Uri.parse("content://media/external/downloads");
    }
}
