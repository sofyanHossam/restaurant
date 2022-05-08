package com.example.resturant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.resturant.R;
import com.example.resturant.adapters.AdapterOrderItem;
import com.example.resturant.adapters.AdapterProdcutUser;
import com.example.resturant.models.ModelOrderItem;
import com.example.resturant.models.ModelProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class OrderDetailsUserActivity extends AppCompatActivity {
private String orderTo,orderID;
    ImageView imageViewBack;
    RecyclerView recyclerViewOrders;
    TextView textViewOrderId,textViewOrderDate,textViewOrderStatue,textViewOrderAddress,textViewOrderCost;
    FirebaseAuth auth;
    ArrayList<ModelOrderItem>orderItemList;
    AdapterOrderItem adapterOrderItem;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_user);
        Intent intent=getIntent();
        orderTo=intent.getStringExtra("orderTo");
        orderID=intent.getStringExtra("orderId");

        imageViewBack=findViewById(R.id.OD_back);
        textViewOrderId=findViewById(R.id.orderId_D);
        textViewOrderDate=findViewById(R.id.orderDate_D);
        textViewOrderAddress=findViewById(R.id.orderAddress_D);
        textViewOrderCost=findViewById(R.id.orderCost_D);
        textViewOrderStatue=findViewById(R.id.orderStatue_D);
        recyclerViewOrders=findViewById(R.id.cartOrderedItem);

        auth=FirebaseAuth.getInstance();

      loadOrderDetails();
      loadOrderItem();

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void loadOrderItem() {
        orderItemList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderID).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderItemList.clear();
                        for(DataSnapshot ds :snapshot.getChildren()) {
                            ModelOrderItem modelOrderItem = ds.getValue(ModelOrderItem.class);
                            orderItemList.add(modelOrderItem);

                        }
                        adapterOrderItem=new AdapterOrderItem(OrderDetailsUserActivity.this,orderItemList);
                        recyclerViewOrders.setAdapter(adapterOrderItem);

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderDetails() {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref .child(orderTo).child("Orders").child(orderID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                   String orderBy =""+snapshot.child("orderBy").getValue();
                   String orderCost =""+snapshot.child("orderCost").getValue();
                   String address =""+snapshot.child("address").getValue();
                   String orderId =""+snapshot.child("orderId").getValue();
                   String orderStatue =""+snapshot.child("orderStatus").getValue();
                   String orderTime =""+snapshot.child("orderTime").getValue();
                   String orderTo =""+snapshot.child("orderTo").getValue();


                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));

                        String date = DateFormat.format("dd/MM/yyyy hh:mm:a",calendar).toString();
                        textViewOrderStatue.setText(orderStatue);
                        if (orderStatue.equals("in Progress"))//in Progress
                        {
                            textViewOrderStatue.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                        else   if (orderStatue.equals("completed"))
                        {
                            textViewOrderStatue.setTextColor(getResources().getColor(R.color.green));
                        }
                        else if (orderStatue.equals("cancelled"))
                        {
                            textViewOrderStatue.setTextColor(getResources().getColor(R.color.red));
                        }

                        textViewOrderId.setText(orderId);

                        textViewOrderCost.setText("£"+orderCost+"شامل التوصيل");
                        textViewOrderDate.setText(date);
                        textViewOrderAddress.setText(address);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}