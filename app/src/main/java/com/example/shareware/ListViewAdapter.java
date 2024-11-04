package com.example.shareware;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<Item> items;
    private LayoutInflater inflater;
    private Context context;

    public ListViewAdapter(Context context, ArrayList<Item> items){
        this.context = context;
        this.items = items;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.card_view, null);
        ImageView image = (ImageView) convertView.findViewById(R.id.itemImage);
        TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
        TextView itemID = (TextView) convertView.findViewById(R.id.itemCode);
        TextView userName = (TextView) convertView.findViewById(R.id.userName);
        TextView condition = (TextView) convertView.findViewById(R.id.condition);
        Item item = items.get(position);

        //image.setImageURI(); just got this method

        String imageUrl = item.getPrimaryImage();
        //setting up image by converting it to a bitmap -- this may feel like an overkill but I couldn't think of another way
        // because android not directly have a method to set src of an ImageView as a URI
        //but this may throw Network exception we have to do this asyncly meaning one item by one I guess.
//        try {
//            URL url = new URL(imageUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream inputStream = connection.getInputStream();
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            image.setImageBitmap(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


// so here I found a solution to handle the threads by creating single threaded executor an set bit map for the image view.
// a similar approach was found on stackoverflow here I manipulated for my requirement https://stackoverflow.com/questions/58767733/the-asynctask-api-is-deprecated-in-android-11-what-are-the-alternatives
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Bitmap> loadImageCallable = new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            }
        };
        Future<Bitmap> future = executor.submit(loadImageCallable); // a blocker for other threaders until loadImageCallable execute completely
        try {
            Bitmap bitmap = future.get(); // This blocks until the task is complete
            if (bitmap != null) {
                image.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();

        itemName.setText(item.getName());
        itemID.setText(item.getId());
        userName.setText(item.getOwner().getUsername());
        condition.setText(item.getCondition());





        return convertView;
    }
}
