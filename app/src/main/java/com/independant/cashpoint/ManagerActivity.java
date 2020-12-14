package com.independant.cashpoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ManagerActivity extends AppCompatActivity {
    CardView popupRech;
    public Spinner spinner_devise;
    public String[] listDevise = {"CDF","USD"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        popupRech = (CardView)findViewById(R.id.l_recharge);

    }

    public void checkReturn(View arg0)
    {
        Intent intent = new Intent(ManagerActivity.this,CentralActivity.class);
        startActivity(intent);
        ManagerActivity.this.finish();

    }

    public void myCart(View arg0)
    {
        Intent intent = new Intent(ManagerActivity.this,MyCarte.class);
        startActivity(intent);
        ManagerActivity.this.finish();

    }


    public void popupRecharge(View arg0)
    {
        final Dialog dialog = new Dialog(this);

        spinner_devise = (Spinner)dialog.findViewById(R.id.device);
       /* ArrayAdapter  adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDevise);
        spinner_devise.setAdapter(adapter);*/

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        dialog.setContentView(R.layout.popup_recharge);
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


}
