package com.example.resturant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.resturant.R;
import com.example.resturant.adapters.AdapterOrderSeller;
import com.example.resturant.adapters.AdapterOrderUser;
import com.example.resturant.adapters.AdapterShops;
import com.example.resturant.models.ModelOderSeller;
import com.example.resturant.models.ModelShop;
import com.example.resturant.models.ModelsOrderUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainUserActivity extends AppCompatActivity {
    ImageView imageViewLogout ,imageViewEdit,imageViewSettings;
    RelativeLayout relativeLayoutShops,relativeLayoutMyOrder;
    FirebaseAuth auth;
    TextView textViewName,textViewTabShops,textViewTabMyOrder;
    ProgressDialog pd;
    RecyclerView recyclerViewShops,recyclerViewOrder;
    ArrayList<ModelShop>shopList;
    ArrayList<ModelsOrderUser>orderUserList;
    AdapterShops adapterShops;
    AdapterOrderUser adapterOrderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        textViewTabShops=findViewById(R.id.shops_tab);
        textViewTabMyOrder =findViewById(R.id.myorder_tab);
        imageViewLogout = findViewById(R.id.Ulogout);
        imageViewSettings = findViewById(R.id.U_settings);
        imageViewEdit = findViewById(R.id.editU_pro);
        textViewName = findViewById(R.id.U_name_tv);
        relativeLayoutShops =findViewById(R.id.shopsRl);
        relativeLayoutMyOrder =findViewById(R.id.myOrderRl);
        recyclerViewShops=findViewById(R.id.shopRV);
        recyclerViewOrder=findViewById(R.id.orderUserRv);
        auth = FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        pd.setTitle("wait...");
        pd.setCanceledOnTouchOutside(false);
        checkUser();
        showShopsUI();

        imageViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this,SettingsActivity.class));
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
                startActivity(new Intent(MainUserActivity.this,ProfileUserEditActivity.class));
            }
        });
        textViewTabShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShopsUI();
            }
        });
        textViewTabMyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyOrderUI();
            }
        });
    }

    private void showMyOrderUI() {
        relativeLayoutMyOrder.setVisibility(View.VISIBLE);
        relativeLayoutShops.setVisibility(View.GONE);
        textViewTabMyOrder.setTextColor(getResources().getColor(R.color.colorBlack));
        textViewTabMyOrder.setBackgroundResource(R.drawable.shape_rect04);

        textViewTabShops.setTextColor(getResources().getColor(R.color.colorWhite));
        textViewTabShops.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showShopsUI() {
        relativeLayoutShops.setVisibility(View.VISIBLE);
        relativeLayoutMyOrder.setVisibility(View.GONE);

        textViewTabShops.setTextColor(getResources().getColor(R.color.colorBlack));
        textViewTabShops.setBackgroundResource(R.drawable.shape_rect04);

        textViewTabMyOrder.setTextColor(getResources().getColor(R.color.colorWhite));
        textViewTabMyOrder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void checkUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
           loadMyInfo();
        } else {
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
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

                       textViewName.setText(name);
                       loadShops();
                       loadOrders();

                   }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrders() {
        orderUserList=new ArrayList<>();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
           orderUserList.clear();
                for (DataSnapshot ds :snapshot.getChildren())
                {
                    String uid =ds.getRef().getKey();
                    DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(auth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot ds :snapshot.getChildren())
                                        {
                                            ModelsOrderUser modelsOrderUser=ds.getValue(ModelsOrderUser.class);
                                            orderUserList.add(modelsOrderUser);
                                        }
                                        adapterOrderUser=new AdapterOrderUser(MainUserActivity.this,orderUserList);
                                        recyclerViewOrder.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadShops() {
        shopList=new ArrayList<>();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                   shopList.clear();
                   for (DataSnapshot ds :snapshot.getChildren())
                   {
                       ModelShop modelShop= ds.getValue(ModelShop.class);
                       shopList.add(modelShop);
                   }
                   adapterShops= new AdapterShops(MainUserActivity.this,shopList);
                   recyclerViewShops.setAdapter(adapterShops);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}