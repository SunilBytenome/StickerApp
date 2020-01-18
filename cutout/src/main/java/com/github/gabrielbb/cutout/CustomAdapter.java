package com.github.gabrielbb.cutout;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    List<String > arrayList;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, List<String > arrayList) {
        this.context = applicationContext;
        this.arrayList = arrayList;

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner, null);
        TextView names = (TextView) view.findViewById(R.id.tvTextSpinner);
        names.setTextColor(Color.parseColor("#000000"));

        names.setText(arrayList.get(i));
        return view;
    }
}