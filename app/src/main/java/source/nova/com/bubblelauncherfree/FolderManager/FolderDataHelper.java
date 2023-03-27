package source.nova.com.bubblelauncherfree.FolderManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by joshua on 17.10.17.
 */

public class FolderDataHelper extends SQLiteOpenHelper {

    public static final int IN = -1;
    public static final int NOT_IN = 1;

    private String delete;
    private String create;
    private String name;

    public FolderDataHelper(Context context, String name, int version, String create, String delete) {
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
        if(newVersion > oldVersion){
            db.execSQL("ALTER TABLE "+name+" ADD COLUMN ");
        }
    }

    public void update(String name,
                       ContentValues contentValues,
                       String whereClause,
                       String[] whereArgs){
        Log.i("datahelper","update");
        this.getReadableDatabase().update(name, contentValues, whereClause, whereArgs);
    }

    public int write(String name,
                     ContentValues contentValues,
                     String spaceholder,
                     String[] columns,
                     String whereClause,
                     String[] whereArgs,
                     String orderby){


        SQLiteDatabase db = null;
        Cursor res = null;
        int ret = 0;

        try {
            db = this.getReadableDatabase();
            db.insert(name,spaceholder,contentValues);

            if(FolderManager.DEBUG_MODE){
                Log.i(FolderManager.DEBUG_FOLDER_MANAGER,"Folder inserted");
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

    public void deleteAll(){
        SQLiteDatabase db = null;

        Log.i("datahelper","update or write");

        try {
            db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+this.name+";");
        }catch (Exception e){
            if(db != null)
                db.close();
        }
    }
}
