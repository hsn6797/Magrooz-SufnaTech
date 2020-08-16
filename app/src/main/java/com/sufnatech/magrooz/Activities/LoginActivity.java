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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sufnatech.magrooz.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


enum Gender{
    M,
    F
}
enum UserStatus{
    S,
    C
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
                DoWork(lookingForGender);
            }
        });

    }

    private void DoWork(final Gender lookingForGender) {

        // 1- Create User in database
        Map<String, Object> user = new HashMap<>();
        user.put("connectedToID", "null");
        user.put("gender", selectedGender);
        user.put("status", UserStatus.S);

        db.collection("Users").add(user).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                String userID = documentReference.getId();
                final List<QueryDocumentSnapshot> usersGot = new ArrayList();

                // 2- Query database for lookingForGender who's status is searching
                db.collection("Users").whereEqualTo("gender",lookingForGender).whereEqualTo("status",UserStatus.S)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        usersGot.add(document);
                                    }

                                    // 3- Select random User from the usersGot list
                                    Random random = new Random();
                                    int index = random.nextInt(usersGot.size());

                                    QueryDocumentSnapshot userPiked = usersGot.get(index);
                                    Log.d(TAG, "Picked Random User => "+ userPiked.getId() + " => " + userPiked.getData());

                                    // TODO: Check if its own and selected user status is not Reserved
                                    // TODO: Then Connecting this User with selected User

                                    // TODO: Go to video screen
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });





            }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);

                }
            });



    }

    private void addUserInDB(Gender gender, UserStatus status, String connectedTo){
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("connectedToID", connectedTo);
        user.put("gender", gender);
        user.put("status", status);

        // Add a new document with a generated ID
        db.collection("Users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                String userID = documentReference.getId();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);

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