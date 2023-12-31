package com.example.intentexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<String> imagePaths; // for external image paths
    private int[] imageResIds; // for drawable resource IDs
    private boolean useDrawableResources = false;
    private final int[] imageIds = new int[]{
            R.drawable.default_image1,
            R.drawable.default_image2,
            R.drawable.default_image3,
            R.drawable.default_image4,
            R.drawable.default_image5,
            R.drawable.default_image6,
            R.drawable.default_image7,
            R.drawable.default_image8
    };

    // Constructor for external image paths
    public ImageAdapter(Context context, ArrayList<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.useDrawableResources = false;
    }

    @Override
    public int getCount() {
        return useDrawableResources ? imageResIds.length : imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return useDrawableResources ? imageResIds[position] : imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_gallery, parent, false);
        }
        imageView = convertView.findViewById(R.id.imageViewGridItem);

        if (useDrawableResources) {
            imageView.setImageResource(imageResIds[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Adjust scaleType for drawable resources
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(new File(imagePaths.get(position)).getAbsolutePath());
            float rotation = getSavedRotationForImage(imagePaths.get(position));
            imageView.setRotation(rotation);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // Adjust scaleType for bitmap images
        }

        return convertView;
    }

    private float getSavedRotationForImage(String imagePath) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String key = "Comments_" + imagePath.hashCode() + "_rotation";
        return prefs.getFloat(key, 0.0f);
    }
}
