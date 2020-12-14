package com.independant.cashpoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ScanCarte extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_carte);
    }

    public void checkReturn(View arg0)
    {
        Intent intent = new Intent(ScanCarte.this,CentralActivity.class);
        startActivity(intent);
        ScanCarte.this.finish();

    }
}
