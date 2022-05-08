package com.example.resturant.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resturant.Filter.FilterOrder;
import com.example.resturant.Filter.FilterProduct;
import com.example.resturant.R;
import com.example.resturant.activities.OrderDetailsSellerActivity;
import com.example.resturant.activities.OrderDetailsUserActivity;
import com.example.resturant.models.ModelOderSeller;
import com.example.resturant.models.ModelsOrderUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderSeller  extends RecyclerView.Adapter<AdapterOrderSeller.MyHolder> implements Filterable {
    private Context context;
    public ArrayList<ModelOderSeller> oderSellerList,filterList;
    private FilterOrder filterOrder;
    public AdapterOrderSeller(Context context, ArrayList<ModelOderSeller> oderSellerList) {
        this.context = context;
        this.oderSellerList = oderSellerList;
        this.filterList=oderSellerList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        ModelOderSeller modelOderSeller=oderSellerList.get(position);
        final String orderId =modelOderSeller.getOrderId();
        String orderTime =modelOderSeller.getOrderTime();
        String orderStatus =modelOderSeller.getOrderStatus();
        final String orderBy =modelOderSeller.getOrderBy();
        final String orderTo =modelOderSeller.getOrderTo();
        String cost =modelOderSeller.getOrderCost();


        holder.textViewCost.setText("cost: £"+cost);
        holder.textViewStatus.setText(orderStatus);
        holder.textViewOrderId.setText("Order Id:"+orderId);


        loadUserInfo(modelOderSeller,holder);

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


//        holder.imageViewNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent =new Intent(context, OrderDetailsUserActivity.class);
//                intent.putExtra("orderTo",orderTo);
//                intent.putExtra("orderId",orderId);
//                context.startActivity(intent);
//            }
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, OrderDetailsSellerActivity.class);
                intent.putExtra("orderBy",orderBy);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);
            }
        });

    }

    private void loadUserInfo(ModelOderSeller modelOderSeller, final MyHolder holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOderSeller.getOrderBy())
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
        return oderSellerList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterOrder==null)
        {
            filterOrder=new FilterOrder(this,filterList);
        }
        return filterOrder;
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageViewNext;
        private TextView textViewOrderId,textViewDateTv,textViewShopName,textViewCost,textViewStatus;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageViewNext=itemView.findViewById(R.id.next_IV_OS);
            textViewOrderId=itemView.findViewById(R.id.orderIdOS);
            textViewDateTv=itemView.findViewById(R.id.dateTvOS);
            textViewShopName=itemView.findViewById(R.id.NameOS);
            textViewCost=itemView.findViewById(R.id.costOS);
            textViewStatus=itemView.findViewById(R.id.statusOS);
        }
    }
}
