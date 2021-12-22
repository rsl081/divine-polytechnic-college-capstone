package com.s2dioapps.divinepolytechniccollege.ui.category;

public class CategoryModel {

    private String docID;
    private String name;
    private int onOfTests;

    public CategoryModel(String docID, String name, int onOfTests) {
        this.docID = docID;
        this.name = name;
        this.onOfTests = onOfTests;
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

    public int getOnOfTests() {
        return onOfTests;
    }

    public void setOnOfTests(int onOfTests) {
        this.onOfTests = onOfTests;
    }
}
