package com.example.resturant.Filter;

import android.widget.Filter;

import com.example.resturant.adapters.AdapterProductSeller;
import com.example.resturant.models.ModelProduct;

import java.util.ArrayList;

public class FilterProduct extends Filter {
    private AdapterProductSeller adapterProductSeller;
    private ArrayList<ModelProduct> productArrayList;

    public FilterProduct(AdapterProductSeller adapterProductSeller, ArrayList<ModelProduct> productArrayList) {
        this.adapterProductSeller = adapterProductSeller;
        this.productArrayList = productArrayList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results=new FilterResults();
        if (charSequence!=null&&charSequence.length()>0)
        {
            charSequence=charSequence.toString().toUpperCase();
            ArrayList<ModelProduct> filteredModels=new ArrayList<>();
            for( int i=0;i<productArrayList.size();i++)
            {
                if (productArrayList.get(i).getTitle().toUpperCase().contains(charSequence)||
                        productArrayList.get(i).getCategory().toUpperCase().contains(charSequence)
                )
                {
                    filteredModels.add(productArrayList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else {
            results.count=productArrayList.size();
            results.values=productArrayList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterProductSeller.productsList=(ArrayList<ModelProduct>)filterResults.values;
        adapterProductSeller.notifyDataSetChanged();
    }
}
