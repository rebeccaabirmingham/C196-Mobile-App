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

public class MentorViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    TermSchedulerDBHelper myDB;
    ListView lvMentors;
    ArrayList<String> arrMentors = new ArrayList<>();
    ArrayList<Integer> mentorIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new TermSchedulerDBHelper(this);
        lvMentors = (ListView) findViewById(R.id.listMentors);
        populateMentors();
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrMentors);
        lvMentors.setAdapter(adapter);
        lvMentors.setOnItemClickListener(this);
    }

    private void populateMentors() {

        Cursor results = myDB.getMentorsList();

        while (results.moveToNext()) {
            int eachId = results.getInt(0);
            String eachMentor = results.getString(1);
            arrMentors.add(eachMentor);
            mentorIds.add(eachId);
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
            Intent intent = new Intent(this, EditMentorActivity.class);
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
        int id2 = mentorIds.get(position);

        String rowId = Integer.toString(id2);
        Cursor results = myDB.getMentor(rowId);
        int rowId2 = -1;
        String title = null;
        String phone = null;
        String email = null;

        while (results.moveToNext()) {
            rowId2 = results.getInt(0);
            title = results.getString(1);
            phone = results.getString(2);
            email = results.getString(3);
        }

        Intent intent = new Intent(this, EditMentorActivity.class);
        intent.putExtra("rowId", rowId2);
        intent.putExtra("title", title);
        intent.putExtra("phone", phone);
        intent.putExtra("email", email);
        startActivity(intent);

    }
}
