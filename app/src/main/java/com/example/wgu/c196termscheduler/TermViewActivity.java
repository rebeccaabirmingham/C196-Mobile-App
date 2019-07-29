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

public class TermViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    TermSchedulerDBHelper myDB;
    ListView lvTerms;
    ArrayList<String> arrTerms = new ArrayList<>();
    ArrayList<Integer> termIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTerms);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new TermSchedulerDBHelper(this);
        lvTerms = (ListView) findViewById(R.id.listTerms);
        populateTerms();
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrTerms);
        lvTerms.setAdapter(adapter);
        lvTerms.setOnItemClickListener(this);
    }

    private void populateTerms() {

        Cursor results = myDB.getTermsList();

        while (results.moveToNext()) {
            int eachId = results.getInt(0);
            String eachTerm = results.getString(1) + ": " + results.getString(2) + " - " + results.getString(3);
            arrTerms.add(eachTerm);
            termIds.add(eachId);
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
            Intent intent = new Intent(this, EditTermActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == android.R.id.home) {
            //super.onBackPressed(); //doesn't always seem to do what i want....
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
        int id2 = termIds.get(position);

        String rowId = Integer.toString(id2);
        Cursor results = myDB.getTerm(rowId);
        int rowId2 = -1;
        String title = null;
        String startDate = null;
        String endDate = null;

        while (results.moveToNext()) {
            rowId2 = results.getInt(0);
            title = results.getString(1);
            startDate = results.getString(2);
            endDate = results.getString(3);
        }

        Intent intent = new Intent(this, EditTermActivity.class);
        intent.putExtra("rowId", rowId2);
        intent.putExtra("title", title);
        intent.putExtra("startDate", startDate);
        intent.putExtra("endDate", endDate);
        startActivity(intent);

    }
}
