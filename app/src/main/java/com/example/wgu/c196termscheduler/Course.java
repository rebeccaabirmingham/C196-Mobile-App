package com.example.wgu.c196termscheduler;

public class Course {

    private int courseId;

    private Boolean selected;

    public Course(int eachId, boolean b) {
        this.courseId = eachId;

        this.selected = b;

    }


    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }


}
