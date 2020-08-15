package com.sufnatech.magrooz.Activities;

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
import com.sufnatech.magrooz.R;

import java.util.HashMap;
import java.util.Map;


enum Gender{
    Male,
    Female
}

public class LoginActivity extends AppCompatActivity {


    private AdView mAdView;

    private ImageButton mButton;
    private ImageButton fButton;
    private Button letGoButton;
    private Gender selectedGender;


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
        selectedGender = Gender.Male;


        letGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: This function is only for testing fireStore connection
                // fireStoreTest();

                Gender lookingForGender =
                        selectedGender == Gender.Male ? Gender.Female : Gender.Male;
                Connecting(lookingForGender);
            }
        });

    }

    private void Connecting(Gender lookingForGender) {

        // TODO: Create User in database

        // TODO: Query database for lookingForGender who's status is searching

        // TODO: select random User from the list

        // TODO: Check if its own and selected user status is not Reserved
        // TODO: Then Connecting this User with selected User

        // TODO: Go to video screen
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize all resources
        init();

    }

    private void fireStoreTest(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("connectedToID", "12345678");
        user.put("gender", "M");
        user.put("status", "S");

        // Add a new document with a generated ID
        db.collection("Users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("HH: ", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("HH: ", "Error adding document", e);
                    }
                });
    }

}