package com.github.gabrielbb.cutout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapertSticker extends RecyclerView.Adapter<AdapertSticker.ViewHolder> {
    Activity activity ;
    ArrayList<String> data;
    interfaceClick interfaceClick;
    public AdapertSticker(Activity activity, ArrayList<String> data, interfaceClick interfaceClick) {
        this.activity=activity;
        this.data=data;
        this.interfaceClick=interfaceClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_sticker, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      //int unincode=  Integer.valueOf(data.get(position));
     //holder.ivImage.setText("\\u2605");
    holder.ivImage.setText(StringEscapeUtils.unescapeJava(data.get(position)));
     //   holder.ivImage.setText(Integer.parseInt(data.get(position)));
        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceClick.image(StringEscapeUtils.unescapeJava(data.get(position)));
                Toast.makeText(activity, ""+data.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ivImage;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            ivImage=itemView.findViewById(R.id.ivImage);
        }
    }
    interface  interfaceClick{
        void  image(String i);
    }
}
