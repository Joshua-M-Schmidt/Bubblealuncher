package source.nova.com.bubblelauncherfree.Notification;

import android.provider.BaseColumns;

public class NotificationContract implements BaseColumns {
    public static final String TABLE_NAME = "notification_database";
    public static final String APP_PACKAGE = "app_package";
    public static final String NOTIFICATION_COUNT = "notification_count";

    public static final String CREATE = "CREATE TABLE "+
            TABLE_NAME+
            " (id INTEGER PRIMARY KEY, "+
            NOTIFICATION_COUNT+
            " INT, "+
            APP_PACKAGE+
            " STRING);";
    
    public static final int VERSION = 1;
    public static final String DELETE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
}
