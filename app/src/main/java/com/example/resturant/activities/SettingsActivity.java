package com.example.resturant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resturant.Constants;
import com.example.resturant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat switchCompat;
    private ImageView imageViewBack;
    private TextView textViewStatue;
    FirebaseAuth auth;
    SharedPreferences sp;
    SharedPreferences.Editor spEditor;
    boolean isChecked=false;
    private static final String enableMessage="الاشعارات تعمل";
    private static final String disEnableMessage="اغلاق الاشعارات";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchCompat=findViewById(R.id.fcmSwitch);
        textViewStatue=findViewById(R.id.statusSetting);
        imageViewBack=findViewById(R.id.SettingsBack);
        auth=FirebaseAuth.getInstance();
        sp=getSharedPreferences("SETTINGS_SP",MODE_PRIVATE);

        isChecked=sp.getBoolean("FCM_ENABLED",false);
        switchCompat.setChecked(isChecked);

        if (isChecked){
            textViewStatue.setText(enableMessage);
        }else{
            textViewStatue.setText(disEnableMessage);

        }
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                        subs();
                }else{
                    unSubs();
                }
            }
        });

    }

    private void subs(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.fcm_topic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        spEditor=sp.edit();
                        spEditor.putBoolean("FCM_ENABLED",true);
                        spEditor.apply();
                        Toast.makeText(SettingsActivity.this, ""+enableMessage, Toast.LENGTH_SHORT).show();
                        textViewStatue.setText(enableMessage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void unSubs(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.fcm_topic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        spEditor=sp.edit();
                        spEditor.putBoolean("FCM_ENABLED",false);
                        spEditor.apply();
                        Toast.makeText(SettingsActivity.this, ""+disEnableMessage, Toast.LENGTH_SHORT).show();
                        textViewStatue.setText(disEnableMessage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}