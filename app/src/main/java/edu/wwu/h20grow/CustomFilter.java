package edu.wwu.h20grow;

import android.widget.Filter;

import java.util.ArrayList;

public class CustomFilter extends Filter {

    MyAdapter adapter;
    ArrayList<Plant> filterList;


    public CustomFilter(ArrayList<Plant> filterList, MyAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    //Filters the plants in view as you type in the search bar
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();


        if(constraint != null && constraint.length() > 0)
        {
            constraint=constraint.toString().toUpperCase();
            ArrayList<Plant> filteredPlants=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                if(filterList.get(i).getPlantName().toUpperCase().contains(constraint))
                {
                    filteredPlants.add(filterList.get(i));
                }
            }

            results.count=filteredPlants.size();
            results.values=filteredPlants;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

       adapter.plants= (ArrayList<Plant>) results.values;

        //refreshes the results
        adapter.notifyDataSetChanged();
    }
}
