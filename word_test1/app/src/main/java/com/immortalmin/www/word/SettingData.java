package com.immortalmin.www.word;

import java.io.Serializable;


/**
 * setting
 */
public class SettingData implements Serializable {
    private int recite_times;

    public SettingData() {
    }

    public SettingData(int recite_times) {
        this.recite_times = recite_times;
    }

}