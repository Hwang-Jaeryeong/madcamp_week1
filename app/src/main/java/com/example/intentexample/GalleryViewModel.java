package com.example.intentexample;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class GalleryViewModel extends ViewModel {
    private static final String PREFS_NAME = "GalleryAppPreferences";
    private static final String IMAGES_KEY = "SavedImagePaths";

    private ArrayList<String> imagePaths = new ArrayList<>();
    private ArrayList<String> comments = new ArrayList<>();
    private MutableLiveData<ArrayList<String>> commentsLiveData = new MutableLiveData<>();

    // Load image paths from SharedPreferences
    public void loadImagePaths(Context context) {
        imagePaths.clear(); // Clear existing paths
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedImagePaths = prefs.getString(IMAGES_KEY, "");
        if (!savedImagePaths.isEmpty()) {
            imagePaths.addAll(Arrays.asList(savedImagePaths.split(",")));
        }
    }

    // Save image paths to SharedPreferences
    public void saveImagePaths(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String savedImagePaths = String.join(",", imagePaths);
        editor.putString(IMAGES_KEY, savedImagePaths);
        editor.apply();
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public LiveData<ArrayList<String>> getCommentsLiveData() {
        if (commentsLiveData.getValue() == null) {
            commentsLiveData.setValue(new ArrayList<>());
        }
        return commentsLiveData;
    }

    public void addImagePath(String imagePath) {
        imagePaths.add(imagePath);
        Log.d("GalleryViewModel", "Image path added. Total images: " + imagePaths.size());
    }

    public void addComment(String comment) {
        commentsLiveData.getValue().add(comment);
        commentsLiveData.setValue(new ArrayList<>(commentsLiveData.getValue()));
        Log.d("GalleryViewModel", "Comment added. Total comments: " + commentsLiveData.getValue().size());
    }

    public void setComments(ArrayList<String> comments) {
        commentsLiveData.setValue(comments);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // ViewModel is destroyed
        imagePaths.clear();
        commentsLiveData.setValue(new ArrayList<>());
    }
}