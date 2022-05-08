package com.example.resturant.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resturant.R;
import com.example.resturant.activities.OrderDetailsUserActivity;
import com.example.resturant.models.ModelProduct;
import com.example.resturant.models.ModelsOrderUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.MyHolder>{
    private Context context;
    public ArrayList<ModelsOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelsOrderUser> orderUserList) {
        this.context = context;
        this.orderUserList = orderUserList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_user,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ModelsOrderUser modelsOrderUser=orderUserList.get(position);
        final String orderId =modelsOrderUser.getOrderId();
        String orderTime =modelsOrderUser.getOrderTime();
        String orderStatus =modelsOrderUser.getOrderStatus();
        String orderBy =modelsOrderUser.getOrderBy();
        final String orderTo =modelsOrderUser.getOrderTo();
        String cost =modelsOrderUser.getOrderCost();

        loadShopInfo(modelsOrderUser,holder);

        holder.textViewCost.setText("cost: £"+cost);
        holder.textViewStatus.setText(orderStatus);
        holder.textViewOrderId.setText("Order Id:"+orderId);
        if (orderStatus.equals("in Progress"))
        {
            holder.textViewStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else   if (orderStatus.equals("completed"))
        {
            holder.textViewStatus.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (orderStatus.equals("cancelled"))
        {
            holder.textViewStatus.setTextColor(context.getResources().getColor(R.color.red));
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();
        holder.textViewDateTv.setText(date);
        holder.imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, OrderDetailsUserActivity.class);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);
            }
        });
    }

    private void loadShopInfo(ModelsOrderUser modelsOrderUser, final MyHolder holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelsOrderUser.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                   String shopName=""+snapshot.child("name").getValue();
                   holder.textViewShopName.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderUserList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageViewNext;
        private TextView textViewOrderId,textViewDateTv,textViewShopName,textViewCost,textViewStatus;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageViewNext=itemView.findViewById(R.id.next_IV);
            textViewOrderId=itemView.findViewById(R.id.orderId);
            textViewDateTv=itemView.findViewById(R.id.dateTv);
            textViewShopName=itemView.findViewById(R.id.shopName);
            textViewCost=itemView.findViewById(R.id.cost);
            textViewStatus=itemView.findViewById(R.id.status);
        }
    }
}
