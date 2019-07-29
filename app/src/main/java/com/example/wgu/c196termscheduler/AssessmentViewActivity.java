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

public class AssessmentViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    TermSchedulerDBHelper myDB;
    ListView lvAssessment;
    ArrayList<String> arrAssessment = new ArrayList<>();
    ArrayList<Integer> assessmentIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new TermSchedulerDBHelper(this);
        lvAssessment = (ListView) findViewById(R.id.listAssessments);
        populateAssessments();
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrAssessment);
        lvAssessment.setAdapter(adapter);
        lvAssessment.setOnItemClickListener(this);
    }

    private void populateAssessments() {

        Cursor results = myDB.getAssessmentList();

        while (results.moveToNext()) {
            int eachId = results.getInt(0);
            String eachAssessment = results.getString(1) + ": " + results.getString(2) + " due: "
                    + results.getString(3);
            arrAssessment.add(eachAssessment);
            assessmentIds.add(eachId);
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
            Intent intent = new Intent(this, EditAssessmentActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == android.R.id.home) {
            //super.onBackPressed();
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
        int id2 = assessmentIds.get(position);

        String rowId = Integer.toString(id2);
        Cursor results = myDB.getAssessment(rowId);
        int rowId2 = -1;
        String title = null;
        String type = null;
        String dueDate = null;
        String goalDate = null;

        while (results.moveToNext()) {
            rowId2 = results.getInt(0);
            title = results.getString(1);
            type = results.getString(2);
            dueDate = results.getString(3);
            goalDate = results.getString(4);
        }

        Intent intent = new Intent(this, EditAssessmentActivity.class);
        intent.putExtra("rowId", rowId2);
        intent.putExtra("title", title);
        intent.putExtra("type", type);
        intent.putExtra("dueDate", dueDate);
        intent.putExtra("goalDate", goalDate);
        startActivity(intent);

    }
}
