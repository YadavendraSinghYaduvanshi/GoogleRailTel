package com.cpm.googlerailtel.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cpm.googlerailtel.constant.CommonString;
import com.cpm.googlerailtel.xmlGetterSetter.CaptureGetterSetter;
import com.cpm.googlerailtel.xmlGetterSetter.ImageDataGetterSetter;

import java.util.ArrayList;

public class GoogleRailTelDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "GOOGLE_RAIL_TEL_DATABASE_2";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;


    public GoogleRailTelDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CommonString.CRETAE_UPLOAD_IMAGE_DATA);
        db.execSQL(CommonString.CRETAE_UPLOAD_CAPTURE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertImageData(int mid, String username, double latitude, double longitude, String remark, String img_str, String img_str2, String intime, String visit_date, String xmlDataUploadStatus) {
        long l =0;
        ContentValues values = new ContentValues();
        try {
            values.put(CommonString.KEY_MID,String.valueOf(mid));
            values.put(CommonString.KEY_USER,username);
            values.put(CommonString.KEY_IMAGE_STR1,img_str );
            values.put(CommonString.KEY_IMAGE_STR2,img_str2);
            values.put(CommonString.KEY_STATUS,xmlDataUploadStatus);
            values.put(CommonString.KEY_LAT,String.valueOf(latitude));
            values.put(CommonString.KEY_LNG,String.valueOf(longitude));
            values.put(CommonString.KEY_REMARK,remark);
            values.put(CommonString.KEY_INTIME,intime);
            values.put(CommonString.KEY_VISIT_DATE,visit_date);

            l = db.insert(CommonString.TABLE_UPLOAD_IMAGE_DATA, null, values);

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Image Data ", ex.toString());
        }
    }

    public void insertCaptureUploadCount(int uploadImage, int capture, String visit_date) {
        db.delete(CommonString.TABLE_UPLOAD_CAPTURE_DATA, null, null);
        long l =0;
        ContentValues values = new ContentValues();
        try {
            values.put(CommonString.KEY_UPLOAD,uploadImage);
            values.put(CommonString.KEY_CAPTURE,capture);
            values.put(CommonString.KEY_VISIT_DATE,visit_date);

            l = db.insert(CommonString.TABLE_UPLOAD_CAPTURE_DATA, null, values);

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Upload Capture Count", ex.toString());
        }
    }


    // getCoverageData
    public ArrayList<ImageDataGetterSetter> getUplaodImageData(String visitdate) {
        ArrayList<ImageDataGetterSetter> list = new ArrayList<ImageDataGetterSetter>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_UPLOAD_IMAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    ImageDataGetterSetter sb = new ImageDataGetterSetter();
                    sb.setId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STATUS)));
                    sb.setImg1(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE_STR1)));
                    sb.setImg2(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE_STR2)));
                    sb.setIntime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_INTIME)));
                    sb.setLat(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LAT)));
                    sb.setLon(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LNG)));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REMARK)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());
        }
        return list;
    }


    public void deleteImageData() {
        db.delete(CommonString.TABLE_UPLOAD_IMAGE_DATA,null,null);
    }


    // Getting Upload Capture Data
    public ArrayList<CaptureGetterSetter> getCaptureUploadData(String visit_date) {
        ArrayList<CaptureGetterSetter> list = new ArrayList<CaptureGetterSetter>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from " + CommonString.TABLE_UPLOAD_CAPTURE_DATA + " where VISITED_DATE = '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CaptureGetterSetter sb = new CaptureGetterSetter();
                    sb.setUpload(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(CommonString.KEY_UPLOAD)));
                    sb.setCaptue(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CAPTURE)));
                    sb.setVisited_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        }catch (Exception e){
            Log.d("Exception when fetching CaptureUpload Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }
        return list;
    }



    //check if table is empty
    public boolean checkPreviousCallData(String visit_date) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_UPLOAD_CAPTURE_DATA + " where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                    db.delete(CommonString.TABLE_UPLOAD_CAPTURE_DATA, null, null);
                    return filled;
                }
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

    public long updateCaptureStatus() {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_STATUS, CommonString.KEY_D);
            l = db.update(CommonString.TABLE_UPLOAD_IMAGE_DATA, values, null, null);
        } catch (Exception e) {

        }
        return l;
    }
}
