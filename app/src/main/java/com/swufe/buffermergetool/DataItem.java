package com.swufe.buffermergetool;

public class DataItem {

    private int id;
    private String curName;
    private String curData;

    public DataItem(String curName, String curData) {
        this.curName = curName;
        this.curData = curData;
    }

    public DataItem() {
        this.curName = "";
        this.curData = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurName() {
        return curName;
    }

    public void setCurName(String curName) {
        this.curName = curName;
    }

    public String getCurData() {
        return curData;
    }

    public void setCurRate(String curData) {
        this.curData = curData;
    }
}
