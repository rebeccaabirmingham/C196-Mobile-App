package com.example.wgu.c196termscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CHANNEL_ID = "myNotifyChannel";
    public static int count = 0;

    public static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set buttons
        Button btnViewTerms = findViewById(R.id.buttonViewTerms);
        btnViewTerms.setOnClickListener(this);
        Button btnViewCourses = findViewById(R.id.buttonViewCourses);
        btnViewCourses.setOnClickListener(this);
        Button btnViewAssessments = findViewById(R.id.buttonViewAssessments);
        btnViewAssessments.setOnClickListener(this);
        Button btnViewMentors = findViewById(R.id.buttonViewMentors);
        btnViewMentors.setOnClickListener(this);

        count = count++;
        //should only run once at startup
        if (count < 2) {
            createNotificationChannel();
            //create the initial sharedPref file
            sharedPref = this.getSharedPreferences(getString(R.string.pref_file_name), this.MODE_PRIVATE);
        }

    }

    //@Override
    public void onClick(View v) {

        int id = v.getId();

        switch(id){
            case R.id.buttonViewTerms: {
                //Toast.makeText(this, "Terms pressed", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, TermViewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.buttonViewCourses: {
                Intent intent = new Intent(this, CourseViewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.buttonViewAssessments: {
                //Toast.makeText(this, "Terms pressed", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, AssessmentViewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.buttonViewMentors: {
                Intent intent = new Intent(this, MentorViewActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel create for 8.0 and higher";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
