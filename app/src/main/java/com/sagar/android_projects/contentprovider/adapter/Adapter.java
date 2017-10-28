package com.sagar.android_projects.contentprovider.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
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
 * adapter for recyclerview.
 * as both tables used have the same structure the same adapter is used for both of them.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    //dataset
    private ArrayList<DataForRecyclerview> dataForRecyclerviews;
    //context for calling the interface methods
    private Context context;

    public Adapter(ArrayList<DataForRecyclerview> dataForRecyclerviews, Context context) {
        this.dataForRecyclerviews = dataForRecyclerviews;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.singleview, parent, false));
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
        private ConstraintLayout constraintLayout;

        ViewHolder(View itemView) {
            super(itemView);

            textViewId = itemView.findViewById(R.id.textview_id);
            textViewValue = itemView.findViewById(R.id.textview_value);
            constraintLayout = itemView.findViewById(R.id.container);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Callback) context).clickedOnItem(getAdapterPosition(),
                            dataForRecyclerviews.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    public interface Callback {
        void clickedOnItem(int position, String dbId);
    }
}
