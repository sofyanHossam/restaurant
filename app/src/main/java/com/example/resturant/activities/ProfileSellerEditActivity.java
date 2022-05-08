package com.example.resturant.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.resturant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileSellerEditActivity extends AppCompatActivity {
ImageView imageViewBack;
    EditText editTextName, editTextPhone,editTextAddress,editTextEmail,editTextPassword;
    Button buttonUpdate;
    ImageView imageViewPro;
    SwitchCompat switchCompatOpen;
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_REQUEST_PICK_CAMERA_CODE=300;
    private static final int IMAGE_REQUEST_PICK_GALLERY_CODE=400;
    Uri image_uri=null;
   private String []  cameraP;
    private String []  storageP;
    ProgressDialog pd ;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_seller_edit);
        imageViewBack=findViewById(R.id.back);
        imageViewPro=findViewById(R.id.profile_image);
        editTextName = findViewById(R.id.L_nameEt);
        editTextPhone = findViewById(R.id.delivery_phoneEt);
        editTextAddress = findViewById(R.id.L_AddressEt);
        editTextEmail = findViewById(R.id.L_email);
        editTextPassword = findViewById(R.id.L_password);
        buttonUpdate= findViewById(R.id.edit_btn);
        switchCompatOpen= findViewById(R.id.open);
        cameraP= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storageP=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        pd= new ProgressDialog(this);
        pd.setTitle("wait...");
        pd.setCanceledOnTouchOutside(false);
        auth=FirebaseAuth.getInstance();
        checkUser();
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });
        imageViewPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

    }
String name ,phone,address,email,password;
    boolean shopOpen;
    private void inputData() {
    name =editTextName.getText().toString().trim();
    phone =editTextPhone.getText().toString().trim();
    address =editTextAddress.getText().toString().trim();
    email =editTextEmail.getText().toString().trim();
    password =editTextPassword.getText().toString().trim();
        shopOpen=switchCompatOpen.isChecked();
        updateProfile();
    }

    private void updateProfile() {
        pd.setMessage("Updating...");
        pd.show();
        if (image_uri==null)
        {
            HashMap<String ,Object> hashMap=new HashMap<>();
            hashMap.put("name" , name);
            hashMap.put("phone" , phone);
            hashMap.put("address" , address);
            hashMap.put("email" , email);
            hashMap.put("password" , password);
            hashMap.put("address" , address);
            hashMap.put("ShopOpen" , ""+shopOpen);
            FirebaseDatabase database =FirebaseDatabase.getInstance();
            DatabaseReference reference =database.getReference("Users");
            reference.child(auth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(ProfileSellerEditActivity.this, "updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(ProfileSellerEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            String filePathAndName ="Profile_images/"+""+auth.getUid();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri =uriTask.getResult();

                            if (uriTask.isSuccessful())
                            {
                                HashMap<String ,Object> hashMap=new HashMap<>();

                                hashMap.put("name" , name);
                                hashMap.put("phone" , phone);
                                hashMap.put("address" , address);
                                hashMap.put("email" , email);
                                hashMap.put("password" , password);
                                hashMap.put("ProfileImg" , ""+downloadImageUri);
                                FirebaseDatabase database =FirebaseDatabase.getInstance();
                                DatabaseReference reference =database.getReference("Users");
                                reference.child(auth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(ProfileSellerEditActivity.this, "updated Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(ProfileSellerEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(ProfileSellerEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showImagePickDialog() {
        String[] options ={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("chose Image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    if (checkCamPermission())
                    {
                        pickeFromCam();
                    }
                    else {
                        requestCamPer();
                    }
                }
                else if(which==1)
                {
                    if (checkStoragePermission())
                    {
                        pickeFromGall();
                    }
                    else {
                        requestStoragePer();
                    }

                }
            }
        });
        builder.create().show();
    }

    private void requestStoragePer()
    {
        ActivityCompat.requestPermissions(this,cameraP,CAMERA_REQUEST_CODE);
    }

    private void pickeFromGall() {
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_REQUEST_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result&&result1;
    }

    private void requestCamPer()
    {
        ActivityCompat.requestPermissions(this,storageP,STORAGE_REQUEST_CODE);
    }

    private void pickeFromCam() {
        ContentValues cv =new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_uri =getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);
        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_REQUEST_PICK_CAMERA_CODE);

    }


    private boolean checkCamPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>=0)
                {
                    boolean camAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (camAccepted&&storageAccepted)
                    {
                        pickeFromCam();

                    }else {
                        Toast.makeText(this, "error Permissions", Toast.LENGTH_SHORT).show();

                    }
                }else {

                }

            }break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>=0)
                {

                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted)
                    {
                        pickeFromGall();

                    }else {
                        Toast.makeText(this, "error Permissions", Toast.LENGTH_SHORT).show();

                    }
                }else {

                }

            }break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK)
        {
            if (requestCode==IMAGE_REQUEST_PICK_GALLERY_CODE)
            {
                image_uri=data.getData();
                imageViewPro.setImageURI(image_uri);

            }
            else if (requestCode==IMAGE_REQUEST_PICK_CAMERA_CODE)
            {
                imageViewPro.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void checkUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            loadMyInfo();
        } else {
            startActivity(new Intent(ProfileSellerEditActivity.this, LoginActivity.class));
            finish();
        }
    }
    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds :snapshot.getChildren())
                        {
                            String name =""+ds.child("name").getValue();
                            String phone =""+ds.child("phone").getValue();
                            String email =""+ds.child("email").getValue();
                            String timestamp =""+ds.child("timestamp").getValue();
                            String ShopOpen =""+ds.child("ShopOpen").getValue();
                            String uid =""+ds.child("uid").getValue();
                            String ProfileImg =""+ds.child("ProfileImg").getValue();
                            String accType =""+ds.child("accountType").getValue();
                            String address =""+ds.child("address").getValue();
                            String password =""+ds.child("password").getValue();

                            editTextName.setText(name);
                            editTextPhone.setText(phone);
                            editTextAddress.setText(address);
                            editTextEmail.setText(email);
                            editTextPassword.setText(password);

                            if (ShopOpen.equals("true"))
                            {
                                switchCompatOpen.setChecked(true);
                            }
                            else{ switchCompatOpen.setChecked(false);}

                            try {
                                Picasso.get().load(ProfileImg).placeholder(R.drawable.ic_store_24).into(imageViewPro);
                            }catch (Exception e)
                            {
                                imageViewPro.setImageResource(R.drawable.ic_store_24);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}