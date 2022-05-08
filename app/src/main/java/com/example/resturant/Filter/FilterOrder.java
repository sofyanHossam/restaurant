package com.example.resturant.Filter;

import android.widget.Filter;

import com.example.resturant.adapters.AdapterOrderSeller;
import com.example.resturant.adapters.AdapterProductSeller;
import com.example.resturant.models.ModelOderSeller;
import com.example.resturant.models.ModelProduct;

import java.util.ArrayList;

public class FilterOrder extends Filter {
    private AdapterOrderSeller adapterOrderSeller;
    private ArrayList<ModelOderSeller> oderSellerList;

    public FilterOrder(AdapterOrderSeller adapterOrderSeller, ArrayList<ModelOderSeller> oderSellerList) {
        this.adapterOrderSeller = adapterOrderSeller;
        this.oderSellerList = oderSellerList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results=new FilterResults();
        if (charSequence!=null&&charSequence.length()>0)
        {

            charSequence=charSequence.toString().toUpperCase();
            ArrayList<ModelOderSeller> filteredModels=new ArrayList<>();
            for( int i=0;i<oderSellerList.size();i++)
            {
                if (oderSellerList.get(i).getOrderStatus().toUpperCase().contains(charSequence))
                {
                    filteredModels.add(oderSellerList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else {
            results.count=oderSellerList.size();
            results.values=oderSellerList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterOrderSeller.oderSellerList=(ArrayList<ModelOderSeller>)filterResults.values;
        adapterOrderSeller.notifyDataSetChanged();
    }
}
