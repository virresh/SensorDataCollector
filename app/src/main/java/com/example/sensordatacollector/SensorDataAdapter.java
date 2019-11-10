package com.example.sensordatacollector;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sensordatacollector.models.SensorDataModel;

import java.util.List;

public class SensorDataAdapter extends RecyclerView.Adapter<SensorDataAdapter.MyViewHolder> {
    private List<SensorDataModel> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public SensorDataAdapter(List<SensorDataModel> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public SensorDataAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sensordataview, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).get_display_string());

    }

    @Override
    public int getItemCount() {
        if(mDataset == null) return 0;
        return mDataset.size();
    }
}