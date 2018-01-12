package com.cpm.googlerailtel.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.googlerailtel.R;
import com.cpm.googlerailtel.constant.AlertMessages;
import com.cpm.googlerailtel.constant.CommonFunctions;
import com.cpm.googlerailtel.constant.CommonString;
import com.cpm.googlerailtel.constant.SharedPreferenceUtility;
import com.cpm.googlerailtel.database.GoogleRailTelDB;
import com.cpm.googlerailtel.gps.GPSTracker;
import com.cpm.googlerailtel.xmlGetterSetter.CaptureGetterSetter;
import com.cpm.googlerailtel.xmlGetterSetter.ImageDataGetterSetter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CaptureImageActivity extends AppCompatActivity implements
        View.OnClickListener {

    ImageView img_cam, img_clicked, img_cam2, img_clicked2;
    TextView txtUploaded, txtTakenPicture;
    EditText etRemark;
    Button btn_save;
    String _pathforcheck, str;
    String visit_date, username, _path, intime, img_str2 = "";
    AlertDialog alert;
    String img_str = "", app_ver, remark, exceptionMessage = "", resultFinal;
    double latitude, longitude;
    int captureCount = 0, uploadCount = 0;
    private SharedPreferenceUtility preferences;
    private ProgressDialog dialog = null;
    boolean isError = false, up_success_flag = true;
    private ArrayList<CaptureGetterSetter> data;
    private ArrayList<ImageDataGetterSetter> imageDataGetterSetters;
    boolean dataFlag = false;
    static int counter = 1;
    Object result;

    //jeevan
    File saveDir = null;
    GPSTracker gps;
    private GoogleRailTelDB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UIdetails();
        str = CommonString.FILE_PATH;

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            saveDir = new File(CommonString.FILE_PATH);
            saveDir.mkdirs();
        }
    }


    private void UIdetails() {

        img_cam = (ImageView) findViewById(R.id.img_selfie);
        img_cam2 = (ImageView) findViewById(R.id.img_selfie2);
        img_clicked = (ImageView) findViewById(R.id.img_cam_selfie);
        img_clicked2 = (ImageView) findViewById(R.id.img_cam_selfie2);
        btn_save = (Button) findViewById(R.id.btn_save_selfie);
        txtUploaded = (TextView) findViewById(R.id.txt_upload);
        txtTakenPicture = (TextView) findViewById(R.id.txt_taken_picture);
        etRemark = (EditText) findViewById(R.id.txt_remark);

        preferences = SharedPreferenceUtility.getInstance(this);
        visit_date = preferences.getStringData(CommonString.KEY_DATE);
        username = preferences.getStringData(CommonString.KEY_USERNAME);
        app_ver = preferences.getStringData(CommonString.KEY_VERSION);
        getSupportActionBar().setTitle("Image -" + visit_date);
        db = new GoogleRailTelDB(CaptureImageActivity.this);
        db.open();

        img_cam.setOnClickListener(this);
        img_clicked.setOnClickListener(this);
        img_cam2.setOnClickListener(this);
        img_clicked2.setOnClickListener(this);
        btn_save.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            finish();
            //overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        // overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.img_cam_selfie:
            case R.id.img_selfie:

                _pathforcheck = "Img_" + username.replace(".", "") + visit_date.replace("/", "") + "_" + CommonFunctions.getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                intime = CommonFunctions.getCurrentTime();
                startCameraActivity(0);

                break;

            case R.id.img_cam_selfie2:
            case R.id.img_selfie2:
                _pathforcheck = "Img2_" + username.replace(".", "") + visit_date.replace("/", "") + "_" + CommonFunctions.getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                intime = CommonFunctions.getCurrentTime();
                startCameraActivity(1);

                break;

            case R.id.btn_save_selfie:

                if (CommonFunctions.CheckNetAvailability(this)) {
                    if (img_str.equals("") || img_str2.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please capture both images", Toast.LENGTH_SHORT).show();
                    } else {
                        remark = etRemark.getText().toString().replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "");
                        AlertDialog.Builder builder = new AlertDialog.Builder(CaptureImageActivity.this);
                        builder.setMessage("Do you want to save the data ")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new UploadAsyncTask().execute();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        alert = builder.create();
                        alert.show();
                    }
                } else {
                    AlertMessages.showToastMsg(this, "No Internet Connection");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gps = new GPSTracker(CaptureImageActivity.this);

        db.checkPreviousCallData(visit_date);
        data = db.getCaptureUploadData(visit_date);

        if (data.size() > 0) {
            txtUploaded.setText(data.get(0).getUpload() + "");
            txtTakenPicture.setText(data.get(0).getCaptue() + "");
        } else {
            txtUploaded.setText("0");
            txtTakenPicture.setText("0");
        }

        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();// \n is for new line
            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);

        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:

                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(str + _pathforcheck).exists()) {
                        Bitmap bmp = BitmapFactory.decodeFile(str + _pathforcheck);
                        Bitmap dest = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

                        Canvas cs = new Canvas(dest);
                        Paint tPaint = new Paint();
                        tPaint.setTextSize(100);
                        tPaint.setColor(Color.RED);
                        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);

                        cs.drawBitmap(bmp, 0f, 0f, null);
                        float height = tPaint.measureText("yY");
                        cs.drawText(dateTime, 20f, height + 15f, tPaint);
                        try {
                            dest.compress(Bitmap.CompressFormat.JPEG, 90,
                                    new FileOutputStream(new File(str + _pathforcheck)));
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        bmp = BitmapFactory.decodeFile(str + _pathforcheck);
                        if (requestCode == 0) {
                            img_cam.setImageBitmap(bmp);
                            img_clicked.setVisibility(View.GONE);
                            img_cam.setVisibility(View.VISIBLE);
                            //Set Clicked image to Imageview
                            img_str = _pathforcheck;

                        } else {
                            img_cam2.setImageBitmap(bmp);
                            img_clicked2.setVisibility(View.GONE);
                            img_cam2.setVisibility(View.VISIBLE);
                            img_str2 = _pathforcheck;

                        }
                        _pathforcheck = "";
                    }
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void startCameraActivity(int code) {
        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, code);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class UploadAsyncTask extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CaptureImageActivity.this);
            dialog.setTitle("Uploading....");
            dialog.setMessage("Please wait a moment.");
            dialog.setCancelable(false);
            if (counter == 1) {
                dialog.show();
            }

        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                imageDataGetterSetters = db.getUplaodImageData(visit_date);
                String final_xml = "";
                if (counter == 1) {
                    ImageDataGetterSetter data = new ImageDataGetterSetter();
                    data.setLat(latitude + "");
                    data.setLon(longitude + "");
                    data.setRemark(remark);
                    data.setIntime(intime);
                    data.setImg1(img_str);
                    data.setImg2(img_str2);
                    data.setStatus(CommonString.KEY_N);
                    imageDataGetterSetters.add(data);
                }

                // calls cycle data
                final_xml = "";
                String onXML = "";
                for (int i = 0; i < imageDataGetterSetters.size(); i++) {
                    if (!imageDataGetterSetters.get(i).getStatus().equals(CommonString.KEY_D)) {
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
                        if (onXML != "") {
                            final_xml = final_xml + onXML;
                        }
                    }

                }
                if (onXML != "") {
                    final String sos_xml1 = "[DATA]" + final_xml + "[/DATA]";

                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                    request.addProperty("XMLDATA", sos_xml1);
                    request.addProperty("KEYS", "RAIL_TEL_IMAGE_DATA");
                    request.addProperty("USERNAME", username);
                    request.addProperty("MID", 0);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL, 20000);
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

                for (int i = 0; i < imageDataGetterSetters.size(); i++) {

                    if (!imageDataGetterSetters.get(i).getImg1().equalsIgnoreCase("")) {
                        if (new File(CommonString.FILE_PATH + imageDataGetterSetters.get(i).getImg1()).exists()) {
                            File originalFile = new File(CommonString.FILE_PATH + imageDataGetterSetters.get(i).getImg1());
                            result = CommonFunctions.UploadImage(originalFile.getName(), "installProofImages");
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                isError = true;
                            }
                            resultFinal = result.toString();
                        }
                    }

                    if (!imageDataGetterSetters.get(i).getImg2().equalsIgnoreCase("")) {
                        if (new File(CommonString.FILE_PATH + imageDataGetterSetters.get(i).getImg2()).exists()) {
                            File originalFile = new File(CommonString.FILE_PATH + imageDataGetterSetters.get(i).getImg2());
                            result = CommonFunctions.UploadImage(originalFile.getName(), "installProofImages");
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                isError = true;
                            }
                            resultFinal = result.toString();
                        }

                    }

                }
            } catch (MalformedURLException e) {
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
            if (result.equals(CommonString.KEY_SUCCESS)) {
                counter = 1;
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureImageActivity.this);
                builder.setTitle("Parinaam");
                builder.setMessage(CommonString.MESSAGE_UPLOAD_DATA).setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.open();
                                data = db.getCaptureUploadData(visit_date);

                                if (data.size() > 0) {
                                    captureCount = data.get(0).getCaptue();
                                    uploadCount = data.get(0).getUpload();
                                    uploadCount = uploadCount + imageDataGetterSetters.size();
                                    captureCount++;
                                    db.insertCaptureUploadCount(uploadCount, captureCount, visit_date);
                                    txtUploaded.setText(uploadCount + "");
                                    txtTakenPicture.setText(captureCount + "");
                                } else {
                                    db.insertCaptureUploadCount(1, 1, visit_date);
                                    txtUploaded.setText("1");
                                    txtTakenPicture.setText("1");
                                }

                                db.deleteImageData();
                                clearData();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                //inserting Capture Count
                db.open();
                if (counter == 1) {
                    data = db.getCaptureUploadData(visit_date);
                    if (data.size() > 0) {
                        captureCount = data.get(0).getCaptue();
                        uploadCount = data.get(0).getUpload();
                        captureCount++;
                        db.insertCaptureUploadCount(uploadCount, captureCount, visit_date);
                        txtUploaded.setText(uploadCount + "");
                        txtTakenPicture.setText(captureCount + "");
                    } else {
                        db.insertCaptureUploadCount(0, 1, visit_date);
                        txtUploaded.setText("0");
                        txtTakenPicture.setText("1");
                    }
                }

                if (dataFlag) {
                    //D = upload ,N = Not Upload

                    if (counter == 1)
                        db.insertImageData(0, username, latitude, longitude, remark, img_str, img_str2, intime, visit_date, CommonString.KEY_D);
                        db.updateCaptureStatus();
                } else {
                    if (counter == 1)
                        db.insertImageData(0, username, latitude, longitude, remark, img_str, img_str2, intime, visit_date, CommonString.KEY_N);
                }

                counter++;

                if (counter < 4) {
                    new UploadAsyncTask().execute();
                } else {
                    counter = 1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(CaptureImageActivity.this);
                    builder.setTitle("Parinaam");
                    builder.setMessage(" Network issue data not uploaded on server").setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    clearData();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    }

    private void clearData() {
        etRemark.setText("");
        img_clicked.setVisibility(View.VISIBLE);
        img_cam.setVisibility(View.GONE);
        img_clicked2.setVisibility(View.VISIBLE);
        img_cam2.setVisibility(View.GONE);
        img_str = "";
        img_str2 = "";
    }
}
