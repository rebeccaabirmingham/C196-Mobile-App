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
import android.database.Cursor;
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
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wgu.c196termscheduler.TermSchedulerDBContract.CourseEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EditCourseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    static String status = null;
    static EditText startDate;
    static EditText endDate;
    static int rowId = -1;
    static int id;
    TermSchedulerDBHelper myDB;
    Intent myIntent;
    EditText courseTitle;
    EditText courseNotes;

    ListView lvAssessment;
    ArrayList<String> arrAssessment = new ArrayList<>();
    ArrayList<Assessment> assessmentIds = new ArrayList<>();
    ArrayList<Integer> assessmentIds2 = new ArrayList<>();

    Boolean hasAssignedAssessments = false;
    Boolean hasAssignedMentors = false;

    ListView lvMentors;
    ArrayList<String> arrMentors = new ArrayList<>();
    ArrayList<Mentor> mentorIds = new ArrayList<>();
    ArrayList<Integer> mentorIds2 = new ArrayList<>();

    CheckBox startNotify;
    CheckBox endNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new TermSchedulerDBHelper(this);

        //create the drop down menu for status
        Spinner spStatus = (Spinner) findViewById(R.id.spinnerStatus);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.courseStatus, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(adapter);
        spStatus.setOnItemSelectedListener(this);

        startDate = (EditText) findViewById(R.id.editStartDate);
        startDate.setOnClickListener(this);

        endDate = (EditText) findViewById(R.id.editEndDate);
        endDate.setOnClickListener(this);

        Button btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(this);

        courseTitle = (EditText) findViewById(R.id.editCourse);
        courseNotes = (EditText) findViewById(R.id.editCourseNotes);

        myIntent = getIntent();
        if (myIntent.hasExtra("rowId")) {
            rowId = myIntent.getIntExtra("rowId", -1);
            courseTitle.setText(myIntent.getStringExtra("title"));
            startDate.setText(myIntent.getStringExtra("startDate"));
            endDate.setText(myIntent.getStringExtra("endDate"));
            courseNotes.setText(myIntent.getStringExtra("notes"));

            status = myIntent.getStringExtra("status");
            int spPosition = adapter.getPosition(status);
            spStatus.setSelection(spPosition);
        }

        ImageButton btnShare = (ImageButton) findViewById(R.id.imageShare);
        btnShare.setOnClickListener(this);

        lvAssessment = (ListView) findViewById(R.id.listAssessments);
        populateAssessments();
        ListAdapter adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, arrAssessment);
        lvAssessment.setAdapter(adapter2);
        lvAssessment.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ExpandListViews.listViewHeight(lvAssessment);
        lvAssessment.setOnItemClickListener(this);

        for (int i = 0; i < adapter2.getCount(); i++) {
            Boolean selected = assessmentIds.get(i).getSelected();
            if (selected == true) {
                lvAssessment.setItemChecked(i, true);
                hasAssignedAssessments = true;
            }
        }

        lvMentors = (ListView) findViewById(R.id.listMentors);
        populateMentors();
        ListAdapter adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, arrMentors);
        lvMentors.setAdapter(adapter3);
        lvMentors.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ExpandListViews.listViewHeight(lvMentors);
        lvMentors.setOnItemClickListener(this);

        if (rowId != -1) {
            String rowId2 = Integer.toString(rowId);
            Cursor results = myDB.getCourseMentor(rowId2);
            while (results.moveToNext()) {
                int mentorId = results.getInt(1);
                for (Mentor m : mentorIds) {
                    if (m.getMentorId() == mentorId) {
                        int i = mentorIds.indexOf(m);
                        m.setSelected(true);
                        lvMentors.setItemChecked(i, true);
                        hasAssignedMentors = true;
                    }
                }
            }
        }

        startNotify = (CheckBox) findViewById(R.id.checkBoxStartDate);
        startNotify.setOnCheckedChangeListener(this);
        endNotify = (CheckBox) findViewById(R.id.checkBoxEndDate);
        endNotify.setOnCheckedChangeListener(this);
    }

    private void populateAssessments() {

        Cursor results = myDB.getAssessmentList();

        while (results.moveToNext()) {
            int eachId = results.getInt(0);
            String courseId = results.getString(4);
            if (courseId == null) {
                String eachAssessment = results.getString(1) + ": " + results.getString(2) + " due: "
                        + results.getString(3);
                arrAssessment.add(eachAssessment);
                assessmentIds.add(new Assessment(eachId, false));
            } else if ((Integer.parseInt(courseId)) == rowId) {
                String eachAssessment = results.getString(1) + ": " + results.getString(2) + " due: "
                        + results.getString(3);
                arrAssessment.add(eachAssessment);
                assessmentIds.add(new Assessment(eachId, true));
            }
        }

    }

    private void populateMentors() {

        Cursor results = myDB.getMentorsList();

        while (results.moveToNext()) {
            int eachId = results.getInt(0);

            String eachMentor = results.getString(1);
            arrMentors.add(eachMentor);
            mentorIds.add(new Mentor(eachId, false));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //only add the delete option if it is an existing term to edit
        if (myIntent.hasExtra("rowId")) {
            //myIntent = getIntent();
            getMenuInflater().inflate(R.menu.menu_delete_item, menu);
        } else {
            super.onCreateOptionsMenu(menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.deleteItem) {
            if (!hasAssignedAssessments) {
                String rowId2 = Integer.toString(rowId);
                Boolean test = myDB.deleteCourse(rowId2);

                if (test == false) {
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show();
                } else {
                    //close
                    rowId = -1;
                    Toast.makeText(this, "Data added or edited successfully.", Toast.LENGTH_LONG).show();
                    //finish();
                    Intent intent = new Intent(this, CourseViewActivity.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Please remove all assessments assigned to this course first (uncheck all and click the save button).", Toast.LENGTH_LONG).show();
            }
        } else if (item.getItemId() == android.R.id.home) {
            rowId = -1;
            Intent intent = new Intent(this, CourseViewActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        rowId = -1; //ensures the id is always reset
        //super.onBackPressed();
        Intent intent = new Intent(this, CourseViewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String selected = (String) parent.getItemAtPosition(position);

        if (selected.equals(getString(R.string.course_notStarted))) {
            status = CourseEntry.STATUS_1;
        } else if (selected.equals(getString(R.string.course_inProgress))) {
            status = CourseEntry.STATUS_2;
        } else if (selected.equals(getString(R.string.course_completed))) {
            status = CourseEntry.STATUS_3;
        } else {
            status = null;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        status = null;
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
            case R.id.editStartDate: {
                //Toast.makeText(this, "Start pressed", Toast.LENGTH_LONG).show();
                showDatePickerDialog(v);
                //this.startDate.setText(currentDate);
                break;
            }
            case R.id.editEndDate: {
                showDatePickerDialog(v);
                //this.endDate.setText(currentDate);
                break;
            }
            case R.id.imageShare: {
                try {

                    EditText cn2 = (EditText) findViewById(R.id.editCourseNotes);
                    String courseNotes = cn2.getText().toString();

                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_TEXT, courseNotes);
                    share.setType("text/plain");
                    startActivity(share);

                } catch (Exception e) {
                    Toast.makeText(this, "Notes cannot be empty.", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.buttonSave: {

                try {
                    EditText cn = (EditText) findViewById(R.id.editCourse);
                    String courseName = cn.getText().toString();
                    EditText cn2 = (EditText) findViewById(R.id.editCourseNotes);
                    String courseNotes = cn2.getText().toString();

                    String start = startDate.getText().toString();
                    String end = endDate.getText().toString();

                    for (Assessment a : assessmentIds) {
                        int index = assessmentIds.indexOf(a);
                        Boolean selected = assessmentIds.get(index).getSelected();
                        if (selected == true) {
                            int index2 = assessmentIds.get(index).getAssessmentId();
                            assessmentIds2.add(index2);
                        }
                    }

                    for (Mentor m : mentorIds) {
                        int index = mentorIds.indexOf(m);
                        Boolean selected = mentorIds.get(index).getSelected();
                        if (selected == true) {
                            int index2 = mentorIds.get(index).getMentorId();
                            mentorIds2.add(index2);
                        }
                    }

                    Boolean test = false;
                    if (status != null) {
                        if (rowId != -1) { //edit existing course
                            String rowId2 = Integer.toString(rowId);
                            test = myDB.editCourse(rowId2, courseName, status, start, end, courseNotes);
                            if (test == false) {
                                break;
                            }
                            test = myDB.editAssessmentForKey(rowId2, assessmentIds2);
                            if (test == false) {
                                break;
                            }
                            test = myDB.updateMentorCourse(rowId2, mentorIds2);

                        } else { //add a new course
                            long test2 = myDB.addCourse(courseName, status, start, end, courseNotes);
                            if (test2 == -1) {
                                test = false;
                                break;
                            }
                            String rowReturned2 = Long.toString(test2);
                            test = myDB.editAssessmentForKey(rowReturned2, assessmentIds2);
                            if (test == false) {
                                break;
                            }
                            test = myDB.updateMentorCourse(rowReturned2, mentorIds2);

                        }

                        if (test == false) {
                            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show();
                        } else {
                            //close
                            rowId = -1;
                            Toast.makeText(this, "Data added or edited successfully.", Toast.LENGTH_LONG).show();
                            //finish();
                            Intent intent = new Intent(this, CourseViewActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(this, "All fields required.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e){
                    Toast.makeText(this, "All fields required.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //int viewId = parent.getId();

        //Toast.makeText(this, viewId + " " + view.getId() + " " + parent.getId() + " " + R.id.listAssessments, Toast.LENGTH_LONG).show();

        if (parent.getId() == R.id.listAssessments) {
            Boolean selected = assessmentIds.get(position).getSelected();
            if (selected == true) {
                lvAssessment.setItemChecked(position, false);
                assessmentIds.get(position).setSelected(false);
            } else {
                lvAssessment.setItemChecked(position, true);
                assessmentIds.get(position).setSelected(true);
            }
        } else if (parent.getId() == R.id.listMentors) {
            Boolean selected = mentorIds.get(position).getSelected();
            if (selected == true) {
                lvMentors.setItemChecked(position, false);
                mentorIds.get(position).setSelected(false);
            } else {
                lvMentors.setItemChecked(position, true);
                mentorIds.get(position).setSelected(true);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //buttonView.getId();

        //Toast.makeText(this, buttonView.toString() + " " + buttonView.getId() + " " + R.id.checkBoxStartDate, Toast.LENGTH_LONG).show();
        if (buttonView.getId() == R.id.checkBoxStartDate) {
            if (isChecked) {

                if (!courseTitle.getText().toString().isEmpty()) {
                    String title = "Start Date Notification";
                    String content = "Your class: " + courseTitle.getText() + " starts today!";

                    try {

                        String dateString = startDate.getText().toString();
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
                        scheduleNotification(createNotification(title, content), diffInMillies, 1); //1 for start, 2 for end

                    } catch (Exception e) {
                        Toast.makeText(this, "The start date is either blank or invalid.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Course title cannot be blank.", Toast.LENGTH_LONG).show();
                }
            } else {
                //remove notification
                try {

                    String dateString = startDate.getText().toString();
                    //DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                    Date date = df.parse(dateString);
                    cancelNotification(1); //1 for start, 2 for end, 3 for goal
                } catch (Exception e) {
                    //will catch if the date is blank or invalid and do nothing
                }
            }
        } else if (buttonView.getId() == R.id.checkBoxEndDate) {
            if (isChecked) {
                if (!courseTitle.getText().toString().isEmpty()) {
                    String title = "End Date Notification";
                    String content = "Your class: " + courseTitle.getText() + " ends today!";

                    try {

                        String dateString = endDate.getText().toString();
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
                        scheduleNotification(createNotification(title, content), diffInMillies, 2); //1 for start, 2 for end

                    } catch (Exception e) {
                        Toast.makeText(this, "The end date is either blank or invalid.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Course title cannot be blank.", Toast.LENGTH_LONG).show();
                }
            } else {
                //remove notification
                try {

                    String dateString = endDate.getText().toString();
                    //DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                    Date date = df.parse(dateString);
                    cancelNotification(2); //1 for start, 2 for end, 3 for goal
                } catch (Exception e) {
                    //will catch if the date is blank or invalid and do nothing
                }
            }
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


            return new DatePickerDialog(getActivity(), R.style.MyCalendarTheme, this, year, month, day);
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
                case R.id.editStartDate: {
                    //Toast.makeText(this, "Start pressed", Toast.LENGTH_LONG).show();
                    //showDatePickerDialog(v);
                    startDate.setText(currentDate);
                    break;
                }
                case R.id.editEndDate: {
                    //showDatePickerDialog(v);
                    endDate.setText(currentDate);
                    break;
                }
            }
        }
    }
}
