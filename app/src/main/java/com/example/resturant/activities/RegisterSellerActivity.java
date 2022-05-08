package com.example.resturant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resturant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterSellerActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextName, editTextPhone, editTextc,editTextAddress;
    Button buttonRegister;
    ImageView imageViewBack;
    TextView textView, textViewcus;
    FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);
        editTextEmail = findViewById(R.id.L_emailEt);
        editTextPassword = findViewById(R.id.L_passwordEt);
        editTextAddress = findViewById(R.id.addressSeller);
        editTextc = findViewById(R.id.c_passwordEt);
        editTextName = findViewById(R.id.L_nameEt);
        editTextPhone = findViewById(R.id.d_phoneEt);
        buttonRegister = findViewById(R.id.register_btn);
        textView = findViewById(R.id.have_acc);
        textViewcus = findViewById(R.id.user);
        auth = FirebaseAuth.getInstance();
        imageViewBack=findViewById(R.id.back);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        textViewcus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterSellerActivity.this, RegisterSellerActivity.class));
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterSellerActivity.this, LoginActivity.class));
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    String Name, phoneNumber, email, pass, Cpass,Address;

    private void inputData() {
        Name = editTextName.getText().toString().trim();
        phoneNumber = editTextPhone.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        pass = editTextPassword.getText().toString().trim();
        Cpass = editTextc.getText().toString().trim();
        Address = editTextAddress.getText().toString().trim();


        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(this, " ادخل الاسم...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "ادخل البريد الاكتروني...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(Address)) {
            Toast.makeText(this, "ادخل العنوان...", Toast.LENGTH_SHORT).show();
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "wrong email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "enter password...", Toast.LENGTH_SHORT).show();

        } else if (pass.length() < 8) {
                Toast.makeText(this, "كلمة المرور يجب الا تقل عن 8 احرف...", Toast.LENGTH_SHORT).show();
            }

        if (TextUtils.isEmpty(Cpass)) {
            Toast.makeText(this, "enter password again...", Toast.LENGTH_SHORT).show();

        } else if (!pass.equals(Cpass)) {
                Toast.makeText(this, "كلمة المرور والتأكيد غير متماثلان", Toast.LENGTH_SHORT).show();
            }

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, " ادخل رقم الموبايل...", Toast.LENGTH_SHORT).show();
        } else  if (phoneNumber.length() < 10)
            {
                Toast.makeText(this, "wrong number", Toast.LENGTH_SHORT).show();
            }
        else {
            createAcc();
        }
        }

    private void createAcc() {

        final ProgressDialog mDialog = new ProgressDialog(RegisterSellerActivity.this);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("Registration in progress please wait......");
        mDialog.show();
        auth.createUserWithEmailAndPassword(email,pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFirebaseData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("save info ....");
        String timeStamp =""+System.currentTimeMillis();
        HashMap<Object ,String> hashMap=new HashMap<>();
        hashMap.put("email" , email);
        hashMap.put("password" , pass);
        hashMap.put("uid" , auth.getUid());
        hashMap.put("name" , Name);
        hashMap.put("phone" , phoneNumber);
        hashMap.put("timestamp" , timeStamp);
        hashMap.put("address" ,Address);
        hashMap.put("accountType" ,"admin");
        hashMap.put("ShopOpen" , "true");
        hashMap.put("ProfileImg" ,"");

        FirebaseDatabase database =FirebaseDatabase.getInstance();

        DatabaseReference reference =database.getReference("Users");
        reference.child(auth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSellerActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterSellerActivity.this , MainSellerActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
