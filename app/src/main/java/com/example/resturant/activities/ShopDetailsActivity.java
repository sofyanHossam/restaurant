package com.example.resturant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.resturant.Constants;
import com.example.resturant.R;
import com.example.resturant.adapters.AdapterCart;
import com.example.resturant.adapters.AdapterProdcutUser;
import com.example.resturant.adapters.AdapterProductSeller;
import com.example.resturant.models.ModelCartItem;
import com.example.resturant.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

import static java.lang.Double.parseDouble;


public class ShopDetailsActivity extends AppCompatActivity {
ImageView imageViewShop,imageViewCall,imageViewCart,imageViewBack,imageViewCategory;
TextView textViewName,textViewNumber,textViewAddress,textViewOpen,textViewFilter,textViewCartItems;
    EditText editTextSearch;
    FirebaseAuth auth;
    RelativeLayout relativeLayoutProduct;
    RecyclerView recyclerViewProduct;
    String shopUid;
    AdapterProdcutUser adapterProdcutUser;
    AdapterCart adapterCart;
    ArrayList<ModelProduct>productsList;
    ProgressDialog pd;
  private   ArrayList<ModelCartItem>cartItemList;
  private EasyDB easyDB;

 public  String name,phone,address;
   public String deliveryFee="7";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        imageViewCall=findViewById(R.id.callBTN);
        imageViewShop=findViewById(R.id.imgShop);
        imageViewCart=findViewById(R.id.cartSD);
        imageViewBack=findViewById(R.id.backSD);
        imageViewCategory=findViewById(R.id.SD_filterProduct);
        editTextSearch=findViewById(R.id.SDsearchProduct);

        shopUid=getIntent().getStringExtra("shopUid");

        textViewAddress=findViewById(R.id.addressSD);
        textViewName=findViewById(R.id.NameSD);
        textViewNumber=findViewById(R.id.phoneSD);
        textViewOpen=findViewById(R.id.openSD);
        textViewFilter=findViewById(R.id.SDfilterProductTV);
        textViewCartItems=findViewById(R.id.cart_item);

