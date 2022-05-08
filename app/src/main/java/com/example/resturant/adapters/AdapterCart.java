package com.example.resturant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resturant.R;
import com.example.resturant.activities.ShopDetailsActivity;
import com.example.resturant.models.ModelCartItem;
import com.example.resturant.models.ModelShop;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.MyHolder>{
    private Context context;
    private ArrayList<ModelCartItem> cartItemsList ;

    public AdapterCart(Context context, ArrayList<ModelCartItem> cartItemsList) {
        this.context = context;
        this.cartItemsList = cartItemsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart,parent,false);
        return  new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        ModelCartItem modelCartItem=cartItemsList.get(position);
        final String id =modelCartItem.getId();
        String pId =modelCartItem.getpId();
        String title =modelCartItem.getName();
        String price =modelCartItem.getPrice();
        final String cost =modelCartItem.getCost();
        String quantity =modelCartItem.getQuantity();


        holder.textViewQuantity.setText("["+quantity+"]");
        holder.textViewCost.setText(""+cost);
        holder.textViewTitle.setText(""+title);
        holder.textViewPrice.setText(""+price);

        holder.textViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                        .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                        .doneTableColumn();
                easyDB.deleteRow(1,id);
                Toast.makeText(context, "removed...", Toast.LENGTH_SHORT).show();

                cartItemsList.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

             double tx=Double.parseDouble((((ShopDetailsActivity)context).textViewTotal.getText().toString().trim().replace("£","")));
             double totalPrice=tx - Double.parseDouble(cost.replace("£",""));
             double dFee=Double.parseDouble((((ShopDetailsActivity)context).deliveryFee.replace("£","")));
             double sTotal=Double.parseDouble(String.format("%.2f",totalPrice)) - Double.parseDouble(String.format("%.2f",dFee));
                ((ShopDetailsActivity)context).allTotalPrice=0.00;
                ((ShopDetailsActivity)context).textViewSubTotal.setText("£"+String.format("%.2f",sTotal));
                ((ShopDetailsActivity)context).textViewTotal.setText("£"+String.format("%.2f", Double.parseDouble(String.format("%.2f",totalPrice))));

                ((ShopDetailsActivity)context).cartItem();
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
     
        private TextView textViewTitle,textViewRemove,textViewQuantity,textViewCost,textViewPrice;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
          
            textViewTitle=itemView.findViewById(R.id.itemTitle);
            textViewCost=itemView.findViewById(R.id.itemPricev);
            textViewQuantity=itemView.findViewById(R.id.itemQuantity);
            textViewRemove=itemView.findViewById(R.id.itemRemove);
            textViewPrice=itemView.findViewById(R.id.itemPrice);

        }
    }

}
