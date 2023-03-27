package source.nova.com.bubblelauncherfree.Notification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotificationDataHelper extends SQLiteOpenHelper {

    private String delete;
    private String create;
    private String name;

    public static final int IN = -1;
    public static final int NOT_IN = 1;

    public NotificationDataHelper(Context context, String name, int version, String create, String delete) {
        super(context, name+".db", null, version);
        this.delete = delete;
        this.create = create;
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("current sql v",oldVersion+" new"+newVersion);

    }

    public void update(String name,
                       ContentValues contentValues,
                       String whereClause,
                       String[] whereArgs){
        Log.i("badge datahelper","update");
        this.getReadableDatabase().update(name, contentValues, whereClause, whereArgs);
    }

    public int write(String name,
                     ContentValues contentValues,
                     String spaceholder,
                     String[] columns,
                     String whereClause,
                     String[] whereArgs,
                     String orderby){

        Log.i("badge datahelper","write");

        SQLiteDatabase db = null;
        Cursor res = null;
        int ret = 0;

        try {
            db = this.getWritableDatabase();
            res = read(name, columns, whereClause, whereArgs, orderby);
            db.insert(name,spaceholder,contentValues);

            if(res.moveToFirst()){
                ret = IN;
            }else{
                // {{Replaced}}DB debugg", "write");
                db.insert(name,spaceholder,contentValues);
                ret =  NOT_IN;
            }
        }finally {

            if(db != null)
                db.close();
            if(res != null)
                res.close();
            return ret;
        }

    }

    public Cursor read(String tablename,
                       String[] columns,
                       String whereClause,
                       String[] whereArgs,
                       String orderby){

        Log.i("datahelper","read");

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                tablename,
                columns,
                whereClause,
                whereArgs,
                null,
                null,
                orderby
        );
    }

    public int delete(String table, String whereClause, String[] whereArgs){
        SQLiteDatabase db = null;

        Log.i("datahelper","update or write");

        try {
            db = this.getWritableDatabase();
            return db.delete(table,whereClause,whereArgs);
        }finally {
            if(db != null)
                db.close();
        }
    }
}
