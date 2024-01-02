package com.example.intentexample;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;

public class GalleryViewModel extends ViewModel {
    private static final String PREFS_NAME = "GalleryAppPreferences";
    private static final String IMAGES_KEY = "SavedImagePaths";

    private ArrayList<String> imagePaths = new ArrayList<>();
    private HashMap<String, ArrayList<String>> imageComments = new HashMap<>();
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
    public void removeImagePath(int position) {
        if (position >= 0 && position < imagePaths.size()) {
            String imagePath = imagePaths.remove(position);
            imageComments.remove(imagePath); // Remove comments associated with the image
        }
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public ArrayList<String> getComments(String imagePath) {
        return imageComments.getOrDefault(imagePath, new ArrayList<>());
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

    public void addComment(String imagePath, String comment) {
        if (!imageComments.containsKey(imagePath)) {
            imageComments.put(imagePath, new ArrayList<>());
        }
        imageComments.get(imagePath).add(comment);
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