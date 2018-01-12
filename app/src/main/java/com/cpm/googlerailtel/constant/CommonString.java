package com.cpm.googlerailtel.constant;

import android.os.Environment;

/**
 * Created by neerajg on 08-01-2018.
 */

public class CommonString {

    public static final String SOAP_ACTION = "http://tempuri.org/";
    public static final String URL = "http://gr.parinaam.in/gswebservice.asmx";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String METHOD_LOGIN = "UserLoginDetail";
    public static final String KEY_FAILURE = "Failure";
    public static final String KEY_DATE = "date";

    public static final String KEY_D = "D";
    public static final String KEY_N = "N";

    public static final String TABLE_UPLOAD_IMAGE_DATA = "UPLOAD_IMAGE_DATA";
    public static final String TABLE_UPLOAD_CAPTURE_DATA = "UPLOAD_CAPTURE_DATA";
    public static final String MESSAGE_UPLOAD_DATA = "Data Uploaded Successfully";


    public static final String METHOD_UPLOAD_XML = "DrUploadXml";
    public static final String MESSAGE_FAILURE = "Server Error.Please Access After Some Time";
    public static final String MESSAGE_FALSE = "Invalid User";
    public static final String KEY_CHANGED = "Changed";
    public static final String KEY_FALSE = "False";
    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_PATH = "path";
    public static final String KEY_VERSION = "version";
    public static final String KEY_STATUS = "STATUS";
    public static final String KEY_USER_TYPE = "RIGHTNAME";
    public static final String MESSAGE_CHANGED = "Invalid UserId Or Password / Password Has Been Changed.";
    public static final String MESSAGE_EXCEPTION = "Problem Occured : Report The Problem To Parinaam";
    public static final String MESSAGE_SOCKETEXCEPTION = "Network Communication Failure. Check Your Network Connection";
    public static final String FILE_PATH_OLD = Environment.getExternalStorageDirectory() +"/.Google_Rail_Tel_Images/";
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() +"/.Google_Rail_Tel_Images_New/";

    public static final String SOAP_ACTION_LOGIN = "http://tempuri.org/" + METHOD_LOGIN;
    public static final String KEY_VISIT_DATE = "VISITED_DATE";

    public static final String METHOD_UPLOAD_IMAGE = "GetImageWithFolderName";
    public static final String SOAP_ACTION_UPLOAD_IMAGE = "http://tempuri.org/" + METHOD_UPLOAD_IMAGE;
    public static final String KEY_ID = "_id";
    public static final String KEY_CAPTURE = "CAPTURE_COUNT";
    public static final String KEY_UPLOAD = "UPLOAD_COUNT";
    public static final String KEY_USER = "USER";
    public static final String KEY_MID = "MID";
    public static final String KEY_IMAGE_STR1 = "IMAGE_STR1";
    public static final String KEY_IMAGE_STR2 = "IMAGE_STR2";
    public static final String KEY_LAT = "LAT";
    public static final String KEY_LNG = "LNG";
    public static final String KEY_INTIME = "INTIME";
    public static final String KEY_REMARK = "REMARK";

    public static final String CRETAE_UPLOAD_IMAGE_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_UPLOAD_IMAGE_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_USER + " VARCHAR,"
            + KEY_MID + " VARCHAR,"
            + KEY_IMAGE_STR1 + " VARCHAR,"
            + KEY_IMAGE_STR2 + " VARCHAR,"
            + KEY_LAT + " VARCHAR,"
            + KEY_LNG + " VARCHAR,"
            + KEY_INTIME + " VARCHAR,"
            + KEY_REMARK + " VARCHAR,"
            + KEY_STATUS + " VARCHAR,"
            + KEY_VISIT_DATE + " VARCHAR)";


    public static final String CRETAE_UPLOAD_CAPTURE_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_UPLOAD_CAPTURE_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_UPLOAD + " INTEGER,"
            + KEY_CAPTURE + " INTEGER,"
            + KEY_VISIT_DATE + " VARCHAR)";
}
