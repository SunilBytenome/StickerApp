package com.github.gabrielbb.cutout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterDecoration  extends RecyclerView.Adapter<AdapterDecoration.ViewHolder> {
    Activity activity ;
    ArrayList<Integer> data;
    interfaceClick interfaceClick;
   public AdapterDecoration(Activity activity, ArrayList<Integer> data, interfaceClick interfaceClick) {
        this.activity=activity;
        this.data=data;
        this.interfaceClick=interfaceClick;
    }
    @NonNull
    @Override
    public AdapterDecoration.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_decoration, parent, false);
        AdapterDecoration.ViewHolder vh = new AdapterDecoration.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ivImage.setImageResource(data.get(position));


        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceClick.image(data.get(position));
                Toast.makeText(activity, ""+data.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            ivImage=itemView.findViewById(R.id.ivImage);
        }
    }
    interface  interfaceClick{
        void  image(int i);
    }
}
