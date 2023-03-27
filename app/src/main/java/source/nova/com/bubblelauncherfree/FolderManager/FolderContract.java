package source.nova.com.bubblelauncherfree.FolderManager;

import android.provider.BaseColumns;

/**
 * Created by joshua on 17.10.17.
 */

public class FolderContract implements BaseColumns {
    public static final String TABLE_NAME = "folder_database";
    public static final String FOLDER_NAME = "folder_name";
    public static final String X_POSITION = "app_x_position";
    public static final String Y_POSITION = "app_y_position";
    public static final String APPS_INSIDE = "apps_inside";

    public static final String CREATE = "CREATE TABLE "+
            TABLE_NAME+
            " (id INTEGER PRIMARY KEY, "+
            FOLDER_NAME+
            " TEXT, "+
            X_POSITION+
            " INT, "+
            Y_POSITION+
            " INT, "+
            APPS_INSIDE+
            " TEXT);";

    public static final int VERSION = 1;
    public static final String DELETE = "DROP TABLE IF EXISTS "+TABLE_NAME;
}
