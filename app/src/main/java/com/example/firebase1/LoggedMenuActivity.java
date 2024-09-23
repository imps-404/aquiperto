package com.example.firebase1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoggedMenuActivity extends AppCompatActivity {

    Button btLogout;
    Button btnSearch;
    Button btnShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_log);

        btLogout = findViewById(R.id.buttonLogout);
        btnSearch = findViewById(R.id.buttonSearch);
        btnShop = findViewById(R.id.buttonShop);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        LoggedMenuActivity.this,
                        SearchShopActivity.class
                );
                startActivity(i);
            }
        });
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        LoggedMenuActivity.this,
                        ShopEditActivity.class
                );
                startActivity(i);
            }
        });
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        LoggedMenuActivity.this,
                        WelcomeActivity.class
                );
                startActivity(i);
            }
        });


    }
}