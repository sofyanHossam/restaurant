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

public class RegisterUserActivity extends AppCompatActivity {
    EditText editTextEmail ,editTextPassword ,editTextName ,editTextPhone,editTextc,editTextAddress;
    Button buttonRegister;
    ImageView imageViewBack;
    TextView textView,textViewSeller;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        editTextEmail=findViewById(R.id.L_emailEt);
        editTextPassword =findViewById(R.id.L_passwordEt);
        editTextAddress =findViewById(R.id.addressUser);
        editTextc =findViewById(R.id.c_passwordEt);
        editTextName =findViewById(R.id.L_nameEt);
        editTextPhone =findViewById(R.id.L_phoneEt);
        buttonRegister=findViewById(R.id.register_btn);
        textView=findViewById(R.id.have_acc);
        textViewSeller=findViewById(R.id.seller);
        imageViewBack=findViewById(R.id.back);
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("wait....");
        textViewSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUserActivity.this,RegisterSellerActivity.class));
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUserActivity.this,LoginActivity.class));
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
        Address=editTextAddress.getText().toString().trim();

        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(this, "ادخل الاسم...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, " ادخل البريد الالكتروني...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(Address)) {
            Toast.makeText(this, " ادخل عنوانك...", Toast.LENGTH_SHORT).show();
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "wrong email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "enter password...", Toast.LENGTH_SHORT).show();

        } else if (pass.length() < 8) {
            Toast.makeText(this, "كلمة المرور يجب ان تكون 8 حروف اوكثر...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(Cpass)) {
            Toast.makeText(this, "enter password again...", Toast.LENGTH_SHORT).show();

        } else if (!pass.equals(Cpass)) {
            Toast.makeText(this, "كلمة المرور والتأكيد غير متماثلين", Toast.LENGTH_SHORT).show();
        }

        else {
            createAcc();
        }
    }

    private void createAcc() {

        final ProgressDialog mDialog = new ProgressDialog(RegisterUserActivity.this);
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
                Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("save info ....");
        String timeStamp =""+System.currentTimeMillis();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("email" , email);
        hashMap.put("password" , pass);
        hashMap.put("uid" , auth.getUid());
        hashMap.put("name" , Name);
        hashMap.put("phone" , phoneNumber);
        hashMap.put("address" , Address);
        hashMap.put("timestamp" , timeStamp);
        hashMap.put("accountType" , "customer");
        hashMap.put("online" , "true");
        hashMap.put("Profile Img" , "");

        FirebaseDatabase database =FirebaseDatabase.getInstance();

        DatabaseReference reference =database.getReference("Users");
        reference.child(auth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUserActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterUserActivity.this , MainUserActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}