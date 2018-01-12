package com.cpm.googlerailtel.constant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by neeraj on 07/01/2018.
 */

public class AlertMessages {

    private String data, condition;
    private Activity activity;

    public AlertMessages(Activity activity, String data, String condition, Exception exception) {
        this.activity = activity;
        this.data = data;
        this.condition = condition;
    }


    public static void showToastMsg(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static void showAlert(final Activity activity, String str, final Boolean activityFinish) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (activityFinish) {
                            activity.finish();
                        } else {
                            dialog.dismiss();
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
