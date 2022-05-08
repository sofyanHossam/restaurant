package com.example.resturant.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resturant.R;
import com.example.resturant.activities.ShopDetailsActivity;
import com.example.resturant.models.ModelShop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShops extends RecyclerView.Adapter<AdapterShops.MyHolder> {

    private Context context;
    public ArrayList<ModelShop> ShopList ;

    public AdapterShops(Context context, ArrayList<ModelShop> shopList) {
        this.context = context;
        ShopList = shopList;
    }

    @NonNull
    @Override
    public AdapterShops.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_shop,parent,false);
        return  new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShops.MyHolder holder, int position) {
        ModelShop modelShop=ShopList.get(position);
          String accType =modelShop.getAccountType();
          String address =modelShop.getAddress();
          String email =modelShop.getEmail();
          String name =modelShop.getName();
          String phone =modelShop.getPhone();
          String profileImg =modelShop.getProfileImg();
          String shopOpen =modelShop.getShopOpen();
          String timestamp =modelShop.getTimestamp();
          final String uid =modelShop.getUid();


          //holder.textViewClosed.setText(shopOpen);
          holder.textViewPhone.setText(phone);
          holder.textViewAddress.setText(address);
          holder.textViewTitle.setText(name);
          if (shopOpen.equals("true"))
          {
            holder.imageViewOnline.setVisibility(View.VISIBLE);
            holder.textViewClosed.setVisibility(View.GONE);
          }
          else{
              holder.imageViewOnline.setVisibility(View.GONE);
              holder.textViewClosed.setVisibility(View.VISIBLE);
          }
        try {
            Picasso.get().load(profileImg).placeholder(R.drawable.ic_store_24).into(holder.imageViewShopsIcon);
        }
        catch (Exception e)
        {
            holder.imageViewShopsIcon.setImageResource(R.drawable.ic_store_24);
        }
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent= new Intent(context, ShopDetailsActivity.class);
        intent.putExtra("shopUid",uid);
        context.startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
        return ShopList.size();
    }
    class MyHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageViewShopsIcon,imageViewOnline;
        private TextView textViewTitle,textViewAddress,textViewPhone,textViewClosed;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageViewShopsIcon=itemView.findViewById(R.id.shop_icon);
            textViewTitle=itemView.findViewById(R.id.shop_title);
            textViewAddress=itemView.findViewById(R.id.shop_address);
            textViewPhone=itemView.findViewById(R.id.shop_phone);
            textViewClosed=itemView.findViewById(R.id.closed);
            imageViewOnline=itemView.findViewById(R.id.onLine);
        }
    }
}
