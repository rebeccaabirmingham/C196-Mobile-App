package com.example.wgu.c196termscheduler;

import android.provider.BaseColumns;

public final class TermSchedulerDBContract {

    private TermSchedulerDBContract() {}

    public static class TermEntry implements BaseColumns {

        public static final String TABLE_NAME = "terms";
        public static final String COL_NAME = "name";
        public static final String COL_START_DATE = "startDate";
        public static final String COL_END_DATE = "endDate";

    }

    public static class CourseEntry implements BaseColumns {

        public static final String TABLE_NAME = "courses";
        public static final String COL_NAME = "name";
        public static final String COL_START_DATE = "startDate";
        public static final String COL_END_DATE = "endDate";
        public static final String COL_STATUS = "status";
        public static final String COL_NOTES = "notes";

        //foreign key for terms table
        public static final String COL_FOR_KEY = "termID";

        //constants for status type
        public static final String STATUS_1 = "Not Started";
        public static final String STATUS_2 = "In Progress";
        public static final String STATUS_3 = "Completed";


    }

    public static class MentorEntry implements BaseColumns {

        public static final String TABLE_NAME = "mentors";
        public static final String COL_NAME = "name";
        public static final String COL_PHONE = "phoneNumber";
        public static final String COL_EMAIL = "email";

    }

    public  static class AssessmentEntry implements BaseColumns {

        public static final String TABLE_NAME = "assessments";
        public static final String COL_TYPE = "type";
        public static final String COL_NAME = "name";
        public static final String COL_DUE_DATE = "dueDate";
        public static final String COL_GOAL_DATE = "goalDate";

        //foreign key for courses table
        public static final String COL_FOR_KEY = "courseID";

        //constants for assessment type - PA or OA
        public static final String TYPE_PA = "PA";
        public static final String TYPE_OA = "OA";

    }

    public static class MentorCourse implements BaseColumns {
        //because mentors can be assigned to multiple courses...
        public static final String TABLE_NAME = "mentorCourseConnect";

        //foreign key for mentors table
        public static final String COL_FOR_KEY_MENTOR = "mentorID";
        //foreign key for courses table
        public static final String COL_FOR_KEY_COURSE = "courseID";
    }

}
