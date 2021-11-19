package com.complete.weatherapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.complete.weatherapplication.databinding.CardviewBinding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.AdapterViewHolder>{
    public Context ctx;
    public ArrayList<WeatherRVModel> arrayList;


    public WeatherAdapter(Context ctx, ArrayList<WeatherRVModel> arrayList) {
        this.ctx = ctx;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterViewHolder(CardviewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {

        WeatherRVModel model = arrayList.get(position);
        holder.binding.temperature.setText(model.getTemperature()+"°C");
        Picasso.get().load("https:".concat(model.getIcon())).into(holder.binding.icon);

        holder.binding.windspeed.setText(model.getWindSpeed()+"KM/H");
        SimpleDateFormat input = new SimpleDateFormat("yyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try{
            Date t = input.parse(model.getTime());
            holder.binding.time.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class AdapterViewHolder extends RecyclerView.ViewHolder {
        CardviewBinding binding;//Name of the test_list_item.xml in camel case + "Binding"

        public AdapterViewHolder(CardviewBinding b){
            super(b.getRoot());
            binding = b;
        }

    }
}
