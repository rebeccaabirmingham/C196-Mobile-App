package com.example.wgu.c196termscheduler;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wgu.c196termscheduler.TermSchedulerDBContract.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EditAssessmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    static String type = null;
    static EditText dueDate;
    static EditText goalDate;
    static int rowId = -1;
    static int id;
    TermSchedulerDBHelper myDB;
    Intent myIntent;
    EditText assessmentTitle;

    CheckBox goalNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new TermSchedulerDBHelper(this);

        //create the drop down menu for status
        Spinner spType = (Spinner) findViewById(R.id.spinnerType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.assessmentTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        spType.setOnItemSelectedListener(this);

        dueDate = (EditText) findViewById(R.id.editDueDate);
        dueDate.setOnClickListener(this);

        goalDate = (EditText) findViewById(R.id.editGoalDate);
        goalDate.setOnClickListener(this);

        Button btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(this);

        assessmentTitle = (EditText) findViewById(R.id.editAssessment);

        myIntent = getIntent();
        if (myIntent.hasExtra("rowId")) {
            rowId = myIntent.getIntExtra("rowId", -1);
            assessmentTitle.setText(myIntent.getStringExtra("title"));
            dueDate.setText(myIntent.getStringExtra("dueDate"));
            goalDate.setText(myIntent.getStringExtra("goalDate"));

            type = myIntent.getStringExtra("type");
            int spPosition = adapter.getPosition(type);
            spType.setSelection(spPosition);
        }

        goalNotify = (CheckBox) findViewById(R.id.checkBoxGoalDate);
        goalNotify.setOnCheckedChangeListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //only add the delete option if it is an existing assessment to edit
        if (myIntent.hasExtra("rowId")) {
            //myIntent = getIntent();
            getMenuInflater().inflate(R.menu.menu_delete_item, menu);
        } else {
            super.onCreateOptionsMenu(menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteItem) {
            String rowId2 = Integer.toString(rowId);
            Boolean test = myDB.deleteAssessment(rowId2);

            if (test == false) {
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show();
            } else {
                //close
                rowId = -1;
                Toast.makeText(this, "Data added or edited successfully.", Toast.LENGTH_LONG).show();
                //finish();
                Intent intent = new Intent(this, AssessmentViewActivity.class);
                startActivity(intent);
            }

        } else if (item.getItemId() == android.R.id.home) {
            rowId = -1;
            Intent intent = new Intent(this, AssessmentViewActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        rowId = -1; //ensures the id is always reset
        //super.onBackPressed();
        Intent intent = new Intent(this, AssessmentViewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = (String) parent.getItemAtPosition(position);

        if (selected.equals(getString(R.string.assessment_OA))) {
            type = AssessmentEntry.TYPE_OA;
        } else if (selected.equals(getString(R.string.assessment_PA))) {
            type = AssessmentEntry.TYPE_PA;
        } else {
            type = null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        type = null;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerPopup();
        //newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v) {
        id = v.getId();

        switch(id){
            case R.id.editDueDate: {
                //Toast.makeText(this, "Start pressed", Toast.LENGTH_LONG).show();
                showDatePickerDialog(v);
                //this.startDate.setText(currentDate);
                break;
            }
            case R.id.editGoalDate: {
                showDatePickerDialog(v);
                //this.endDate.setText(currentDate);
                break;
            }
            case R.id.buttonSave: {

                try {
                    EditText tn = (EditText) findViewById(R.id.editAssessment);
                    String assessmentName = tn.getText().toString();
                    String due = dueDate.getText().toString();
                    String goal = goalDate.getText().toString();

                    Boolean test = false;
                    if (type != null) { //check that type is not null before trying to add/edit
                        if (rowId != -1) { //edit existing assessment
                            String rowId2 = Integer.toString(rowId);
                            test = myDB.editAssessment(rowId2, assessmentName, type, due, goal);
                        } else { //add a new assessment
                            test = myDB.addAssessment(assessmentName, type, due, goal);
                        }

                        if (test == false) {
                            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show();
                        } else {
                            //close
                            rowId = -1;
                            Toast.makeText(this, "Data added or edited successfully.", Toast.LENGTH_LONG).show();
                            //finish();
                            Intent intent = new Intent(this, AssessmentViewActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(this, "All fields required.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) { //catches null data
                    Toast.makeText(this, "All fields required.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {

            if (!assessmentTitle.getText().toString().isEmpty()) {
                String title = "Assessment Notification";
                String content = "Your assessment: " + assessmentTitle.getText() + " is due today!";

                try {

                    String dateString = goalDate.getText().toString();
                    //DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                    Date date = df.parse(dateString);
                    Date current = new Date();
                    //Date current2 = df.format(current);
                    long difference = current.getTime() - date.getTime();
                    long diffInMillies = TimeUnit.MILLISECONDS.convert(difference, TimeUnit.MILLISECONDS);
                    //long diffInMillies = 30000;
                    //Toast.makeText(this, current.getTime() + " " + difference + " " + date.getTime(), Toast.LENGTH_LONG).show();
                    //schedule the notification
                    scheduleNotification(createNotification(title, content), diffInMillies, 3); //1 for start, 2 for end, 3 for goal

                } catch (Exception e) {
                    Toast.makeText(this, "The goal date is either blank or invalid.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Assessment title cannot be blank.", Toast.LENGTH_LONG).show();
            }
        } else {
            //remove notification
            try {

                String dateString = goalDate.getText().toString();
                //DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                Date date = df.parse(dateString);
                cancelNotification(3); //1 for start, 2 for end, 3 for goal
            } catch (Exception e) {
                //will catch if the date is blank or invalid and do nothing
            }
        }

    }

    private void scheduleNotification(Notification notification, long delay, int dateType) {
        String notifyId;
        if (rowId == -1){
            notifyId = dateType + "9999";
        } else {
            notifyId = Integer.toString(dateType) + Integer.toString(rowId);
        }

        int notifyId2 = Integer.parseInt(notifyId);

        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notifyId2);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(notifyId, notifyId2);
        editor.apply();

        Toast.makeText(this, "Notification set.", Toast.LENGTH_LONG).show();
    }

    private  void cancelNotification(int dateType) {

        String notifyId;
        if (rowId == -1){
            notifyId = dateType + "9999";
        } else {
            notifyId = Integer.toString(dateType) + Integer.toString(rowId);
        }

        //int notifyId2 = Integer.parseInt(notifyId);

        try {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

            int notifyId2 = sharedPref.getInt(notifyId, -1);

            Intent myIntent = new Intent(this, MyNotificationPublisher.class);
            myIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notifyId2);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);

            Toast.makeText(this, "Notification cancelled.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Unable to find notification to cancel.", Toast.LENGTH_LONG).show();
        }
    }

    private Notification createNotification(String title, String content) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return  mBuilder.build();
    }

    public static class DatePickerPopup extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public static String currentDate;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);


            return new DatePickerDialog(getActivity(), R.style.MyCalendarTheme,this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //DateEdit.setText(day + "/" + (month + 1) + "/" + year);

            if ((month+1) < 10 && (month+1) != 10 && (month+1) != 11 && (month+1) != 12) {
                currentDate = "0" + (month + 1) + "-" + dayOfMonth + "-" + year;
            } else {
                currentDate = (month + 1) + "-" + dayOfMonth + "-" + year;
            }

            switch(id){
                case R.id.editDueDate: {
                    //Toast.makeText(this, "Start pressed", Toast.LENGTH_LONG).show();
                    //showDatePickerDialog(v);
                    dueDate.setText(currentDate);
                    break;
                }
                case R.id.editGoalDate: {
                    //showDatePickerDialog(v);
                    goalDate.setText(currentDate);
                    break;
                }
            }
        }
    }
}
