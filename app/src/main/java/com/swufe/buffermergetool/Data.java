package com.swufe.buffermergetool;

import android.content.SharedPreferences;

import java.io.Serializable;

public class Data implements Serializable {
    private DataManager DM;

    public Data(DataManager DM) {
        this.DM = DM;
    }

    public DataManager getDM() {
        return DM;
    }

    public void setDM(DataManager DM) {
        this.DM = DM;
    }
}
