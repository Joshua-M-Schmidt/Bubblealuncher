package source.nova.com.bubblelauncherfree.AppManager;


import android.provider.BaseColumns;

/**
 * Created by joshua on 16.10.16.
 */

public class AppContract implements BaseColumns{
    public static final String TABLE_NAME = "app_database";
    public static final String APP_NAME = "app_name";
    public static final String APP_ICON_RES = "app_icon_res";
    public static final String APP_PACKAGE = "app_package";
    public static final String X_POSITION = "app_x_position";
    public static final String Y_POSITION = "app_y_position";

    //new columns

    public static final String BELONGS_TO_FOLDER = "belongs_to_folder";
    public static final String SHOW_APP = "show_app";
    public static final String APP_CATEGORY = "app_category";
    public static final String APP_ON_HOMESCREEN = "app_on_homescreen";
    public static final String DEINSTALLED = "app_deinstalled";

    //updates

    public static final String ADD_BELONGS_TO_FOLDER = "ALTER TABLE "+
            TABLE_NAME +
            " ADD COLUMN " +
            BELONGS_TO_FOLDER +
            " STRING;";

    public static final String ADD_SHOW_APP = "ALTER TABLE "+
            TABLE_NAME +
            " ADD COLUMN " +
            SHOW_APP +
            " INT DEFAULT 1;";

    public static final String ADD_APP_CATEGORY = "ALTER TABLE "+
            TABLE_NAME +
            " ADD COLUMN " +
            APP_CATEGORY +
            " STRING;";

    public static final String ADD_APP_ON_HOMESCREEN = "ALTER TABLE "+
            TABLE_NAME +
            " ADD COLUMN " +
            APP_ON_HOMESCREEN +
            " INT DEFAULT 1;";

    public static final String ADD_DEINSTALL = "ALTER TABLE "+
            TABLE_NAME +
            " ADD COLUMN " +
            DEINSTALLED +
            " INT DEFAULT 0;";

    public static final String CREATE = "CREATE TABLE "+
            TABLE_NAME+
            " (id INTEGER PRIMARY KEY, "+
            APP_NAME+
            " TEXT, "+
            APP_PACKAGE+
            " TEXT, "+
            APP_ICON_RES+
            " INT, "+
            X_POSITION+
            " INT, "+
            Y_POSITION+
            " INT, "+
            BELONGS_TO_FOLDER+
            " STRING, "+
            APP_ON_HOMESCREEN+
            " INT DEFAULT 1,"+
            SHOW_APP+
            " INT DEFAULT 1,"+
            DEINSTALLED+
            " INT DEFAULT 0,"+
            APP_CATEGORY
            +" STRING);";


    public static final int VERSION = 6;
    public static final String DELETE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
}

