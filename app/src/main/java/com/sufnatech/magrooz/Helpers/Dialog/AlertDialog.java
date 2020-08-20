package com.sufnatech.magrooz.Helpers.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.sufnatech.magrooz.R;

public class AlertDialog extends AppCompatActivity {


    public void showMultiButtonAlertDialog(Context activity, String positiveButtonText, String negativeButtonText, String title, String message, final AlertDialogInterface alertDialogInterface) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialogInterface.positiveButtonPressed();
                        dialogInterface.cancel();
                    }
                }).setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogInterface.negativeButtonPressed();
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showSingleButtonAlertDialog(Activity activity, String buttonText, String title, String message, final AlertDialogSingleInterface dialogSingleInterface) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogSingleInterface.doTaskOnClick();
                        dialogInterface.cancel();
                    }
                });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static Dialog showLoadingDialog(Context activity) {
        // custom dialog
        final Dialog dialog = new Dialog(activity,R.style.AppCompatAlertDialogStyle);


        dialog.setContentView(R.layout.loading_dialog_layout);
        return dialog;
    }

}
