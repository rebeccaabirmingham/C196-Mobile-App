package com.example.wgu.c196termscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditMentorActivity extends AppCompatActivity implements View.OnClickListener {

    static int id;
    TermSchedulerDBHelper myDB;
    EditText mentorTitle;
    EditText mentorPhone;
    EditText mentorEmail;
    static int rowId = -1;
    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new TermSchedulerDBHelper(this);

        Button btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(this);

        mentorTitle = (EditText) findViewById(R.id.editMentor);
        mentorPhone = (EditText) findViewById(R.id.editPhone);
        mentorEmail = (EditText) findViewById(R.id.editEmail);

        //if we're editing a mentor rather than adding
        myIntent = getIntent();
        if (myIntent.hasExtra("rowId")) {
            rowId = myIntent.getIntExtra("rowId", -1);
            mentorTitle.setText(myIntent.getStringExtra("title"));
            mentorPhone.setText(myIntent.getStringExtra("phone"));
            mentorEmail.setText(myIntent.getStringExtra("email"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //only add the delete option if it is an existing mentor to edit
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
            Boolean test = myDB.deleteMentor(rowId2);

            if (test == false) {
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show();
            } else {
                //close
                rowId = -1;
                Toast.makeText(this, "Data added or edited successfully.", Toast.LENGTH_LONG).show();
                //finish();
                Intent intent = new Intent(this, MentorViewActivity.class);
                startActivity(intent);
            }

        } else if (item.getItemId() == android.R.id.home) {
            rowId = -1;
            Intent intent = new Intent(this, MentorViewActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        rowId = -1; //ensures the id is always reset
        Intent intent = new Intent(this, MentorViewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        id = v.getId();

        switch(id){
            case R.id.buttonSave: {

                try {
                    String mentorName = mentorTitle.getText().toString();
                    String phone = mentorPhone.getText().toString();
                    String email = mentorEmail.getText().toString();

                    Boolean test = false;

                    if (rowId != -1) { //edit existing mentor
                        String rowId2 = Integer.toString(rowId);
                        test = myDB.editMentor(rowId2, mentorName, phone, email);
                    } else { //add a new mentor
                        test = myDB.addMentor(mentorName, phone, email);
                    }

                    if (test == false) {
                        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show();
                    } else {
                        //close
                        rowId = -1;
                        Toast.makeText(this, "Data added or edited successfully.", Toast.LENGTH_LONG).show();
                        //finish();
                        Intent intent = new Intent(this, MentorViewActivity.class);
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "All fields required.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }
}
