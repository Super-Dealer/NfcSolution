package com.independant.cashpoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.independant.cashpoint.login.login;

public class MainActivity extends AppCompatActivity {
    private static  int SPLASH_TIME_OUT = 3000;
    NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                if(activeNetwork == null){
                    Intent welcomIntent = new Intent(MainActivity.this, CheckConnexion.class);
                    startActivity(welcomIntent);
                    finish();
                }else{
                    if(nfcAdapter == null){

                        Intent welcomIntent = new Intent(MainActivity.this, AlertActivity.class);
                        startActivity(welcomIntent);
                        finish();
                    }else{
                        Intent welcomIntent = new Intent(MainActivity.this, login.class);
                        startActivity(welcomIntent);
                        finish();
                    }
                }
            }
        }, SPLASH_TIME_OUT);


    }

    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }


}
