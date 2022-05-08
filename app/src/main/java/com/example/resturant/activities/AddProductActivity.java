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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resturant.Constants;
import com.example.resturant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {
    ImageView imageViewBack;
    EditText editTextTitle, editTextDesc,editTextPrice,editTextQuantity,editTextDisPrice;
    TextView textViewCategory;
    Button buttonADD;
    ImageView imageViewProduct;
    SwitchCompat switchCompatDiss;
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
        setContentView(R.layout.activity_add_product);

        imageViewBack=findViewById(R.id.back);
        imageViewProduct=findViewById(R.id.addProduct_image);
        editTextTitle = findViewById(R.id.title);
        textViewCategory = findViewById(R.id.category);
        editTextPrice = findViewById(R.id.price);
        editTextQuantity = findViewById(R.id.quantity);
        editTextDisPrice = findViewById(R.id.dissPrice);
        editTextDesc = findViewById(R.id.descProduct);
        editTextDisPrice.setVisibility(View.GONE);
        buttonADD= findViewById(R.id.addProduct_btn);
        imageViewBack=findViewById(R.id.AP_back);
        switchCompatDiss= findViewById(R.id.diss);
        pd= new ProgressDialog(this);
        pd.setTitle("wait...");
        pd.setCanceledOnTouchOutside(false);
        auth=FirebaseAuth.getInstance();
        switchCompatDiss.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    editTextDisPrice.setVisibility(View.VISIBLE);
                }
                else{
                    editTextDisPrice.setVisibility(View.GONE);

                }
            }
        });
        cameraP= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storageP=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });
        textViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCategory();
            }
        });
        buttonADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    String title ,desc,price,quant,category,discountPrice;
    boolean discount=false;
    private void inputData() {
        title =editTextTitle.getText().toString().trim();
        desc =editTextDesc.getText().toString().trim();
        price =editTextPrice.getText().toString().trim();
        quant =editTextQuantity.getText().toString().trim();
        category =textViewCategory.getText().toString().trim();
        discount=switchCompatDiss.isChecked();
       /////////
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "enter title...", Toast.LENGTH_SHORT).show();
            return;
        }
        else  if (TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "enter description...", Toast.LENGTH_SHORT).show();
            return;
        }
        else  if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "enter price...", Toast.LENGTH_SHORT).show();
            return;
        }
        else   if (TextUtils.isEmpty(quant)) {
            Toast.makeText(this, "enter quantity...", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "enter category...", Toast.LENGTH_SHORT).show();
            return;
        }
        else  if (discount) {
            discountPrice =editTextDisPrice.getText().toString().trim();
            if (TextUtils.isEmpty(discountPrice)) {
                Toast.makeText(this, "enter a new price...", Toast.LENGTH_SHORT).show();
                return;
            }
            }
        else{
            discountPrice="0";
        }
        addProduct();
    }

    private void addProduct() {
        final String timeStamp =""+System.currentTimeMillis();
        pd.setMessage("adding product...");
        pd.show();
        if (image_uri==null)
        {
            HashMap<String ,Object> hashMap=new HashMap<>();
            hashMap.put("title" , title);
            hashMap.put("uid" , auth.getUid());
            hashMap.put("product" , timeStamp);
            hashMap.put("timeStamp" , timeStamp);
            hashMap.put("description" , desc);
            hashMap.put("category" , category);
            hashMap.put("quantity" , quant);
            hashMap.put("productIcon" , "");
            hashMap.put("price" , price);
            hashMap.put("discountPrice" , discountPrice);
            hashMap.put("discount" , ""+discount);
            FirebaseDatabase database =FirebaseDatabase.getInstance();
            DatabaseReference reference =database.getReference("Users");
            reference.child(auth.getUid()).child("Products").child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(AddProductActivity.this, "added Successfully", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            String filePathAndName ="product_images/"+""+timeStamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri =uriTask.getResult();
                            if (uriTask.isSuccessful())
                            {
                                HashMap<String ,Object> hashMap=new HashMap<>();
                                hashMap.put("title" , title);
                                hashMap.put("uid" , auth.getUid());
                                hashMap.put("product" , timeStamp);
                                hashMap.put("timeStamp" , timeStamp);
                                hashMap.put("description" , desc);
                                hashMap.put("category" , category);
                                hashMap.put("quantity" , quant);
                                hashMap.put("productIcon" ,""+ downloadImageUri);
                                hashMap.put("price" , price);
                                hashMap.put("discountPrice" , discountPrice);
                                hashMap.put("discount" , ""+discount);

                                FirebaseDatabase database =FirebaseDatabase.getInstance();
                                DatabaseReference reference =database.getReference("Users");
                                reference.child(auth.getUid()).child("Products").child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(AddProductActivity.this, "added Successfully", Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private  void clearData()
    {
        editTextDisPrice.setText("");
        editTextPrice.setText("");
        editTextTitle.setText("");
        editTextDesc.setText("");
        textViewCategory.setText("");
        editTextQuantity.setText("");
        imageViewProduct.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
    }
    private void createCategory() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("chose product category")
                .setItems(Constants.categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String cate=Constants.categories[i];
                        textViewCategory.setText(cate);
                    }
                })
                .show();
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
                imageViewProduct.setImageURI(image_uri);

            }
            else if (requestCode==IMAGE_REQUEST_PICK_CAMERA_CODE)
            {
                imageViewProduct.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}