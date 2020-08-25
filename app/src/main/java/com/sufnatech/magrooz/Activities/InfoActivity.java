package com.sufnatech.magrooz.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sufnatech.magrooz.R;

public class InfoActivity extends AppCompatActivity {

    ImageButton FB;
    ImageButton Tw;
    ImageButton Gmail;
    ImageButton Message;


    TextView review;
    TextView privacy;
    TextView contact;
    TextView moreapp;


    public static final String facebook ="com.facebook.katana";
    public static final String Twitter = "com.twitter.android";
    public static final String gmailIntent = "com.google.android.gm";


    String MoreAppUR = "http://play.google.com/store/apps/developer?id=Aman+sufnatech&hl=en";

    String AppUrL = "https://play.google.com/store/apps/details?id=com.barcode.qrcode.scanner.reader.pro";

    String PrivacyURL = "https://www.freeprivacypolicy.com/blog/privacy-policy-url/";


    String OurEmailAdd = "aman.sufnatech@gmail.com";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);


        FB = (ImageButton)findViewById(R.id.fbB);
        Tw = (ImageButton) findViewById(R.id.tB);
        Gmail = (ImageButton) findViewById(R.id.emailB);
        Message = (ImageButton) findViewById(R.id.mB);

        review = (TextView) findViewById(R.id.rrT);
        privacy = (TextView) findViewById(R.id.ppT);

        contact = (TextView) findViewById(R.id.cuT);
        moreapp = (TextView) findViewById(R.id.maT);

        // Share Buttons here::::::

        FB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharingToSocialMedia(facebook);

            }
        });
        Tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingToSocialMedia(Twitter);

            }
        });
        Gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharingToSocialMedia(gmailIntent);
            }
        });
        Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String MessageIntent = Telephony.Sms.getDefaultSmsPackage(InfoActivity.this);

                SharingToSocialMedia(MessageIntent);

            }
        });


        // Other links here::::


        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                privacyorreviewApp(AppUrL);

            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                privacyorreviewApp(PrivacyURL);

            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contactUS();

            }
        });

        moreapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                privacyorreviewApp(MoreAppUR);
            }
        });

    }


    public  void privacyorreviewApp(String URL){
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void contactUS (){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", OurEmailAdd, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "For Application Magrooz");
        startActivity(emailIntent);

    }



    public void SharingToSocialMedia(String application){



        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share my App");
        intent.putExtra(Intent.EXTRA_TEXT,"\n\nUse this App and share with your friends\n\n"+AppUrL);
        intent.putExtra(Intent.EXTRA_STREAM, application);

        boolean installed = checkAppInstall(application);
        if (installed) {
            intent.setPackage(application);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Install application first", Toast.LENGTH_LONG).show();
        }

    }


    private boolean checkAppInstall(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);

            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

}