package com.independant.cashpoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class AlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        AnimateBell();
    }

    public void AnimateBell() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shakeanimation);
        ImageView imgBell= (ImageView) findViewById(R.id.img_nfc_false);
        imgBell.setImageResource(R.drawable.nfc_false);
        imgBell.setAnimation(shake);
    }
}
