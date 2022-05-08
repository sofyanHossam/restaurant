package com.example.resturant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.resturant.adapters.AdapterOrderSeller;
import com.example.resturant.adapters.AdapterOrderUser;
import com.example.resturant.adapters.AdapterProductSeller;
import com.example.resturant.Constants;
import com.example.resturant.models.ModelOderSeller;
import com.example.resturant.models.ModelProduct;
import com.example.resturant.R;
import com.example.resturant.models.ModelsOrderUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainSellerActivity extends AppCompatActivity {
    ImageView imageViewLogout,imageViewEdit,imageViewCategory,imageViewFilterOrder,imageViewSettings;
    EditText editTextSearch;
    RelativeLayout relativeLayoutProduct,relativeLayoutOrder;
    RecyclerView recyclerViewProduct,recyclerViewOrder;
    FloatingActionButton fab;
    FirebaseAuth auth;
    ArrayList<ModelProduct>productList;
    AdapterProductSeller adapterProductSeller;
    ArrayList<ModelOderSeller>OderSellerList;
    AdapterOrderSeller adapterOrderSeller;
    TextView textViewName,textViewTabProduct,textViewTabOrder,textViewFilter,textViewFilterOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);
        textViewName=findViewById(R.id.S_name_tv);
        textViewTabProduct=findViewById(R.id.product_tab);
        editTextSearch=findViewById(R.id.searchProduct);
        textViewTabOrder=findViewById(R.id.order_tab);
        textViewFilter=findViewById(R.id.filterProductTV);
        textViewFilterOrder=findViewById(R.id.filterOrderTV);
        imageViewLogout =findViewById(R.id.Slogout);
        imageViewEdit =findViewById(R.id.editS_pro);
        imageViewSettings =findViewById(R.id.S_settings);
        imageViewCategory =findViewById(R.id.filterProduct);
        imageViewFilterOrder =findViewById(R.id.filterOrderIV);
        relativeLayoutProduct =findViewById(R.id.productRl);
        relativeLayoutOrder =findViewById(R.id.orderRl);
        recyclerViewProduct=findViewById(R.id.ProductRV);
        recyclerViewOrder=findViewById(R.id.orderSellerRv);
        fab=findViewById(R.id.fab);
        auth=FirebaseAuth.getInstance();
        checkUser();
        loadAllProduct();
        loadAllOrder();
        showProductsUI();

        imageViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSellerActivity.this,SettingsActivity.class));
            }
        });

        imageViewFilterOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String [] options={"all","in Progress","completed","cancelled"};
                AlertDialog.Builder builder=new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("عرض طلبات");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i==0)
                        {
                            textViewFilterOrder.setText("all");
                            adapterOrderSeller.getFilter().filter("");
                        }else
                            {
                                String oc=options[i];
                                textViewFilterOrder.setText(""+oc);
                                adapterOrderSeller.getFilter().filter(oc);
                        }
                    }
                })
                        .show();

            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    adapterProductSeller.getFilter().filter(charSequence);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                checkUser();
            }
        });
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSellerActivity.this,ProfileSellerEditActivity.class));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));
            }
        });
        textViewTabProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductsUI();
            }
        });
        textViewTabOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrdersUI();
            }
        });
        imageViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("اختر صنف");
                 builder.setItems(Constants.categories1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String cate=Constants.categories1[i];
                        textViewFilter.setText(cate);
                        if (cate.equals("الكل"))
                        {
                            loadAllProduct();
                        }else{
                            loadFilterd(cate);
                        }
                    }
                })
                 .show();
            }
        });

    }

    private void loadAllOrder() {
        OderSellerList  =new ArrayList<>();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(auth.getUid()).child("Orders")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    OderSellerList.clear();
                                        for (DataSnapshot ds :snapshot.getChildren())
                                        {
                                            ModelOderSeller modelOderSeller=ds.getValue(ModelOderSeller.class);
                                            OderSellerList.add(modelOderSeller);
                                        }
                                        adapterOrderSeller=new AdapterOrderSeller(MainSellerActivity.this,OderSellerList);
                                        recyclerViewOrder.setAdapter(adapterOrderSeller);
                                    }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
           

    private void loadFilterd(final String cate) {
        productList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(auth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for(DataSnapshot ds :snapshot.getChildren()) {
                            String pc=""+ds.child("category").getValue();
                            if (cate.equals(pc))
                            {
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }
                        }
                        adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                        recyclerViewProduct.setAdapter(adapterProductSeller);
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProduct() {
        productList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(auth.getUid()).child("Products")
            .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               productList.clear();
                for(DataSnapshot ds :snapshot.getChildren()) {
                    ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                    productList.add(modelProduct);
                }
                   adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                    recyclerViewProduct.setAdapter(adapterProductSeller);
                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                            textViewName.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void showOrdersUI() {
        relativeLayoutOrder.setVisibility(View.VISIBLE);
        relativeLayoutProduct.setVisibility(View.GONE);
        textViewTabOrder.setTextColor(getResources().getColor(R.color.colorBlack));
        textViewTabOrder.setBackgroundResource(R.drawable.shape_rect04);

        textViewTabProduct.setTextColor(getResources().getColor(R.color.colorWhite));
        textViewTabProduct.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showProductsUI() {
        relativeLayoutProduct.setVisibility(View.VISIBLE);
        relativeLayoutOrder.setVisibility(View.GONE);

        textViewTabProduct.setTextColor(getResources().getColor(R.color.colorBlack));
        textViewTabProduct.setBackgroundResource(R.drawable.shape_rect04);

        textViewTabOrder.setTextColor(getResources().getColor(R.color.colorWhite));
        textViewTabOrder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void checkUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            loadMyInfo();
        } else {
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();
        }
    }


}