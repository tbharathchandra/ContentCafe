package com.ssdevelopers.blotzmann.gkworld;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView developer,contactUs,privacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        toolbar = (Toolbar) findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        developer = (TextView) findViewById(R.id.info_developer);
        contactUs = (TextView) findViewById(R.id.contact_us);
        privacyPolicy = (TextView) findViewById(R.id.privacy_policy_link);

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openUrl = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ssdevelopersindia.blogspot.com/p/privacy-policy-content-cafe.html"));
                startActivity(openUrl);
            }
        });
        developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openUrl = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tbharathchandra"));
                startActivity(openUrl);
            }
        });
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","ssdevelopersindia2000@gmail.com", null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }
}
