package com.example.resturant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resturant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText editTextEmail ,editTextPassword;
    Button buttonLogin;
    TextView textViewNoACC,textViewFP;
  private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail=findViewById(R.id.L_emailEt);
        editTextPassword=findViewById(R.id.L_passwordEt);
        buttonLogin=findViewById(R.id.Login_btn);
        textViewNoACC=findViewById(R.id.NO_account_tv);
        textViewFP=findViewById(R.id.recover_password);
        auth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        textViewNoACC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterUserActivity.class));
            }
        });
        textViewFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    loginUser();
            }
        });
    }

    private void loginUser() {
        String Email =editTextEmail.getText().toString().trim();
        String Password =editTextPassword.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
        {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
        }
       if (TextUtils.isEmpty(Password)) {
            Toast.makeText(this, "enter password...", Toast.LENGTH_SHORT).show();}
        else if (TextUtils.isEmpty(Email)) {
            Toast.makeText(this, "enter password...", Toast.LENGTH_SHORT).show();}
else {
        progressDialog.setMessage("Login...");
        progressDialog.show();
        auth.signInWithEmailAndPassword(Email, Password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        makeMeOnline();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });}
    }

    private void makeMeOnline() {
        progressDialog.setMessage("checking user...");
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("online","true");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(auth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                                     checkUserType();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserType() {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds :snapshot.getChildren())
                        {
                            String accountType =""+ds.child("accountType").getValue();
                            if (accountType.equals("customer"))
                            {
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this,MainUserActivity.class));
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this,MainSellerActivity.class));
                                finish();

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover password");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailEt =new EditText(this);
        emailEt.setHint("enter your email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email =emailEt.getText().toString().trim();
                beginRecovry(email);


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovry(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "check your emails", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}