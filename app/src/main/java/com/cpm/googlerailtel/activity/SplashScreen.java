package com.cpm.googlerailtel.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cpm.googlerailtel.R;
import com.cpm.googlerailtel.constant.CommonFunctions;
import com.cpm.googlerailtel.constant.CommonString;
import com.cpm.googlerailtel.constant.SharedPreferenceUtility;
import com.cpm.googlerailtel.database.GoogleRailTelDB;
import com.cpm.googlerailtel.xmlGetterSetter.FailureGetterSetter;
import com.cpm.googlerailtel.xmlGetterSetter.ImageDataGetterSetter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private ProgressDialog dialog = null;
    static int counter = 1;
    private SharedPreferenceUtility preferences;
    String  visit_date, username;
    private GoogleRailTelDB db;
    Object result;
    boolean dataFlag= false;
    boolean isError = false,up_success_flag = true;
    private FailureGetterSetter failureGetterSetter = null;
    String exceptionMessage = "",errormsg="",resultFinal;
    int captureCount = 0,uploadCount =0;
    int imageCount=0;
    Data data;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        File dir = new File(CommonString.FILE_PATH_OLD);
        ArrayList<String> list1 = new ArrayList();
        if(dir.listFiles() != null) {
            list1 = getFileNames(dir.listFiles());
            if (list1.size() > 0) {
                imageCount = list1.size();
                new UploadAsyncTask().execute();
            } else {
                StartAnimations();
                new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

                    @Override
                    public void run() {
                        Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }
        }else{
            StartAnimations();
            new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

                @Override
                public void run() {
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }

    }


    private class UploadAsyncTask extends AsyncTask<Void, Data, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SplashScreen.this);
            dialog.setTitle("Uploading Previous Day Images");
            dialog.setMessage("Uploading Image " + "1" +"/" + imageCount);
            dialog.setCancelable(false);

                dialog.show();

        }

        @Override
        protected String doInBackground(Void... voids) {
            up_success_flag = true;
            resultFinal = CommonString.KEY_SUCCESS;
            try{
                data = new Data();
                File dir = new File(CommonString.FILE_PATH_OLD);
                ArrayList<String> imageList = new ArrayList();
                imageList = getFileNames(dir.listFiles());

                    for(int i=0;i<imageList.size();i++) {

                        data.value = i+1;
                        data.total = imageList.size();

                        publishProgress(data);
                        if (new File(CommonString.FILE_PATH_OLD + imageList.get(i)).exists()) {
                            File originalFile = new File(CommonString.FILE_PATH_OLD +  imageList.get(i));
                            result = CommonFunctions.UploadPrevImage(originalFile.getName(), "installProofImages");
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                isError = true;
                            }
                            resultFinal = result.toString();
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
        protected void onProgressUpdate(Data... values) {
            //super.onProgressUpdate(values);
            // TODO Auto-generated method stub
            dialog.setMessage("Uploading Image - " + values[0].value +"/" + values[0].total);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
                dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS))
            {
                dialog.dismiss();
                counter=1;
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                builder.setTitle("Parinaam");
                builder.setMessage(CommonString.MESSAGE_UPLOAD_DATA).setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else {

                counter++;

                if (counter < 4) {
                    new UploadAsyncTask().execute();
                } else {
                    counter = 1;
                    dialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                    builder.setTitle("Parinaam");
                    builder.setMessage(" Network issue image not uploaded on server").setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    }


    class Data {
        int value;
        int total;
        String name;
    }


    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }

    private void StartAnimations()
    {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);
    }


}
