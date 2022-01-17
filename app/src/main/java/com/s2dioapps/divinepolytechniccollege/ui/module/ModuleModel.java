package com.s2dioapps.divinepolytechniccollege.ui.module;

public class ModuleModel {

    public ModuleModel(String moduleID, String modulePDF, int count) {
        this.moduleID = moduleID;
        this.modulePDF = modulePDF;
        this.count = count;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }



    public String getModulePDF() {
        return modulePDF;
    }

    public void setModulePDF(String modulePDF) {
        this.modulePDF = modulePDF;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private String moduleID;
    private String modulePDF;
    private int count;
}
