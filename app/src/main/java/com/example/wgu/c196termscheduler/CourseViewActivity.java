package com.example.wgu.c196termscheduler;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CourseViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    TermSchedulerDBHelper myDB;
    ListView lvCourse;
    ArrayList<String> arrCourse = new ArrayList<>();
    ArrayList<Integer> courseIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCourses);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new TermSchedulerDBHelper(this);
        lvCourse = (ListView) findViewById(R.id.listCourses);
        populateCourses();
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrCourse);
        lvCourse.setAdapter(adapter);
        lvCourse.setOnItemClickListener(this);
    }

    private void populateCourses() {

        Cursor results = myDB.getCoursesList();

        while (results.moveToNext()) {
            int eachId = results.getInt(0);
            String eachCourse = results.getString(1) + ": " + results.getString(2) + " "
                    + results.getString(3) + " - " + results.getString(4);
            arrCourse.add(eachCourse);
            courseIds.add(eachId);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.inputAdd) {
            Intent intent = new Intent(this, EditCourseActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String entry = parent.getItemAtPosition(position).toString();
        //Toast.makeText(this, "Row ID: " + id + " Position: " + position, Toast.LENGTH_LONG).show(); //starts at 0 :) sqlite starts at 1
        //Long id2 = id + 1;
        int id2 = courseIds.get(position);

        String rowId = Integer.toString(id2);
        Cursor results = myDB.getCourse(rowId);
        int rowId2 = -1;
        String title = null;
        String status = null;
        String startDate = null;
        String endDate = null;
        String notes = null;

        while (results.moveToNext()) {
            rowId2 = results.getInt(0);
            title = results.getString(1);
            status = results.getString(2);
            startDate = results.getString(3);
            endDate = results.getString(4);
            notes = results.getString(5);
        }

        Intent intent = new Intent(this, EditCourseActivity.class);
        intent.putExtra("rowId", rowId2);
        intent.putExtra("title", title);
        intent.putExtra("status", status);
        intent.putExtra("startDate", startDate);
        intent.putExtra("endDate", endDate);
        intent.putExtra("notes", notes);
        startActivity(intent);

    }
}
