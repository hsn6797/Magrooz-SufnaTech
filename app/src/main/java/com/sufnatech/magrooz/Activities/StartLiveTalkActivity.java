package com.sufnatech.magrooz.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sufnatech.magrooz.Helpers.Dialog.AlertDialog;
import com.sufnatech.magrooz.Helpers.Dialog.AlertDialogSingleInterface;
import com.sufnatech.magrooz.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StartLiveTalkActivity extends AppCompatActivity {

    private static final String TAG = "StartLiveTalkActivity: ";

    public  static  String Log_tag = StartLiveTalkActivity.class.getSimpleName();


    String userID;
    Gender gender;
    String lookingForGender;

    Button startLiveTalk;
    FirebaseFirestore db;

    String currentSessionTableID="";


    String SESSION_ID = "";
    String TOKEN = "";



    private void init(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userID");
            gender = (Gender) extras.get("gender");
            lookingForGender = extras.getString("lookingForGender");
        }else{
            finish();
        }
        db = FirebaseFirestore.getInstance();

        startLiveTalk = (Button) findViewById(R.id.StartLiveTalkB);

        startLiveTalk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doWork();
            }
        });
    }

    private void doWork(){

        // Searching for a free Sessions
        db.collection("Sessions").whereEqualTo("subscriberID","")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        // Searching for Available Session on the bases of empty subscriberID field
                        if (task.isSuccessful() && task.getResult().size() > 0) {
                            Log.d(TAG, "Available Sessions\n");

                            // 1- Select Random Session
                            List<DocumentSnapshot> sessionsList = task.getResult().getDocuments();
                            int index = new Random().nextInt(sessionsList.size());
                            DocumentSnapshot document = sessionsList.get(index);

                            Log.d(TAG, document.getId() + " => " + document.getData());

                            // 2- Update the session and insert the userID in subscriberID field
                            // and move to next video screen
                            Map<String, Object> sessionMap = new HashMap<>();
                            sessionMap.put("publisherID", document.get("publisherID"));
                            sessionMap.put("sessionID", document.get("sessionID"));
                            sessionMap.put("sessionToken", document.get("sessionToken"));
                            sessionMap.put("subscriberID", userID);

                            updateSessionSubsciberID(sessionMap,document.getId());

//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
                        } else{

                            // getting token ,Session,Apikey from server
                            fetchSessionforConnection();
//                            if(!SESSION_ID.equals("") && !TOKEN.equals("")){
                            // 1- Create Session in database and move to next video screen

//                            }else{
//                                AlertDialog.showSingleButtonAlertDialog(StartLiveTalkActivity.this,"Ok","Error",
//                                        "Please try again later",new AlertDialogSingleInterface(){
//                                    @Override
//                                    public void doTaskOnClick() {
//                                    }
//                                });
//                            }

                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Delete the created user from database where id = userID
        deleteUser();
//         Delete the created user from database where id = currentSessionID
        deleteSession();

        db = null;
    }


    private void fetchSessionforConnection(){

        // Loading Start
        final Dialog dialog = AlertDialog.showLoadingDialog(StartLiveTalkActivity.this);
        dialog.show();

        RequestQueue reqQueue = Volley.newRequestQueue(this);
        reqQueue.add(new JsonObjectRequest(Request.Method.GET,
                "https://videoapptokbox.herokuapp.com" + "/session",
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
        db.collection("Sessions").add(map).
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

                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                // Loading finished
//                dialog.dismiss();

                // Display Some error message on screen.
                AlertDialog.showSingleButtonAlertDialog(StartLiveTalkActivity.this,"Ok",
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
        final Dialog dialog = AlertDialog.showLoadingDialog(StartLiveTalkActivity.this);
        dialog.show();

        db.collection("Sessions")
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
        db.collection("Users")
                .document(userID)
                .delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO - If user not deleted successfully handle it here
            }
        });
    }
    private void deleteSession(){
        db.collection("Sessions")
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