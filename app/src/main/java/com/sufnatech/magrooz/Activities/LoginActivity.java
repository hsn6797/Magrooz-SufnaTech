package com.sufnatech.magrooz.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sufnatech.magrooz.Helpers.Dialog.AlertDialog;
import com.sufnatech.magrooz.Helpers.Dialog.AlertDialogInterface;
import com.sufnatech.magrooz.Helpers.Dialog.AlertDialogSingleInterface;
import com.sufnatech.magrooz.R;

import java.util.HashMap;
import java.util.Map;


enum Gender{
    M,
    F
}


public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity: ";
    private AdView mAdView;

    private ImageButton mButton;
    private ImageButton fButton;
    private Button letGoButton;
    private Gender selectedGender;


    FirebaseFirestore db = FirebaseFirestore.getInstance();



    private void init(){
        // Run your ads here :
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        //Find View By ID Ads:
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        mButton = (ImageButton) findViewById(R.id.Malebtn);
        fButton = (ImageButton) findViewById(R.id.femalebtn);
        letGoButton = (Button) findViewById(R.id.letsGObtn);

        // Default value
        selectedGender = Gender.M;

        // Firebase Listeners

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = Gender.M;
                if (selectedGender == Gender.M){
                    mButton.setBackgroundResource(R.drawable.custom_person_onclick);
                    fButton.setBackgroundResource(R.drawable.custom_person_button);

                }
            }
        });
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = Gender.F;

                if (selectedGender == Gender.F){

                    fButton.setBackgroundResource(R.drawable.custom_person_onclick);
                    mButton.setBackgroundResource(R.drawable.custom_person_button);

                }

            }
        });


        letGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: This function is only for testing fireStore connection
                // fireStoreTest();

                Gender lookingForGender =
                        selectedGender == Gender.M ? Gender.F : Gender.M;
                doWork(lookingForGender);
            }
        });

    }

    private void doWork(final Gender lookingForGender){
        // Loading Start
        final Dialog dialog = AlertDialog.showLoadingDialog(LoginActivity.this);
        dialog.show();
        // 1- Create User in database
        Map<String, Object> user = new HashMap<>();
        user.put("Gender", selectedGender);

        db.collection("UsersMag").add(user).
            addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // Loading finished
                dialog.dismiss();

                Log.d(TAG, "DocumentSnapshot added with ID:" + documentReference.getId());
                final String userID = documentReference.getId();


                // Go to StartLiveTalkActivity with current userID, gender and lookingForGender
                Intent intent = new Intent(LoginActivity.this,StartLiveTalkActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("gender",selectedGender);
                intent.putExtra("lookingForGender",lookingForGender.toString());
                intent.putExtra("isRedirect",true);
                startActivity(intent);
            }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Loading finished
                dialog.dismiss();

                // Display Some error message on screen.
                AlertDialog.showSingleButtonAlertDialog(LoginActivity.this,"Ok","Error","Some error occur. Please try again later",new AlertDialogSingleInterface(){
                    @Override
                    public void doTaskOnClick() {
                    }
                });
            }
        });
    }

//    private void fireStoreTest(){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("connectedToID", "12345678");
//        user.put("gender", "M");
//        user.put("status", "S");
//
//        // Add a new document with a generated ID
//        db.collection("Users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d("HH: ", "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("HH: ", "Error adding document", e);
//                    }
//                });
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize all resources
        init();

    }

}


// TODO- This code is for display two buttons Dialog box
//AlertDialog ad = new AlertDialog();
//
//ad.showMultiButtonAlertDialog(context, "Delete", "Cancel", message+" "+mRecentlyDeletedItem.getA_siteName(), "This action will delete you file permanently", new AlertDialogInterface() {
//
//@Override
//public void positiveButtonPressed() {
//Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show();
//DeleteRow(Integer.parseInt(mRecentlyDeletedItem.getA_Id()));
//
//}
//
//@Override
//public void negativeButtonPressed() {
//
//}
//});