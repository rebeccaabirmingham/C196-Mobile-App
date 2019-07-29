package com.example.wgu.c196termscheduler;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class EditTermActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    static EditText startDate;
    static EditText endDate;
    static int id;
    TermSchedulerDBHelper myDB;
    EditText termTitle;
    static int rowId = -1;
    Intent myIntent;

    ListView lvCourse;
    ArrayList<String> arrCourse = new ArrayList<>();
    ArrayList<Course> courseIds = new ArrayList<>();
    ArrayList<Integer> courseIds2 = new ArrayList<>();

    Boolean hasAssignedCourses = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new TermSchedulerDBHelper(this);

        startDate = (EditText) findViewById(R.id.editStartDate);
        startDate.setOnClickListener(this);

        endDate = (EditText) findViewById(R.id.editEndDate);
        endDate.setOnClickListener(this);

        Button btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(this);

        termTitle = (EditText) findViewById(R.id.editTitle);

        //if we're editing a term rather than adding
        myIntent = getIntent();
        if (myIntent.hasExtra("rowId")) {
            rowId = myIntent.getIntExtra("rowId", -1);
            termTitle.setText(myIntent.getStringExtra("title"));
            startDate.setText(myIntent.getStringExtra("startDate"));
            endDate.setText(myIntent.getStringExtra("endDate"));
        }

        lvCourse = (ListView) findViewById(R.id.listCourses);
        populateCourses();
        ListAdapter adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, arrCourse);
        lvCourse.setAdapter(adapter2);
        lvCourse.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ExpandListViews.listViewHeight(lvCourse);
        lvCourse.setOnItemClickListener(this);

        for (int i = 0; i < adapter2.getCount(); i++) {
            Boolean selected = courseIds.get(i).getSelected();
            if (selected == true) {
                lvCourse.setItemChecked(i, true);
                hasAssignedCourses = true;
            }
        }

    }

    private void populateCourses() {

        Cursor results = myDB.getCoursesList();

        while (results.moveToNext()) {
            int eachId = results.getInt(0);
            String termId = results.getString(5);
            //int termId2 = Integer.parseInt(termId);
            if (termId == null) {
                String eachCourse = results.getString(1) + ": " + results.getString(2) + " "
                        + results.getString(3) + " - " + results.getString(4);
                arrCourse.add(eachCourse);
                courseIds.add(new Course(eachId, false));
            } else if ((Integer.parseInt(termId)) == rowId) {
                String eachCourse = results.getString(1) + ": " + results.getString(2) + " "
                        + results.getString(3) + " - " + results.getString(4);
                arrCourse.add(eachCourse);
                courseIds.add(new Course(eachId, true));
            }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteItem) {

            if (!hasAssignedCourses) {
                String rowId2 = Integer.toString(rowId);
                Boolean test = myDB.deleteTerm(rowId2);

                if (test == false) {
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show();
                } else {
                    //close
                    rowId = -1;
                    Toast.makeText(this, "Data added or edited successfully.", Toast.LENGTH_LONG).show();
                    //finish();
                    Intent intent = new Intent(this, TermViewActivity.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Please remove all courses assigned to this term first (uncheck all and click the save button).", Toast.LENGTH_LONG).show();
            }

        } else if (item.getItemId() == android.R.id.home) {
            rowId = -1;
            Intent intent = new Intent(this, TermViewActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerPopup();
        //newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onBackPressed() {
        rowId = -1; //ensures the id is always reset
        Intent intent = new Intent(this, TermViewActivity.class);
        startActivity(intent);
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
            case R.id.buttonSave: {

                try {
                    EditText tn = (EditText) findViewById(R.id.editTitle);
                    String termName = tn.getText().toString();
                    String start = startDate.getText().toString();
                    String end = endDate.getText().toString();

                    Boolean test = false;
                    long rowReturned = -1;

                    for (Course c : courseIds) {
                        int index = courseIds.indexOf(c);
                        Boolean selected = courseIds.get(index).getSelected();
                        if (selected == true) {
                            int index2 = courseIds.get(index).getCourseId();
                            courseIds2.add(index2);
                        }
                    }

                    if (rowId != -1) { //edit existing term
                        String rowId2 = Integer.toString(rowId);
                        test = myDB.editTerm(rowId2, termName, start, end);
                        if (test == false) {
                            break;
                        }
                        test = myDB.editCourseForKey(rowId2, courseIds2);
                    } else { //add a new term
                        rowReturned = myDB.addTerm(termName, start, end);
                        if (rowReturned == -1) {
                            test = false;
                            break;
                        }
                        String rowReturned2 = Long.toString(rowReturned);
                        test = myDB.editCourseForKey(rowReturned2, courseIds2);
                    }

                    if (test == false) {
                        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show();
                    } else {
                        //close
                        rowId = -1;
                        Toast.makeText(this, "Data added or edited successfully.", Toast.LENGTH_LONG).show();
                        //finish();
                        Intent intent = new Intent(this, TermViewActivity.class);
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "All fields required.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Boolean selected = courseIds.get(position).getSelected();
        if (selected == true) {
            lvCourse.setItemChecked(position, false);
            courseIds.get(position).setSelected(false);
        } else {
            lvCourse.setItemChecked(position, true);
            courseIds.get(position).setSelected(true);
        }

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
