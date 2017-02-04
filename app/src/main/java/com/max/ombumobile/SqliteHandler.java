package com.max.ombumobile;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "OmbuMobile.db";
    private static final int DATABASE_VERSION = 5;
    public static final String SESSION_TABLE_NAME = "sessions";
    public static final String SESSION_COLUMN_USER = "user";
    public static final String SESSION_COLUMN_USER_ID = "user_id";
    public static final String SESSION_COLUMN_NOMBRE = "nombre";
    public static final String SESSION_COLUMN_SESSION = "session";
    public static final String SESSION_COLUMN_PERFIL = "perfil";


    public SqliteHandler(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + SESSION_TABLE_NAME
                + " ("+SESSION_COLUMN_USER+" VARCHAR, "
                +SESSION_COLUMN_SESSION+" VARCHAR, "
                +SESSION_COLUMN_USER_ID+" VARCHAR, "
                +SESSION_COLUMN_NOMBRE+" VARCHAR, "
                +SESSION_COLUMN_PERFIL+" VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE_NAME);
        onCreate(db);
    }

    public Cursor traerSession() {
        SQLiteDatabase db = this.getReadableDatabase();
        // una sola session
        Cursor res =  db.rawQuery( "select * from "+SESSION_TABLE_NAME+" limit 1", null );
        return res;
    }

    public boolean persistirSession(Usuario usr, String perfil) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SESSION_COLUMN_USER_ID, usr.getUser_id());
        contentValues.put(SESSION_COLUMN_USER, usr.getUser());
        contentValues.put(SESSION_COLUMN_NOMBRE, usr.getNombre());
        contentValues.put(SESSION_COLUMN_SESSION, usr.getSession_id());
        contentValues.put(SESSION_COLUMN_PERFIL, perfil);
        db.insert(SESSION_TABLE_NAME, null, contentValues);
        return true;
    }

    public Integer deleteSession (String session) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SESSION_TABLE_NAME,
                SESSION_COLUMN_SESSION+" = ? ",
                new String[] { session });
    }

}

