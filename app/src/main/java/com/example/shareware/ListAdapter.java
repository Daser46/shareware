package com.example.shareware;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private ArrayList<Item> items;
    private LayoutInflater inflater;
    private Context context;

    public ListAdapter(Context context, ArrayList<Item> items){
        this.context = context;
        this.items = items;
        this.inflater = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_layout, null);
        TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
        TextView itemID = (TextView) convertView.findViewById(R.id.itemCode);
        TextView ownerName = (TextView) convertView.findViewById(R.id.ownerName);

        Item item = items.get(position);
        itemName.setText(item.getName());
        itemID.setText(item.getId());
        ownerName.setText(item.getOwner().getUsername());
        return convertView;
    }
}
