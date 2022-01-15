package com.s2dioapps.divinepolytechniccollege.ui.lesson;

public class LessonModel {

    public LessonModel(String docID, String name, int onOfModules) {
        this.docID = docID;
        this.name = name;
        this.onOfModules = onOfModules;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOnOfModules() {
        return onOfModules;
    }

    public void setOnOfModules(int onOfModules) {
        this.onOfModules = onOfModules;
    }

    private String docID;
    private String name;
    private int onOfModules;

}
