package com.example.wgu.c196termscheduler;

class Mentor {

    private int mentorId;

    private Boolean selected;

    public Mentor(int eachId, boolean b) {
        this.mentorId = eachId;

        this.selected = b;

    }


    public int getMentorId() {
        return mentorId;
    }

    public void setMentorId(int mentorId) {
        this.mentorId = mentorId;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
