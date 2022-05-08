package com.example.resturant.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resturant.Filter.FilterProductUsere;
import com.example.resturant.R;
import com.example.resturant.activities.ShopDetailsActivity;
import com.example.resturant.models.ModelProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProdcutUser extends RecyclerView.Adapter<AdapterProdcutUser.MyHolder> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList,filterList;
    private FilterProductUsere filterProduct;
    public AdapterProdcutUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList=productsList;
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final ModelProduct modelProduct=productsList.get(position);
        final   String id =modelProduct.getProduct_id();
        String uid =modelProduct.getUid();
        String category =modelProduct.getCategory();
        String description =modelProduct.getDescription();
        String discountPrice =modelProduct.getDiscountPrice();
        String title =modelProduct.getTitle();
        String price =modelProduct.getPrice();
        String quantity =modelProduct.getQuantity();
        String productIcon =modelProduct.getProductIcon();
        String timeStamp =modelProduct.getTimeStamp();
        String discount =modelProduct.getDiscount();


        holder.textViewTitle.setText(title);
        holder.textViewDiscountPrice.setText("£"+discountPrice);
        holder.textViewPrice.setText("£"+price);
        holder.textViewDescription.setText(description);
        if (discount.equals("true"))
        {
            holder.textViewDiscountPrice.setVisibility(View.VISIBLE);
            holder.textViewPrice.setPaintFlags(holder.textViewPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.textViewDiscountPrice.setVisibility(View.GONE);
            holder.textViewPrice.setPaintFlags(0);
        }
        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_baseline_add_shopping_cart_24).into(holder.imageViewProductIcon);
        }
        catch (Exception e)
        {
            holder.imageViewProductIcon.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        }
        holder.textViewAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuantityDialog(modelProduct);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
private  double cost=0;
private  double finalCost=0;
private  int Numofquantity=0;
    private void showQuantityDialog(ModelProduct modelProduct) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        ImageView ImageViewProduct =view.findViewById(R.id.productIV);
        ImageView ImageViewPlus =view.findViewById(R.id.increaseBtn);
        ImageView ImageViewRemove =view.findViewById(R.id.descreaseBtn);
        final TextView textViewTitle=view.findViewById(R.id.DQtitleTV);
        final TextView textViewDesc=view.findViewById(R.id.DQdescProduct);
        final TextView textViewQuantity=view.findViewById(R.id.DQ_quantity);
        final TextView textViewCategory=view.findViewById(R.id.DQcategory);
        final TextView textViewDissPrice=view.findViewById(R.id.DQdisscountPrice);
        final TextView textViewPrice=view.findViewById(R.id.DQprice);
        final TextView textViewFinalPrice=view.findViewById(R.id.final_price);
        final TextView textViewNum=view.findViewById(R.id.numberOf);
        final Button buttonAddToCart=view.findViewById(R.id.continueBtn);

        final String id =modelProduct.getTimeStamp();
        String uid =modelProduct.getUid();
        String category =modelProduct.getCategory();
        String description =modelProduct.getDescription();
        String discountPrice =modelProduct.getDiscountPrice();
        final String title =modelProduct.getTitle();
        final String quantity =modelProduct.getQuantity();
        String productIcon =modelProduct.getProductIcon();
        final String timeStamp =modelProduct.getTimeStamp();
        String discount =modelProduct.getDiscount();

        final String price;
        if (discount.equals("true"))
        {
            price=modelProduct.getDiscountPrice();

            textViewPrice.setPaintFlags(textViewPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            price=modelProduct.getPrice();
            textViewDissPrice.setVisibility(View.GONE);
        }




        cost=Double.parseDouble(price.replaceAll("£",""));
        finalCost=Double.parseDouble(price.replaceAll("£",""));
        Numofquantity=1;

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_baseline_shopping_cart_24).into(ImageViewProduct);
        }
        catch (Exception e)
        {
            ImageViewProduct.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
        }
        textViewCategory.setText(category);
        textViewTitle.setText(title);
        textViewPrice.setText("£"+modelProduct.getPrice());
        textViewDesc.setText(description);
        textViewQuantity.setText(quantity);
        textViewFinalPrice.setText(""+finalCost);
        textViewNum.setText(""+Numofquantity);
        final AlertDialog dialog=builder.create();
        dialog.show();

        ImageViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalCost+=cost;
                Numofquantity++;
                textViewFinalPrice.setText("£"+finalCost);
                textViewNum.setText(""+Numofquantity);
            }
        });
        ImageViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Numofquantity>1)
                {
                    finalCost-=cost;
                    Numofquantity--;
                    textViewFinalPrice.setText("£"+finalCost);
                    textViewNum.setText(""+Numofquantity);
                }
            }
        });
        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=textViewTitle.getText().toString().trim();
                String priceEach=price;
                String quantity=textViewNum.getText().toString().trim();
                String totalPrice=textViewFinalPrice.getText().toString().trim().replace("£","");

                AddToCart(id,title,priceEach,quantity,totalPrice);
                dialog.dismiss();
            }
        });

    }
    private int itemId=1;
    private void AddToCart(String id, String title, String priceEach, String quantity, String finalPrice) {
        itemId++;
        EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .doneTableColumn();
        Boolean b =easyDB
                .addData("Item_Id",itemId)
                .addData("Item_PID",id)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",finalPrice)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();

        ((ShopDetailsActivity)context).cartItem();
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterProduct==null)
        {
            filterProduct=new FilterProductUsere(this,filterList);
        }
        return filterProduct;
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageViewProductIcon;
        private TextView textViewTitle,textViewDescription,textViewDiscountPrice,textViewPrice,textViewAddToCart;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProductIcon=itemView.findViewById(R.id.product_icon);
            textViewTitle=itemView.findViewById(R.id.rpu_title);
            textViewDescription=itemView.findViewById(R.id.rpu_description);
            textViewDiscountPrice=itemView.findViewById(R.id.UdiscountPrice);
            textViewPrice=itemView.findViewById(R.id.Uprice);
            textViewAddToCart=itemView.findViewById(R.id.addToCart);

        }
    }
}
