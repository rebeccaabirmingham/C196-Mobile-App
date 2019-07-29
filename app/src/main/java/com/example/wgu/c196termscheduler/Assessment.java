package com.example.wgu.c196termscheduler;

class Assessment {

    private int assessmentId;

    private Boolean selected;

    public Assessment(int eachId, boolean b) {
        this.assessmentId = eachId;

        this.selected = b;

    }


    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}