        relativeLayoutProduct =findViewById(R.id.SDproductRl);
        recyclerViewProduct=findViewById(R.id.SDProductRV);

        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        pd.setTitle("wait...");
        pd.setCanceledOnTouchOutside(false);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    adapterProdcutUser.getFilter().filter(charSequence);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        imageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", ""+phone,null));
                startActivity(intent);
            }
        });

        imageViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("اختر صنف");
                builder.setItems(Constants.categories1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String cate=Constants.categories1[i];
                        textViewFilter.setText(cate);
                        if (cate.equals("الكل"))
                        {
                            loadShopProduct();
                        }else{
                           adapterProdcutUser.getFilter().filter(cate);
                        }
                    }
                })
                        .show();
            }
        });


        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

       imageViewCart.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                showCartDialog();
           }
       });
        loadMyInfo();
        loadShopInfo();
        loadShopProduct();
         easyDB=EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .doneTableColumn();
        deleteCartData();
        cartItem();
    }

    private void deleteCartData() {
        easyDB.deleteAllDataFromTable();
    }
    public void cartItem()
    {
        int count=easyDB.getAllData().getCount();
        if(count<=0){
            textViewCartItems.setVisibility(View.GONE);
        }
        else{
            textViewCartItems.setVisibility(View.VISIBLE);
            textViewCartItems.setText(""+count);
        }
    }

    public double allTotalPrice=0.00;
    public TextView textViewSubTotal,textViewDelivery,textViewTotal;
    private void showCartDialog() {
        cartItemList=new ArrayList<>();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);

        Button buttonCheck=view.findViewById(R.id.confirmBtn);
        RecyclerView recyclerViewCart=view.findViewById(R.id.cartItem);
         textViewSubTotal=view.findViewById(R.id.orderTotal);
         textViewDelivery=view.findViewById(R.id.feeD);
         textViewTotal=view.findViewById(R.id.Total);


         AlertDialog.Builder builder=new AlertDialog.Builder(this);
         builder.setView(view);

        EasyDB easyDB=EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id =res.getString(1);
            String pId =res.getString(2);
            String name =res.getString(3);
            String price =res.getString(4);
            String cost =res.getString(5);
            String quantity =res.getString(6);

            allTotalPrice =allTotalPrice+ parseDouble(cost);
            ModelCartItem modelCartItem=new ModelCartItem (""+id,""+pId,""+name,""+price,""+cost,""+quantity);

            cartItemList.add(modelCartItem);
        }
        adapterCart =new AdapterCart(this,cartItemList);
        recyclerViewCart.setAdapter(adapterCart);

       textViewSubTotal.setText("£"+String.format("%.2f",allTotalPrice));
       textViewTotal.setText("£"+(allTotalPrice+ parseDouble(deliveryFee.replace("£",""))));
       AlertDialog alertDialog=builder.create();
       alertDialog.show();
       alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
           @Override
           public void onCancel(DialogInterface dialogInterface) {
               allTotalPrice=0.00;
           }
       });
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(address.equals("null")||address.equals(""))
                {
                    Toast.makeText(ShopDetailsActivity.this, "اكتب العنوان ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(phone.equals("null")||phone.equals(""))
                {
                    Toast.makeText(ShopDetailsActivity.this, "اكتب رقم الموبايل ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cartItemList.size()==0)
                {
                    Toast.makeText(ShopDetailsActivity.this, "فين الطلبات ؟ ", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitOrder();
            }
        });


    }

    private void submitOrder() {
        pd.setMessage("جاري التأكيد");
        pd.show();

        final String timeStamp=""+System.currentTimeMillis();
        String cost =textViewTotal.getText().toString().trim().replace("£","");
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("orderId",""+timeStamp);
        hashMap.put("orderTime",""+timeStamp);
        hashMap.put("orderStatus","in Progress");
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderBy",""+auth.getUid());
        hashMap.put("orderTo",""+shopUid);
        hashMap.put("address",""+address);
         final DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for(int i=0;i<cartItemList.size();i++)
                        {
                            String pId =cartItemList.get(i).getpId();
                            String Id =cartItemList.get(i).getId();
                            String cost =cartItemList.get(i).getCost();
                            String name =cartItemList.get(i).getName();
                            String price =cartItemList.get(i).getPrice();
                            String quantity =cartItemList.get(i).getQuantity();

                            HashMap<String,String> hashMap1=new HashMap<>();
                            hashMap1.put("pId",""+pId);
                            hashMap1.put("Id",""+Id);
                            hashMap1.put("cost",cost);
                            hashMap1.put("name",name);
                            hashMap1.put("price",price);
                            hashMap1.put("quantity",quantity);

                            ref.child(timeStamp).child("Items").child(pId).setValue(hashMap1);
                        }

                        pd.dismiss();
                        Toast.makeText(ShopDetailsActivity.this, "تم ارسال الطلب", Toast.LENGTH_SHORT).show();
                        prepareNotificationMMessaging(timeStamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(ShopDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
                            String phone =""+ds.child("phone").getValue();

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadShopProduct() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String Product_id = ""+snapshot.child("timeStamp").getValue();
                         name = ""+snapshot.child("name").getValue();
                         phone = ""+snapshot.child("phone").getValue();
                       String   ProfileImg = ""+snapshot.child("ProfileImg").getValue();
                         address = ""+snapshot.child("address").getValue();
                     String  ShopOpen = ""+snapshot.child("ShopOpen").getValue();

                        textViewName.setText(name);
                        textViewNumber.setText(phone);
                       textViewAddress.setText(address);

                       if (ShopOpen.equals("true"))
                       {
                           textViewOpen.setText("open !");
                       }else {
                           textViewOpen.setText("closed !");
                       }

                        try {
                            Picasso.get().load(ProfileImg).into(imageViewShop);
                        }
                        catch (Exception e)
                        {
                            imageViewShop.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadShopInfo() {
        productsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();
                        for(DataSnapshot ds :snapshot.getChildren()) {
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productsList.add(modelProduct);

                        }
                        adapterProdcutUser=new AdapterProdcutUser(ShopDetailsActivity.this,productsList);
                        recyclerViewProduct.setAdapter(adapterProdcutUser);
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void prepareNotificationMMessaging(String orderId)
    {
        String NOTIFICATION_TOPIC="/topics/"+Constants.fcm_topic;
        String NOTIFICATION_TITLE="طلب جديد"+orderId;
        String NOTIFICATION_MESSAGE="لقد استلمت طلب جديد";
        String NOTIFICATION_TYPE="NewOrder";

        JSONObject notificationJo=new JSONObject();
        JSONObject notificationBodyJo=new JSONObject();
        try{
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid",auth.getUid());
            notificationBodyJo.put("sellerUid",shopUid);
            notificationBodyJo.put("orderId",orderId);
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessaging",NOTIFICATION_MESSAGE);
            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);
        }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        sendFcmNotification(notificationJo,orderId);

    }

    private void sendFcmNotification(JSONObject notificationJo, final String orderId) {
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent =new Intent(ShopDetailsActivity.this, OrderDetailsUserActivity.class);
                intent.putExtra("orderTo",shopUid);
                intent.putExtra("orderId",orderId);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent =new Intent(ShopDetailsActivity.this, OrderDetailsUserActivity.class);
                intent.putExtra("orderTo",shopUid);
                intent.putExtra("orderId",orderId);
                startActivity(intent);
               
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