package com.example.resturant.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resturant.Filter.FilterProduct;
import com.example.resturant.models.ModelProduct;
import com.example.resturant.R;
import com.example.resturant.activities.EditProductActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller  extends RecyclerView.Adapter<AdapterProductSeller.MyHolder> implements Filterable {
    private Context context;
    public ArrayList<ModelProduct>productsList,filterList ;
    private FilterProduct filterProduct;
    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList=productsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);
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
        holder.textViewQuantity.setText(quantity);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsBottomSheet(modelProduct);
            }
        });
    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
        final BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_detail_seller,null);
        bottomSheetDialog.setContentView(view);

        ImageView imageViewBack=view.findViewById(R.id.PDback);
        ImageView imageViewEdit=view.findViewById(R.id.editBTN);
        ImageView imageViewDel=view.findViewById(R.id.delete);
        ImageView imageViewIcon=view.findViewById(R.id.addProduct_image);

        TextView textViewTitle =view.findViewById(R.id.titleTV);
        TextView textViewDescription =view.findViewById(R.id.descProduct);
        TextView textViewQuantity =view.findViewById(R.id.bs_quantity);
        TextView textViewPrice =view.findViewById(R.id.price);
        TextView textViewDiscountPrice =view.findViewById(R.id.disscountPrice);
        TextView textViewCategory =view.findViewById(R.id.category);


         final String id =modelProduct.getTimeStamp();
        String uid =modelProduct.getUid();
        String category =modelProduct.getCategory();
        String description =modelProduct.getDescription();
        String discountPrice =modelProduct.getDiscountPrice();
        final String title =modelProduct.getTitle();
        String price =modelProduct.getPrice();
        String quantity =modelProduct.getQuantity();
        String productIcon =modelProduct.getProductIcon();
        String timeStamp =modelProduct.getTimeStamp();
        String discount =modelProduct.getDiscount();

        textViewCategory.setText(category);
        textViewTitle.setText(title);
        textViewPrice.setText("£"+price);
        textViewDescription.setText(description);
        textViewDiscountPrice.setText("£"+discountPrice);
        textViewQuantity.setText(quantity);


        if (discount.equals("true"))
        {
            textViewDiscountPrice.setVisibility(View.VISIBLE);
            textViewPrice.setPaintFlags(textViewPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            textViewDiscountPrice.setVisibility(View.GONE);

        }
        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_baseline_add_shopping_cart_24).into(imageViewIcon);
        }
        catch (Exception e)
        {
            imageViewIcon.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        }
        bottomSheetDialog.show();

        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                Intent intent=new Intent(context, EditProductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);
            }
        });
        imageViewDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("delete product "+title+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteProduct(id);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })

                        .show();
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });
    }

    private void deleteProduct(String id) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(auth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "product deleted...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterProduct==null)
        {
            filterProduct=new FilterProduct(this,filterList);

        }
        return filterProduct;
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageViewProductIcon;
        private TextView textViewTitle,textViewQuantity,textViewDiscountPrice,textViewPrice;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProductIcon=itemView.findViewById(R.id.product_icon);
            textViewTitle=itemView.findViewById(R.id.title);
            textViewQuantity=itemView.findViewById(R.id.quantityTV);
            textViewDiscountPrice=itemView.findViewById(R.id.discountPrice);
            textViewPrice=itemView.findViewById(R.id.price);
        }
    }
}
