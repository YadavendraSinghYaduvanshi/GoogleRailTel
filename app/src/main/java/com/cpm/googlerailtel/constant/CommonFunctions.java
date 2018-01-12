package com.cpm.googlerailtel.constant;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.cpm.googlerailtel.xmlGetterSetter.FailureGetterSetter;
import com.cpm.googlerailtel.xmlHandler.FailureXMLHandler;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by neerajg on 08-01-2018.
 */

public class CommonFunctions {

    private static FailureGetterSetter failureGetterSetter = null;

    public static String getCurrentTime() {
        String hour,min,sec;
        Calendar m_cal = Calendar.getInstance();

        if(m_cal.get(Calendar.HOUR_OF_DAY) < 10){
            hour = "0" + m_cal.get(Calendar.HOUR_OF_DAY);
        }else{
            hour = String.valueOf(m_cal.get(Calendar.HOUR_OF_DAY));
        }
        if(m_cal.get(Calendar.MINUTE) <10){
            min = "0" + m_cal.get(Calendar.MINUTE);
        }else{
            min = String.valueOf(m_cal.get(Calendar.MINUTE));
        }

        if(m_cal.get(Calendar.SECOND) <10){
            sec = "0" + m_cal.get(Calendar.SECOND);
        }else{
            sec = String.valueOf(m_cal.get(Calendar.SECOND));
        }

        String intime = hour + ":" + min + ":" + sec;
        return intime;
    }

    public static boolean CheckNetAvailability(Context context) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // we are connected to a network
            connected = true;
        }
        return connected;
    }



    public static String UploadPrevImage(String path, String folder_path) throws Exception {

        String errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(CommonString.FILE_PATH_OLD + path, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(CommonString.FILE_PATH_OLD + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = com.cpm.googlerailtel.upload.Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_IMAGE);
        String[] split = path.split("/");
        String path1 = split[split.length - 1];

        request.addProperty("img", ba1);
        request.addProperty("name", path1);
        request.addProperty("FolderName", folder_path);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL,20000);
        androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_IMAGE, envelope);

        Object result = envelope.getResponse();

        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                return CommonString.KEY_FALSE;
            }

            SAXParserFactory saxPF = SAXParserFactory.newInstance();
            SAXParser saxP = saxPF.newSAXParser();
            XMLReader xmlR = saxP.getXMLReader();
            // for failure
            FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
            xmlR.setContentHandler(failureXMLHandler);
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result.toString()));
            xmlR.parse(is);
            failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
            if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString.KEY_FAILURE;
            }
        } else {
            new File(CommonString.FILE_PATH_OLD + path).delete();
            //  preferences.setStringData();
        }

        return result.toString();
    }



    public static String UploadImage(String path, String folder_path) throws Exception {

        String errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(CommonString.FILE_PATH + path, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(CommonString.FILE_PATH + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = com.cpm.googlerailtel.upload.Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_IMAGE);
        String[] split = path.split("/");
        String path1 = split[split.length - 1];

        request.addProperty("img", ba1);
        request.addProperty("name", path1);
        request.addProperty("FolderName", folder_path);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL,20000);
        androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_IMAGE, envelope);

        Object result = envelope.getResponse();

        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                return CommonString.KEY_FALSE;
            }

            SAXParserFactory saxPF = SAXParserFactory.newInstance();
            SAXParser saxP = saxPF.newSAXParser();
            XMLReader xmlR = saxP.getXMLReader();
            // for failure
            FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
            xmlR.setContentHandler(failureXMLHandler);
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result.toString()));
            xmlR.parse(is);
            failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
            if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString.KEY_FAILURE;
            }
        } else {
            new File(CommonString.FILE_PATH + path).delete();
            //  preferences.setStringData();
        }

        return result.toString();
    }

}
