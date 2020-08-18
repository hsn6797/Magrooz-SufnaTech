package com.sufnatech.magrooz.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Publisher;
import com.opentok.android.Subscriber;
import com.sufnatech.magrooz.R;

import com.opentok.android.OpentokError;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity  implements
        Session.SessionListener,
        PublisherKit.PublisherListener{


    public  static  String Log_tag = VideoChatActivity.class.getSimpleName();

    public  static String API_KEY = "46886134";


    public static final int RC_setting = 545;



    private com.opentok.android.Session mSession;


    private FrameLayout subsciberContainer;
    private FrameLayout publisherContainer;
    private ImageButton EndCallButton;


    private static Publisher publisher;
    private Subscriber subscriber;



//    // Go to StartLiveTalkActivity with sessionID
//    Intent intent = new Intent(StartLiveTalkActivity.this,VideoChatActivity.class);
//                intent.putExtra("sessionID",map.get("sessionID").toString());
//                intent.putExtra("sessionToken",map.get("sessionToken").toString());
//                intent.putExtra("currentSessionTableID",currentSessionTableID);


    String SESSION_TABLE_ID = "";

    String SESSION_ID = "";
    String TOKEN = "";



    private void init(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SESSION_TABLE_ID = extras.getString("currentSessionTableID");
            SESSION_ID =  extras.getString("sessionID");
            TOKEN = extras.getString("sessionToken");
        }else{
            finish();
        }


        subsciberContainer = (FrameLayout)findViewById(R.id.subsciberContainer);
        publisherContainer =(FrameLayout)findViewById(R.id.publisherContainer);
        EndCallButton =(ImageButton) findViewById(R.id.EndCall);

        request_permissions();
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
    private void request_permissions()  {

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

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public void onConnected(Session session) {

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
        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }
}
