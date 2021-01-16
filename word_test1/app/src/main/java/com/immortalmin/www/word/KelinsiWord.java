package com.immortalmin.www.word;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 柯林斯词典 单词类
 */
public class KelinsiWord extends Word {

    private String star;//星级
    private ArrayList<KelinsiItem> items = null;

    public KelinsiWord(){}

    public KelinsiWord(String wid, String word_en, String star, ArrayList<KelinsiItem> items) {
        super(wid, word_en);
        this.star = star;
        this.items = items;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public ArrayList<KelinsiItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<KelinsiItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "KelinsiWord{" +
                "wid='" + getWid() + '\'' +
                ", word_en=" + getWord_en() +
                ", star=" + star +
                ", items=" + items.toString() +
                '}';
    }
}
