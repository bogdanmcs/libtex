package com.ad_victoriam.libtex.admin;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;

public class AddBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_book);
    }
    
    public void clicked(View view) {
        switch(view.getId()) {
            case R.id.bAdd:
                // validate items


                // call the firebase api
                break;

            default:
        }
    }
}
