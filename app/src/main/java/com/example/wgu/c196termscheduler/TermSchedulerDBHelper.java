package com.example.wgu.c196termscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wgu.c196termscheduler.TermSchedulerDBContract.*;

import java.util.ArrayList;

public class TermSchedulerDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "termScheduler.db";

    private static final int DB_VERSION = 1;

    //Constructs a new instance
    public TermSchedulerDBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

    }

    //SQL CREATE statements for each of the tables

    String SQL_CREATE_TERMS_TABLE = "CREATE TABLE " + TermEntry.TABLE_NAME
        + " (" + TermEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + TermEntry.COL_NAME + " TEXT NOT NULL, "
        + TermEntry.COL_START_DATE + " TEXT NOT NULL, "
        + TermEntry.COL_END_DATE + " TEXT NOT NULL);";

    String SQL_CREATE_COURSES_TABLE = "CREATE TABLE " + CourseEntry.TABLE_NAME
        + " (" + CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + CourseEntry.COL_NAME + " TEXT NOT NULL, "
        + CourseEntry.COL_START_DATE + " TEXT NOT NULL, "
        + CourseEntry.COL_END_DATE + " TEXT NOT NULL, "
        + CourseEntry.COL_STATUS + " TEXT NOT NULL, "
        + CourseEntry.COL_NOTES + " TEXT DEFAULT NULL, "
        + CourseEntry.COL_FOR_KEY + " INTEGER, "
            + "FOREIGN KEY(" + CourseEntry.COL_FOR_KEY + ") REFERENCES "
            + TermEntry.TABLE_NAME + "(" + TermEntry._ID + "));";

    String SQL_CREATE_MENTORS_TABLE = "CREATE TABLE " + MentorEntry.TABLE_NAME
        + " (" + MentorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + MentorEntry.COL_NAME + " TEXT NOT NULL, "
        + MentorEntry.COL_PHONE + " TEXT NOT NULL, "
        + MentorEntry.COL_EMAIL + " TEXT NOT NULL);";

    String SQL_CREATE_ASSESSMENTS_TABLE = "CREATE TABLE " + AssessmentEntry.TABLE_NAME
        + " (" + AssessmentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + AssessmentEntry.COL_TYPE + " TEXT NOT NULL, "
        + AssessmentEntry.COL_NAME + " TEXT NOT NULL, "
        + AssessmentEntry.COL_DUE_DATE + " TEXT NOT NULL, "
        + AssessmentEntry.COL_GOAL_DATE + " TEXT NOT NULL, "
        + AssessmentEntry.COL_FOR_KEY + " INTEGER, "
            + "FOREIGN KEY(" + AssessmentEntry.COL_FOR_KEY + ") REFERENCES "
            + CourseEntry.TABLE_NAME + "(" + CourseEntry._ID + "));";

    String SQL_CREATE_MENTORCOURSE_TABLE = "CREATE TABLE " + MentorCourse.TABLE_NAME
        + " (" + MentorCourse._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + MentorCourse.COL_FOR_KEY_MENTOR + " INTEGER, "
        + MentorCourse.COL_FOR_KEY_COURSE + " INTEGER, "
            + "FOREIGN KEY(" + MentorCourse.COL_FOR_KEY_MENTOR + ") REFERENCES "
            + MentorEntry.TABLE_NAME + "(" + MentorEntry._ID + "), "
            + "FOREIGN KEY(" + MentorCourse.COL_FOR_KEY_COURSE + ") REFERENCES "
            + CourseEntry.TABLE_NAME + "(" + CourseEntry._ID + "));";

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create the tables

        db.execSQL(SQL_CREATE_TERMS_TABLE);
        db.execSQL(SQL_CREATE_COURSES_TABLE);
        db.execSQL(SQL_CREATE_MENTORS_TABLE);
        db.execSQL(SQL_CREATE_ASSESSMENTS_TABLE);
        db.execSQL(SQL_CREATE_MENTORCOURSE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //delete the tables

        String SQL_DELETE_TERMS_TABLE = "DROP TABLE IF EXISTS " + TermEntry.TABLE_NAME;
        String SQL_DELETE_COURSES_TABLE = "DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME;
        String SQL_DELETE_MENTORS_TABLE = "DROP TABLE IF EXISTS " + MentorEntry.TABLE_NAME;
        String SQL_DELETE_ASSESSMENTS_TABLE = "DROP TABLE IF EXISTS " + AssessmentEntry.TABLE_NAME;
        String SQL_DELETE_MENTORCOURSE_TABLE = "DROP TABLE IF EXISTS " + MentorCourse.TABLE_NAME;

        db.execSQL(SQL_DELETE_TERMS_TABLE);
        db.execSQL(SQL_DELETE_COURSES_TABLE);
        db.execSQL(SQL_DELETE_MENTORS_TABLE);
        db.execSQL(SQL_DELETE_ASSESSMENTS_TABLE);
        db.execSQL(SQL_DELETE_MENTORCOURSE_TABLE);

        //recreate the tables

        onCreate(db);


    }
    //TERMS
    public long addTerm(String name, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TermEntry.COL_NAME, name);
        cv.put(TermEntry.COL_START_DATE, startDate);
        cv.put(TermEntry.COL_END_DATE, endDate);

        long rowID = db.insert(TermEntry.TABLE_NAME, null, cv);

        return rowID;
        /*if (rowID == -1) {
            return false;
        } else {
            return true;
        }*/
    }

    public boolean editTerm(String rowId, String name, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TermEntry.COL_NAME, name);
        cv.put(TermEntry.COL_START_DATE, startDate);
        cv.put(TermEntry.COL_END_DATE, endDate);

        String where = TermEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated = db.update(TermEntry.TABLE_NAME, cv, where, whereEquals);

        if (rowsUpdated == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteTerm(String rowId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where = TermEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated = db.delete(TermEntry.TABLE_NAME, where, whereEquals);

        if (rowsUpdated == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getTermsList() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] data = {TermEntry._ID, TermEntry.COL_NAME, TermEntry.COL_START_DATE, TermEntry.COL_END_DATE};

        Cursor results = db.query(TermEntry.TABLE_NAME, data, null, null, null, null, null);

        return results;

    }

    public Cursor getTerm(String rowId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] data = {TermEntry._ID, TermEntry.COL_NAME, TermEntry.COL_START_DATE, TermEntry.COL_END_DATE};

        String where = TermEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        Cursor results = db.query(TermEntry.TABLE_NAME, data, where, whereEquals, null, null, null);

        return results;

    }

    //COURSES
    public long addCourse(String name, String status, String startDate, String endDate, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CourseEntry.COL_NAME, name);
        cv.put(CourseEntry.COL_STATUS, status);
        cv.put(CourseEntry.COL_START_DATE, startDate);
        cv.put(CourseEntry.COL_END_DATE, endDate);
        cv.put(CourseEntry.COL_NOTES, notes);

        long rowID = db.insert(CourseEntry.TABLE_NAME, null, cv);

        return rowID;
        /*if (rowID == -1) {
            return false;
        } else {
            return true;
        }*/
    }

    public boolean editCourse(String rowId, String name, String status, String startDate, String endDate, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CourseEntry.COL_NAME, name);
        cv.put(CourseEntry.COL_STATUS, status);
        cv.put(CourseEntry.COL_START_DATE, startDate);
        cv.put(CourseEntry.COL_END_DATE, endDate);
        cv.put(CourseEntry.COL_NOTES, notes);

        String where = CourseEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated = db.update(CourseEntry.TABLE_NAME, cv, where, whereEquals);

        if (rowsUpdated == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean editCourseForKey(String rowId, ArrayList<Integer> courseArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        //first reset the termId to null if it is currently set to this term
        String reset = null;

        ContentValues cv = new ContentValues();
        cv.put(CourseEntry.COL_FOR_KEY, reset);

        String where = CourseEntry.COL_FOR_KEY + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated2 = db.update(CourseEntry.TABLE_NAME, cv, where, whereEquals);
        int rowsUpdated = 0;
        //then set any selected courses to that term
        if (!courseArray.isEmpty()) {
            for (Integer i : courseArray) {
                String courseId = i.toString();
                ContentValues cv2 = new ContentValues();
                cv2.put(CourseEntry.COL_FOR_KEY, rowId);

                String where2 = CourseEntry._ID + " = ?";
                String[] whereEquals2 = {courseId};

                rowsUpdated = db.update(CourseEntry.TABLE_NAME, cv2, where2, whereEquals2);
                }
            } else {
                rowsUpdated = 1; //nothing gets updated, but so that it returns true
            }

        if (rowsUpdated == 0 || rowsUpdated2 == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteCourse(String rowId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where = CourseEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated = db.delete(CourseEntry.TABLE_NAME, where, whereEquals);

        if (rowsUpdated == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getCoursesList() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] data = {CourseEntry._ID, CourseEntry.COL_NAME, CourseEntry.COL_STATUS, CourseEntry.COL_START_DATE, CourseEntry.COL_END_DATE, CourseEntry.COL_FOR_KEY};

        Cursor results = db.query(CourseEntry.TABLE_NAME, data, null, null, null, null, null);

        return results;

    }

    public Cursor getCourse(String rowId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] data = {CourseEntry._ID, CourseEntry.COL_NAME, CourseEntry.COL_STATUS, CourseEntry.COL_START_DATE, CourseEntry.COL_END_DATE, CourseEntry.COL_NOTES};

        String where = CourseEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        Cursor results = db.query(CourseEntry.TABLE_NAME, data, where, whereEquals, null, null, null);

        return results;

    }

    //ASSESSMENTS
    public boolean addAssessment(String name, String type, String dueDate, String goalDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(AssessmentEntry.COL_NAME, name);
        cv.put(AssessmentEntry.COL_TYPE, type);
        cv.put(AssessmentEntry.COL_DUE_DATE, dueDate);
        cv.put(AssessmentEntry.COL_GOAL_DATE, goalDate);

        long rowID = db.insert(AssessmentEntry.TABLE_NAME, null, cv);

        if (rowID == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean editAssessment(String rowId, String name, String type, String dueDate, String goalDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(AssessmentEntry.COL_NAME, name);
        cv.put(AssessmentEntry.COL_TYPE, type);
        cv.put(AssessmentEntry.COL_DUE_DATE, dueDate);
        cv.put(AssessmentEntry.COL_GOAL_DATE, goalDate);

        String where = AssessmentEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated = db.update(AssessmentEntry.TABLE_NAME, cv, where, whereEquals);

        if (rowsUpdated == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteAssessment(String rowId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where = AssessmentEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated = db.delete(AssessmentEntry.TABLE_NAME, where, whereEquals);

        if (rowsUpdated == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean editAssessmentForKey(String rowId, ArrayList<Integer> assessmentArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        //first reset the termId to null if it is currently set to this term
        String reset = null;

        ContentValues cv = new ContentValues();
        cv.put(AssessmentEntry.COL_FOR_KEY, reset);

        String where = AssessmentEntry.COL_FOR_KEY + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated2 = db.update(AssessmentEntry.TABLE_NAME, cv, where, whereEquals);
        int rowsUpdated = 0;
        //then set any selected assessments to that course
        if (!assessmentArray.isEmpty()) {
            for (Integer i : assessmentArray) {
                String aId = i.toString();
                ContentValues cv2 = new ContentValues();
                cv2.put(AssessmentEntry.COL_FOR_KEY, rowId);

                String where2 = AssessmentEntry._ID + " = ?";
                String[] whereEquals2 = {aId};

                rowsUpdated = db.update(AssessmentEntry.TABLE_NAME, cv2, where2, whereEquals2);
            }
        } else {
            rowsUpdated = 1; //nothing gets updated, but so that it returns true
        }

        if (rowsUpdated == 0 || rowsUpdated2 == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAssessmentList() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] data = {AssessmentEntry._ID, AssessmentEntry.COL_NAME, AssessmentEntry.COL_TYPE, AssessmentEntry.COL_DUE_DATE, AssessmentEntry.COL_FOR_KEY};

        Cursor results = db.query(AssessmentEntry.TABLE_NAME, data, null, null, null, null, null);

        return results;

    }

    public Cursor getAssessment(String rowId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] data = {AssessmentEntry._ID, AssessmentEntry.COL_NAME, AssessmentEntry.COL_TYPE, AssessmentEntry.COL_DUE_DATE, AssessmentEntry.COL_GOAL_DATE};

        String where = AssessmentEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        Cursor results = db.query(AssessmentEntry.TABLE_NAME, data, where, whereEquals, null, null, null);

        return results;

    }

    //MENTORS
    public boolean addMentor(String name, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(MentorEntry.COL_NAME, name);
        cv.put(MentorEntry.COL_PHONE, phone);
        cv.put(MentorEntry.COL_EMAIL, email);

        long rowID = db.insert(MentorEntry.TABLE_NAME, null, cv);

        if (rowID == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean editMentor(String rowId, String name, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(MentorEntry.COL_NAME, name);
        cv.put(MentorEntry.COL_PHONE, phone);
        cv.put(MentorEntry.COL_EMAIL, email);

        String where = MentorEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated = db.update(MentorEntry.TABLE_NAME, cv, where, whereEquals);

        if (rowsUpdated == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteMentor(String rowId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where = MentorEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated = db.delete(MentorEntry.TABLE_NAME, where, whereEquals);

        if (rowsUpdated == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getMentorsList() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] data = {MentorEntry._ID, MentorEntry.COL_NAME};

        Cursor results = db.query(MentorEntry.TABLE_NAME, data, null, null, null, null, null);

        return results;

    }

    public Cursor getMentor(String rowId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] data = {MentorEntry._ID, MentorEntry.COL_NAME, MentorEntry.COL_PHONE, MentorEntry.COL_EMAIL};

        String where = MentorEntry._ID + " = ?";
        String[] whereEquals = {rowId};

        Cursor results = db.query(MentorEntry.TABLE_NAME, data, where, whereEquals, null, null, null);

        return results;

    }

    public Cursor getCourseMentor(String rowId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] data = {MentorCourse._ID, MentorCourse.COL_FOR_KEY_MENTOR, MentorCourse.COL_FOR_KEY_COURSE};

        String where = MentorCourse.COL_FOR_KEY_COURSE + " = ?";
        String[] whereEquals = {rowId};

        Cursor results = db.query(MentorCourse.TABLE_NAME, data, where, whereEquals, null, null, null);

        return results;

    }

    public boolean updateMentorCourse(String rowId, ArrayList<Integer> mentorArray) {
        SQLiteDatabase db = this.getWritableDatabase();

        //first delete all rows for this course
        String where = MentorCourse.COL_FOR_KEY_COURSE + " = ?";
        String[] whereEquals = {rowId};

        int rowsUpdated = db.delete(MentorCourse.TABLE_NAME, where, whereEquals);


        //then update with new values if the array is not empty
        int rowsUpdated2 = -1;
        if (!mentorArray.isEmpty()) {
            for (Integer i : mentorArray) {
                String eachMentor = Integer.toString(i);
                ContentValues cv = new ContentValues();
                cv.put(MentorCourse.COL_FOR_KEY_MENTOR, eachMentor);
                cv.put(MentorCourse.COL_FOR_KEY_COURSE, rowId);

                long rowsUpdated3 = db.insert(MentorCourse.TABLE_NAME, null, cv);
            }
        } else {
            rowsUpdated2 = 1;
        }

        if (rowsUpdated == -1) {
            return false;
        } else {
            return true;
        }
    }
}
