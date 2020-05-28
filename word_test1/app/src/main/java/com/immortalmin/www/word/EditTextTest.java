package com.immortalmin.www.word;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditTextTest extends AppCompatActivity {

    MyEditText myEditText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text_test);
        myEditText2 = findViewById(R.id.myEditText2);
        myEditText2.setPasteString("hahaha2");
    }
}
