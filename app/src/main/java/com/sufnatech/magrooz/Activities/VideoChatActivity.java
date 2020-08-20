package com.sufnatech.magrooz.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.Publisher;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.sufnatech.magrooz.Helpers.Dialog.AlertDialog;
import com.sufnatech.magrooz.R;

import com.opentok.android.OpentokError;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity  implements
        Session.SessionListener,
        PublisherKit.PublisherListener,Subscriber.SubscriberListener{


    public static  String Log_tag = VideoChatActivity.class.getSimpleName();

    public static String API_KEY = "46890114";

    public static final int RC_setting = 124;


    FirebaseFirestore db;


    private FrameLayout subsciberContainer;
    private FrameLayout publisherContainer;
    private ImageButton EndCallButton;
    private ImageButton swapcamera;
    private ImageButton muteMIC;
    private TextView Waitingforsubs;




    private static Publisher publisher;
    private Subscriber subscriber;
    private Session mSession;

    String SESSION_TABLE_ID = "";
    String SESSION_ID = "";
    String TOKEN = "";


    String currentUserID = "";
    String UserType = "";
    boolean mic_mute = false;

    CountDownTimer Timer;
    private long intervalmillis = 10000;
    boolean subscribercame;


    private void init(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SESSION_TABLE_ID = extras.getString("currentSessionTableID");
            SESSION_ID =  extras.getString("sessionID");
            TOKEN = extras.getString("sessionToken");
            currentUserID = extras.getString("CurrentUserID");
            UserType = extras.getString("UserType");


        }else{
           // finish();
        }


        //getDB instanse here
        db = FirebaseFirestore.getInstance();


        Log.i(Log_tag, "TABLEID: " + SESSION_TABLE_ID);
        Log.i(Log_tag, "TABLEUSER: " + UserType);



        subsciberContainer = (FrameLayout)findViewById(R.id.subsciberContainer);
        publisherContainer =(FrameLayout)findViewById(R.id.publisherContainer);
        EndCallButton =(ImageButton) findViewById(R.id.EndCall);
        swapcamera =(ImageButton)findViewById(R.id.Swapcamera);
        muteMIC =(ImageButton)findViewById(R.id.micMute);
        Waitingforsubs = (TextView)findViewById(R.id.LoadingTxt);




        //Setting the timer for subscriber come:






        EndCallButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

               endCallFunction();

            }
        });

        swapcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    publisher.swapCamera();

            }
        });
        muteMIC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mic_mute == false){
                    mic_mute = true;
                    publisher.setPublishAudio(false);
                    muteMIC.setBackgroundResource(R.drawable.unmute);
                }
                else{
                    mic_mute = false;
                    publisher.setPublishAudio(true);
                    muteMIC.setBackgroundResource(R.drawable.mute);
                }
            }
        });



        request_permissions();
    }


    private void endCallFunction(){


            if (publisher != null && subscriber != null){

                    mSession.unpublish(publisher);
                    mSession.unsubscribe(subscriber);

            }
            else if (publisher != null){


                    mSession.unpublish(publisher);
                    if (subscriber != null){
                        mSession.unsubscribe(subscriber);
                    }



            }
            else if (subscriber != null){

                    mSession.unpublish(publisher);
                    mSession.unsubscribe(subscriber);


            }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        init();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);


    }

    @AfterPermissionGranted(RC_setting)
    private void request_permissions(){

        String[] perm = {Manifest.permission.INTERNET, Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};

        if(EasyPermissions.hasPermissions(this,perm))
        {

            mSession = new Session.Builder(this,API_KEY,SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);

        }
        else {
            EasyPermissions.requestPermissions(this,"This app need to access your camera and mic",RC_setting,perm);
        }

    }


    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {


    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

        publisher = null;
            finish();

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }



    @Override
    public void onConnected(Session session) {

        final Dialog dialog = AlertDialog.showLoadingDialog(VideoChatActivity.this);



            publisher = new Publisher.Builder(this).name("publisher").build();
            publisher.setPublisherListener(this);

            publisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            publisherContainer.addView(publisher.getView(),0);


            if(publisher.getView() instanceof GLSurfaceView)
            {
                ((GLSurfaceView) publisher.getView()).setZOrderOnTop(true);

            }

            session.publish(publisher);
        }




    @Override
    public void onDisconnected(Session session) {


    }


    @Override
    public void onStreamReceived(Session session, Stream stream) {


        if(subscriber == null){
            Waitingforsubs.setText("Connected!");
            Waitingforsubs.setVisibility(View.INVISIBLE);

            subscriber = new Subscriber.Builder(this,stream).build();
            session.subscribe(subscriber);
            subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            subsciberContainer.addView(subscriber.getView());

        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        if (subscriber != null){

            subscriber = null;
            subsciberContainer.removeAllViews();

            finish();
        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {

    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {


    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {

    }

    //Delete Current session from Firebase
    private void deleteSessionfromDB(){

        db.collection("SessionMag")
                .document(SESSION_TABLE_ID)
                .delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // TODO - If session not deleted successfully handle it here
                    }
                });
    }

    private void deleteUser(){
        db.collection("UsersMag")
                .document(currentUserID)
                .delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO - If user not deleted successfully handle it here
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(Log_tag, "USER: " + currentUserID );
        Log.i(Log_tag, "TABLEUSER: " + SESSION_TABLE_ID );

        deleteUser();
        deleteSessionfromDB();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endCallFunction();
    }
}
