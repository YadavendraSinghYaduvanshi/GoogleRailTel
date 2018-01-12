package com.cpm.googlerailtel.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cpm.googlerailtel.R;
import com.cpm.googlerailtel.autoupdate.AutoUpdateActivity;
import com.cpm.googlerailtel.constant.AlertMessages;
import com.cpm.googlerailtel.constant.CommonFunctions;
import com.cpm.googlerailtel.constant.CommonString;
import com.cpm.googlerailtel.constant.SharedPreferenceUtility;
import com.cpm.googlerailtel.gps.GPSTracker;
import com.cpm.googlerailtel.xmlGetterSetter.FailureGetterSetter;
import com.cpm.googlerailtel.xmlGetterSetter.LoginGetterSetter;
import com.cpm.googlerailtel.xmlHandler.XMLHandlers;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mUseridView;
    private Button mSignInButton;
    private EditText mPasswordView;
    private String app_ver;
    private int versionCode;
    private String username,password,p_username,p_password;
    private Context context;
    private SharedPreferenceUtility preferences;
    double latitude,longitude;
    int eventType;
    LoginGetterSetter lgs;
    boolean loginflag = true;
    String error_msg;
    static int counter = 1;
    // GPSTracker class
    GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            app_ver = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        uiDeclaration();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.password || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        // Create a Folder for Images
        File file = new File(Environment.getExternalStorageDirectory(), "Google_Rail_Tel_Images_New");
        if (!file.isDirectory()) {
            file.mkdir();
        }
    }


    private boolean isuseridValid(String userid) {
        //TODO: Replace this with your own logic
        boolean flag = true;
        String u_id = preferences.getStringData(CommonString.KEY_USERNAME);
        if (!u_id.equals("") && !userid.equalsIgnoreCase(u_id)) {
            flag = false;
        }
        return flag;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        boolean flag = true;
        String pw = preferences.getStringData(CommonString.KEY_PASSWORD);
        if (!pw.equals("") && !password.equals(pw)) {
            flag = false;
        }
        return flag;
    }

    private void attemptLogin() {
        // Reset errors.
        mUseridView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        username = mUseridView.getText().toString().trim();
        password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.S
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_enter_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid userid address.
        if (TextUtils.isEmpty(username)) {
            mUseridView.setError(getString(R.string.error_enter_username));
            focusView = mUseridView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else if (!isuseridValid(username)) {
            Snackbar.make(mUseridView, getString(R.string.error_incorrect_username), Snackbar.LENGTH_SHORT).show();
        } else if (!isPasswordValid(password)) {
            Snackbar.make(mUseridView, getString(R.string.error_incorrect_password), Snackbar.LENGTH_SHORT).show();
        } else {
            if (CommonFunctions.CheckNetAvailability(context))
            {
                gps = new GPSTracker(LoginActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){
                     latitude = gps.getLatitude();
                     longitude = gps.getLongitude();
                    //new AuthenticateTask(this,username,password,latitude,longitude,app_ver).execute();
                    new AuthenticateTask().execute();
                    // \n is for new line
                   // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            } else {
                AlertMessages.showToastMsg(context, "No Internet Connection");
            }
        }

    }


    private class AuthenticateTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("Login");
            dialog.setMessage("Authenticating....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String resultHttp = "";
                error_msg = "";
                String userauth_xml =
                        "[DATA]"
                                + "[USER_DATA]"
                                + "[USER_ID]" + username + "[/USER_ID]"
                                + "[Password]" + password + "[/Password]"
                                + "[IN_TIME]" + CommonFunctions.getCurrentTime() + "[/IN_TIME]"
                                + "[LATITUDE]" + latitude + "[/LATITUDE]"
                                + "[LONGITUDE]" + longitude + "[/LONGITUDE]"
                                + "[APP_VERSION]" + app_ver + "[/APP_VERSION]"
                                + "[ATT_MODE]OnLine[/ATT_MODE]"
                                + "[/USER_DATA]"
                                + "[/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_LOGIN);
                request.addProperty("onXML", userauth_xml);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_LOGIN, envelope);

                Object result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    error_msg = CommonString.MESSAGE_FAILURE;
                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    error_msg = CommonString.MESSAGE_FALSE;
                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_CHANGED)) {
                    error_msg = CommonString.MESSAGE_CHANGED;
                } else {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    FailureGetterSetter failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        error_msg = CommonString.METHOD_LOGIN + failureGetterSetter.getErrorMsg();
                    } else {
                        try {
                            // For String source
                            xpp.setInput(new StringReader(result.toString()));
                            xpp.next();
                            eventType = xpp.getEventType();
                            lgs = XMLHandlers.loginXMLHandler(xpp, eventType);
                        } catch (XmlPullParserException e) {
                            error_msg = CommonString.MESSAGE_EXCEPTION;
                            e.printStackTrace();
                        } catch (IOException e) {
                            error_msg = CommonString.MESSAGE_SOCKETEXCEPTION;
                            e.printStackTrace();
                        }
                        loginflag = true;
                        // PUT IN PREFERENCES
                        preferences = SharedPreferenceUtility.getInstance(context);
                        preferences.setStringData(CommonString.KEY_USERNAME, username);
                        preferences.setStringData(CommonString.KEY_PASSWORD, password);
                        preferences.setStringData(CommonString.KEY_VERSION, lgs.getVERSION());
                        preferences.setStringData(CommonString.KEY_PATH, lgs.getPATH());
                        // editor.putString(CommonString.KEY_DATE, "04/05/2017");
                        preferences.setStringData(CommonString.KEY_DATE, lgs.getDATE());
                        preferences.setStringData(CommonString.KEY_USER_TYPE, lgs.getRIGHTNAME());
                        return CommonString.KEY_SUCCESS;
                    }

                    return resultHttp;
                }

                //  return "";

            } catch (MalformedURLException e) {
                loginflag = false;
                error_msg = CommonString.MESSAGE_EXCEPTION;
                e.printStackTrace();

            } catch (Exception e) {
                loginflag = false;
                error_msg = CommonString.MESSAGE_SOCKETEXCEPTION;
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
            if (loginflag) {
                if (result.equals(CommonString.KEY_SUCCESS)) {
                    if (lgs.getVERSION().equals(Integer.toString(versionCode))) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getBaseContext(), AutoUpdateActivity.class);
                        intent.putExtra(CommonString.KEY_PATH, preferences.getStringData(CommonString.KEY_PATH));
                        startActivity(intent);
                        finish();
                    }
                } else {
                    AlertMessages.showAlert((Activity) context, "Error in login :" + error_msg, false);
                }
            } else {
                AlertMessages.showAlert((Activity) context, "Error in login :" + error_msg, false);
            }
        }
    }




    private void uiDeclaration()
    {
        context = this;
        TextView tv_version = (TextView) findViewById(R.id.tv_version_code);
        tv_version.setText("Version" + app_ver);
        // Set up the login form.
        mUseridView = (AutoCompleteTextView) findViewById(R.id.user_id);
        mPasswordView = (EditText) findViewById(R.id.password);
        mSignInButton = (Button) findViewById(R.id.user_login_button);
        mUseridView.setText("");
        mPasswordView.setText("");
        preferences = SharedPreferenceUtility.getInstance(this);
        p_username = preferences.getStringData(CommonString.KEY_USERNAME);
        p_password = preferences.getStringData(CommonString.KEY_PASSWORD);
    }

}

