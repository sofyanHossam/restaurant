package com.example.resturant.activities;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.resturant.Constants;
import com.example.resturant.R;
import com.example.resturant.adapters.AdapterOrderItem;
import com.example.resturant.models.ModelOrderItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsSellerActivity extends AppCompatActivity {
    private String orderBy,orderID;
    ImageView imageViewBack,imageViewEdit;
    RecyclerView recyclerViewOrders;
    TextView textViewOrderId,textViewOrderDate,textViewOrderStatue,textViewOrderAddress,textViewOrderCost,textViewNumber;
    FirebaseAuth auth;
    ArrayList<ModelOrderItem> orderItemList;
    AdapterOrderItem adapterOrderItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);
        Intent intent=getIntent();
        orderBy=intent.getStringExtra("orderBy");
        orderID=intent.getStringExtra("orderId");

        imageViewBack=findViewById(R.id.OS_back);
        imageViewEdit=findViewById(R.id.editOrder);
        textViewOrderId=findViewById(R.id.orderId_S);
        textViewOrderDate=findViewById(R.id.orderDate_S);
        textViewOrderAddress=findViewById(R.id.orderAddress_S);
        textViewOrderCost=findViewById(R.id.orderCost_S);
        textViewOrderStatue=findViewById(R.id.orderStatue_S);
        textViewNumber=findViewById(R.id.userPhone_S);
        recyclerViewOrders=findViewById(R.id.cartOrderedItem);

        auth=FirebaseAuth.getInstance();
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDialog();
            }
        });
loadUserInfo();
loadOrderDetails();
loadOrderItem();
    }

    private void editDialog() {
        final String[]option={"in Progress","cancelled","completed"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("حالة الطلب ")
                .setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected =option[i];
                        editStatus(selected);
                    }
                }).show();
    }

    private void editStatus(final String selected) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("orderStatus",""+selected);

        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref.child(auth.getUid()).child("Orders").child(orderID)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(OrderDetailsSellerActivity.this, "تم التغيير", Toast.LENGTH_SHORT).show();
                        prepareNotificationMMessaging(orderID,selected);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderDetailsSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserInfo() {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String phone =""+snapshot.child("phone").getValue();
                        String name =""+snapshot.child("name").getValue();
                        String address =""+snapshot.child("address").getValue();

                        textViewNumber.setText(phone);
                        textViewOrderAddress.setText(address);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadOrderDetails() {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref .child(auth.getUid()).child("Orders").child(orderID)
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
                        

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadOrderItem() {
        orderItemList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(auth.getUid()).child("Orders").child(orderID).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderItemList.clear();
                        for(DataSnapshot ds :snapshot.getChildren()) {
                            ModelOrderItem modelOrderItem = ds.getValue(ModelOrderItem.class);
                            orderItemList.add(modelOrderItem);
                        }
                        adapterOrderItem=new AdapterOrderItem(OrderDetailsSellerActivity.this,orderItemList);
                        recyclerViewOrders.setAdapter(adapterOrderItem);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void prepareNotificationMMessaging(String orderId,String message)
    {
        String  NOTIFICATION_TOPIC="/topics/"+ Constants.fcm_topic;
        String NOTIFICATION_TITLE="طلبك "+orderId;
        String NOTIFICATION_MESSAGE=""+message;
        String NOTIFICATION_TYPE="orderStatuesChanged";
        JSONObject notificationJo=new JSONObject();
        JSONObject notificationBodyJo=new JSONObject();
        try{
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid",orderBy);
            notificationBodyJo.put("sellerUid",auth.getUid());
            notificationBodyJo.put("orderId",orderId);
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessaging",NOTIFICATION_MESSAGE);
            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);
        }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        sendFcmNotification(notificationJo);

    }

    private void sendFcmNotification(JSONObject notificationJo) {
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String,String>headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key="+Constants.fcmKey);

                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}