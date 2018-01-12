package com.cpm.googlerailtel.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cpm.googlerailtel.R;
import com.cpm.googlerailtel.constant.CommonFunctions;
import com.cpm.googlerailtel.constant.CommonString;
import com.cpm.googlerailtel.constant.SharedPreferenceUtility;
import com.cpm.googlerailtel.database.GoogleRailTelDB;
import com.cpm.googlerailtel.xmlGetterSetter.CaptureGetterSetter;
import com.cpm.googlerailtel.xmlGetterSetter.FailureGetterSetter;
import com.cpm.googlerailtel.xmlGetterSetter.ImageDataGetterSetter;
import com.cpm.googlerailtel.xmlHandler.FailureXMLHandler;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class UploadActivity extends AppCompatActivity {

    private ProgressDialog dialog = null;
    static int counter = 1;
    private SharedPreferenceUtility preferences;
    String  visit_date, username;
    private GoogleRailTelDB db;
    Object result;
    boolean dataFlag= false;
    private ArrayList<ImageDataGetterSetter>  imageDataGetterSetters;
    boolean isError = false,up_success_flag = true;
    private FailureGetterSetter failureGetterSetter = null;
    String exceptionMessage = "",errormsg="",resultFinal;
    int captureCount = 0,uploadCount =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        preferences = SharedPreferenceUtility.getInstance(this);
        visit_date = preferences.getStringData(CommonString.KEY_DATE);
        db = new GoogleRailTelDB(UploadActivity.this);
        db.open();
        new UploadAsyncTask().execute();
    }


    private class UploadAsyncTask extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(UploadActivity.this);
            dialog.setTitle("Uploading....");
            dialog.setMessage("Please wait a moment.");
            dialog.setCancelable(false);
            if(counter == 1) {
                dialog.show();
            }

        }

        @Override
        protected String doInBackground(Void... voids) {

            try{

                imageDataGetterSetters =  db.getUplaodImageData(visit_date);
                String final_xml = "";

                // calls cycle data
                final_xml = "";
                String onXML = "";
                for(int i=0;i<imageDataGetterSetters.size();i++) {
                    if(!imageDataGetterSetters.get(i).getStatus().equals(CommonString.KEY_D)){
                        onXML = "[IMAGE_CAPTURE_DATA][MID]"
                                + "0"
                                + "[/MID]"

                                + "[CREATED_BY]"
                                + username
                                + "[/CREATED_BY]"

                                + "[LATITUDE]"
                                + imageDataGetterSetters.get(i).getLat()
                                + "[/LATITUDE]"

                                + "[LONGITUDE]"
                                + imageDataGetterSetters.get(i).getLon()
                                + "[/LONGITUDE]"

                                + "[INTIME]"
                                + imageDataGetterSetters.get(i).getIntime()
                                + "[/INTIME]"

                                + "[REMARK]"
                                + imageDataGetterSetters.get(i).getRemark().replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "")
                                + "[/REMARK]"

                                + "[IMAGE_NAME]"
                                + imageDataGetterSetters.get(i).getImg1()
                                + "[/IMAGE_NAME]"

                                + "[IMAGE_NAME2]"
                                + imageDataGetterSetters.get(i).getImg2()
                                + "[/IMAGE_NAME2]"

                                + "[VISITED_DATE]"
                                + visit_date
                                + "[/VISITED_DATE]"

                                + "[/IMAGE_CAPTURE_DATA]";
                        if(onXML != "") {
                            final_xml = final_xml + onXML;
                        }
                    }

                }
                if(onXML != "") {
                    final String sos_xml1 = "[DATA]" + final_xml + "[/DATA]";

                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                    request.addProperty("XMLDATA", sos_xml1);
                    request.addProperty("KEYS", "RAIL_TEL_IMAGE_DATA");
                    request.addProperty("USERNAME", username);
                    request.addProperty("MID", 0);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL,20000);
                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);

                    result = (Object) envelope.getResponse();

                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                            dataFlag = false;
                            return CommonString.KEY_FAILURE;
                        }
                        return "IMAGE_CAPTURE_DATA";
                    } else {
                        dataFlag = true;
                        //Toast.makeText(CaptureImageActivity.this, "Here", Toast.LENGTH_SHORT).show();
                    }
                }

                for(int i=0;i<imageDataGetterSetters.size();i++) {

                    if(!imageDataGetterSetters.get(i).getImg1().equalsIgnoreCase("")){
                        if (new File(CommonString.FILE_PATH + imageDataGetterSetters.get(i).getImg1()).exists()) {
                            File originalFile = new File(CommonString.FILE_PATH +  imageDataGetterSetters.get(i).getImg1());
                            result = CommonFunctions.UploadImage(originalFile.getName(), "installProofImages");
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                isError = true;
                            }
                            resultFinal = result.toString();
                        }
                    }

                    if(!imageDataGetterSetters.get(i).getImg2().equalsIgnoreCase("")){
                        if (new File(CommonString.FILE_PATH +  imageDataGetterSetters.get(i).getImg2()).exists()) {
                            File originalFile = new File(CommonString.FILE_PATH +  imageDataGetterSetters.get(i).getImg2());
                            result = CommonFunctions.UploadImage(originalFile.getName(), "installProofImages");
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                isError = true;
                            }
                            resultFinal = result.toString();
                        }
                    }
                }
            }catch (MalformedURLException e) {
                up_success_flag = false;
                exceptionMessage = e.toString();

            } catch (IOException e) {
                up_success_flag = false;
                exceptionMessage = e.toString();
            } catch (Exception e) {
                up_success_flag = false;
                exceptionMessage = e.toString();
            }

            if (up_success_flag) {
                return resultFinal;
            } else {
                return exceptionMessage;
            }
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS))
            {
                counter=1;
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                builder.setTitle("Parinaam");
                builder.setMessage(CommonString.MESSAGE_UPLOAD_DATA).setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.open();
                                ArrayList<CaptureGetterSetter> data;
                                data = db.getCaptureUploadData(visit_date);

                                if (data.size() > 0) {
                                    captureCount = data.get(0).getCaptue();
                                    uploadCount = data.get(0).getUpload();
                                    uploadCount = uploadCount + imageDataGetterSetters.size();
                                    db.insertCaptureUploadCount(uploadCount, captureCount, visit_date);
                                }
                                    db.deleteImageData();
                                 finish();
                            }

                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else {

                if (dataFlag) {
                    db.open();
                    db.updateCaptureStatus();
                }

                counter++;

                if (counter < 4) {
                    new UploadAsyncTask().execute();
                } else {
                    counter = 1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                    builder.setTitle("Parinaam");
                    builder.setMessage(" Network issue data not uploaded on server").setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    }


}
