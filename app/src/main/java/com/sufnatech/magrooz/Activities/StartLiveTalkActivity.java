package com.sufnatech.magrooz.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sufnatech.magrooz.Helpers.Dialog.AlertDialogBox;
import com.sufnatech.magrooz.Helpers.Dialog.AlertDialogSingleInterface;
import com.sufnatech.magrooz.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.google.android.gms.ads.InterstitialAd;
//import com.yarolegovich.lovelydialog.LovelyStandardDialog;


public class StartLiveTalkActivity extends AppCompatActivity {

    private static final String TAG = "StartLiveTalkActivity: ";

    public  static  String Log_tag = StartLiveTalkActivity.class.getSimpleName();

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    private static String interstitalad = "ca-app-pub-1410667554869432/1247637562";





    String userID;
    Gender gender;
    String lookingForGender;

    Button startLiveTalk;
    FirebaseFirestore db;


    String currentSessionTableID="";
    String SESSION_ID = "";
    String TOKEN = "";

    boolean isRedirectLogin ;






    private void init(){


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userID");
            gender = (Gender) extras.get("gender");
            lookingForGender = extras.getString("lookingForGender");

            isRedirectLogin = extras.getBoolean("isRedirect");
        }else{
           // finish();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });




        db = FirebaseFirestore.getInstance();



        Log.i(Log_tag, "SESSION_TABLE: " + currentSessionTableID);


        startLiveTalk = (Button) findViewById(R.id.StartLiveTalkB);

        startLiveTalk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                doWork();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isRedirectLogin){
            //Do nothing
            LoadBanner();
        }
        else{
            LoadAdswhenLoadingScreen();
            LoadBanner();

        }


    }

    private void LoadBanner(){

        mAdView = findViewById(R.id.adView2);
      //  AdSize adSize = new AdSize(300, 50);
       // mAdView.setAdSize(adSize);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }

    private void LoadAdswhenLoadingScreen(){



        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(interstitalad);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());



        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();

            }
        });

    }


    private void doWork(){


        // Searching for a free Sessions
        db.collection("SessionMag").whereEqualTo("subscriberID","")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        // Searching for Available Session on the bases of empty subscriberID field
                        if (task.isSuccessful() && task.getResult().size() > 0) {
                            Log.d(TAG, "Available Sessions\n");


                            // 1- Select Random Session
                            List<DocumentSnapshot> sessionsList = task.getResult().getDocuments();
                            DocumentSnapshot document = null;

                            for (DocumentSnapshot doc:
                                 sessionsList) {
                               // if(doc.get("lookingForGender") == gender)
                               String c =  doc.get("lookingForGender").toString();
                                Log.i(Log_tag, "Genders: " + c);

                                if(c.equals(gender.toString())){
                                    Log.i(Log_tag, "docfound:"+ gender);

                                    document = doc;

                                    break;
                                }
                                else{


                                    break;

                                }
                            }
                            if(document == null){

                                LayoutInflater inflater = LayoutInflater.from(StartLiveTalkActivity.this);
                                View view = inflater.inflate(R.layout.alert_dialogue,null);

                                Button Accept = view.findViewById(R.id.okbtn);
                                Button reject = view.findViewById(R.id.cancelbtn);


                                final AlertDialog alertDialog = new AlertDialog.Builder(StartLiveTalkActivity.this)
                                        .setView(view).create();
                                alertDialog.setCancelable(false);

                                Accept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        db.collection("SessionMag")
                                                .whereEqualTo("subscriberID","")
                                                .whereEqualTo("lookingForGender",lookingForGender)//Lookingforgender
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                if (task2.isSuccessful() && task2.getResult().size() > 0) {
                                                    // 1- Select Random Session
                                                    int index = new Random().nextInt(task2.getResult().getDocuments().size());

                                                    DocumentSnapshot document2 = task2.getResult().getDocuments().get(index);

                                                    // 2- Update the session and insert the userID in subscriberID field
                                                    // and move to next video screen
                                                    Map<String, Object> sessionMap = new HashMap<>();
                                                    sessionMap.put("publisherID", document2.get("publisherID"));
                                                    sessionMap.put("sessionID", document2.get("sessionID"));
                                                    sessionMap.put("sessionToken", document2.get("sessionToken"));
                                                    sessionMap.put("subscriberID", userID);
                                                    sessionMap.put("lookingForGender", document2.get("lookingForGender"));

                                                    updateSessionSubsciberID(sessionMap,document2.getId());

                                                }
                                                else{

                                                }
                                            }
                                        });//end of listener here....

                                    }
                                });

                                reject.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        alertDialog.dismiss();


                                    }
                                });
                                alertDialog.show();

