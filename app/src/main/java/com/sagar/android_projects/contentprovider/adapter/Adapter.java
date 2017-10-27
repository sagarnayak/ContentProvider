package com.sagar.android_projects.contentprovider.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sagar.android_projects.contentprovider.R;
import com.sagar.android_projects.contentprovider.pojo.DataForRecyclerview;

import java.util.ArrayList;

/**
 * Created by sagar on 10/27/2017.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<DataForRecyclerview> dataForRecyclerviews;

    public Adapter(ArrayList<DataForRecyclerview> dataForRecyclerviews) {
        this.dataForRecyclerviews = dataForRecyclerviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.singleview, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewId.setText(String.valueOf(dataForRecyclerviews.get(position).getId()));
        holder.textViewValue.setText(String.valueOf(dataForRecyclerviews.get(position).getValue()));
    }

    @Override
    public int getItemCount() {
        return dataForRecyclerviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewId;
        private TextView textViewValue;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewId = itemView.findViewById(R.id.textview_id);
            textViewValue = itemView.findViewById(R.id.textview_value);
        }
    }
}
