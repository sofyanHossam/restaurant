package com.example.resturant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resturant.R;
import com.example.resturant.models.ModelCartItem;
import com.example.resturant.models.ModelOrderItem;
import com.example.resturant.models.ModelsOrderUser;

import java.util.ArrayList;

public class AdapterOrderItem extends RecyclerView.Adapter<AdapterOrderItem.MyHolder> {
    private Context context;
    private ArrayList<ModelOrderItem> modelOrderItemList ;

    public AdapterOrderItem(Context context, ArrayList<ModelOrderItem> modelOrderItemList) {
        this.context = context;
        this.modelOrderItemList = modelOrderItemList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordered_item,parent,false);
        return new  MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ModelOrderItem modelOrderItem=modelOrderItemList.get(position);

        String pId =modelOrderItem.getpId();
        String title =modelOrderItem.getName();
        String price =modelOrderItem.getPrice();
        final String cost =modelOrderItem.getCost();
        String quantity =modelOrderItem.getQuantity();

        holder.textViewQuantity.setText("["+quantity+"]");
        holder.textViewCost.setText(""+cost);
        holder.textViewTitle.setText(""+title);
        holder.textViewPrice.setText(""+price);
    }

    @Override
    public int getItemCount() {
        return modelOrderItemList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {



        private TextView textViewTitle,textViewQuantity,textViewCost,textViewPrice;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle=itemView.findViewById(R.id.itemTitleOI);
            textViewCost=itemView.findViewById(R.id.itemPricevOI);
            textViewQuantity=itemView.findViewById(R.id.itemQuantityOI);
            textViewPrice=itemView.findViewById(R.id.itemPriceOI);

        }
    }

}