//
//                                new LovelyStandardDialog(StartLiveTalkActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
//                                        .setTopColorRes(R.color.BackgroundApp)
//                                        .setButtonsColorRes(R.color.whitealert)
//                                        .setIcon(R.drawable.alert)
//                                        .setCancelable(false)
//                                        .setTitle("Opsss!")
//                                        .setMessage("No opposite gender found! Would you like chat with same sex?")
//                                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//
//
//
//
//                                            }
//                                        })
//                                        .setNegativeButton(android.R.string.no, null)
//                                        .show();

                            }
                            else {
//                            DocumentSnapshot document = sessionsList.get(index);
//                            int index = new Random().nextInt(sessionsList.size());

                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // 2- Update the session and insert the userID in subscriberID field
                                // and move to next video screen
                                Map<String, Object> sessionMap = new HashMap<>();
                                sessionMap.put("publisherID", document.get("publisherID"));
                                sessionMap.put("sessionID", document.get("sessionID"));
                                sessionMap.put("sessionToken", document.get("sessionToken"));
                                sessionMap.put("subscriberID", userID);
                                sessionMap.put("lookingForGender", document.get("lookingForGender"));

                                updateSessionSubsciberID(sessionMap, document.getId());

//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
                            }
                        }
                        else{

                            // getting token ,Session, from server
                            fetchSessionforConnection();


                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(userID != ""){
            deleteUser();

        }
        // Delete the created user from database where id = currentSessionID


        if (currentSessionTableID != ""){
            deleteSession();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
                // Delete the created user from database where id = userID
        if(userID != ""){
            deleteUser();

        }
        // Delete the created user from database where id = currentSessionID


        if (currentSessionTableID != ""){
             deleteSession();
        }
        finish();
    }



    private void fetchSessionforConnection(){

        // Loading Start
        final Dialog dialog = AlertDialogBox.showLoadingDialog(StartLiveTalkActivity.this);
        dialog.show();

        RequestQueue reqQueue = Volley.newRequestQueue(this);
        reqQueue.add(new JsonObjectRequest(Request.Method.GET,
                "https://magroozvideoapp.herokuapp.com" + "/session",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    SESSION_ID = response.getString("sessionId");
                    TOKEN = response.getString("token");

                    // Create Session in firebase
                    Map<String, Object> sessionCreateMap = new HashMap<>();
                    sessionCreateMap.put("publisherID", userID);
                    sessionCreateMap.put("sessionID", SESSION_ID);
                    sessionCreateMap.put("sessionToken", TOKEN);
                    sessionCreateMap.put("subscriberID", "");
                    sessionCreateMap.put("lookingForGender", lookingForGender);

                    createSession(sessionCreateMap);

                    // Loading finished
                    dialog.dismiss();

                    Log.i(Log_tag, "SESSION_ID: " + SESSION_ID);
                    Log.i(Log_tag, "TOKEN: " + TOKEN);

                } catch (JSONException error) {
                    Log.e(Log_tag, "Web Service error: " + error.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }



    private void createSession(final Map<String,Object> map){
//        // Loading Start
//        final Dialog dialog = AlertDialog.showLoadingDialog(StartLiveTalkActivity.this);
//        dialog.show();
        db.collection("SessionMag").add(map).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        // Loading finished
//                        dialog.dismiss();

                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        final String sessionID = documentReference.getId();
                        currentSessionTableID = sessionID;



                        // Go to StartLiveTalkActivity with sessionID
                        Intent intent = new Intent(StartLiveTalkActivity.this,VideoChatActivity.class);
                        intent.putExtra("sessionID",map.get("sessionID").toString());
                        intent.putExtra("sessionToken",map.get("sessionToken").toString());
                        intent.putExtra("currentSessionTableID",currentSessionTableID);
                        intent.putExtra("CurrentUserID",userID);
                        intent.putExtra("UserType","P");

                        isRedirectLogin = false;
                       startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                // Loading finished
//                dialog.dismiss();

                // Display Some error message on screen.
                AlertDialogBox.showSingleButtonAlertDialog(StartLiveTalkActivity.this,"Ok",
                        "Error","Some error occur. Please try again later",
                        new AlertDialogSingleInterface(){
                            @Override
                            public void doTaskOnClick() {
                            }
                        });
            }
        });
    }
    private void updateSessionSubsciberID(final Map<String,Object> map, final String sessionID){

        // Loading Start
        final Dialog dialog = AlertDialogBox.showLoadingDialog(StartLiveTalkActivity.this);
        dialog.show();

        db.collection("SessionMag")
                .document(sessionID)
                .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Loading finished
                dialog.dismiss();

                currentSessionTableID = sessionID;

                // Go to StartLiveTalkActivity with sessionID
                Intent intent = new Intent(StartLiveTalkActivity.this,VideoChatActivity.class);
                intent.putExtra("sessionID",map.get("sessionID").toString());
                intent.putExtra("sessionToken",map.get("sessionToken").toString());
                intent.putExtra("currentSessionTableID",currentSessionTableID);
                intent.putExtra("CurrentUserID",userID);
                intent.putExtra("UserType","S");



                isRedirectLogin = false;

                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Loading finished
                dialog.dismiss();
            }
        });

    }
    private void deleteUser(){
        db.collection("UsersMag")
                .document(userID)
                .delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO - If user not deleted successfully handle it here
            }
        });
    }
    private void deleteSession(){
        db.collection("SessionMag")
                .document(currentSessionTableID)
                .delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO - If session not deleted successfully handle it here
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_live_talk);
        init();
    }
}
//
