package com.example.intentexample;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ImageDialogFragment extends DialogFragment {

    public interface CommentChangeListener {
        void onCommentChanged(ArrayList<String> updatedComments);
    }

    private CommentChangeListener commentChangeListener;
    private static final String ARG_COMMENTS = "comments";
    private ArrayList<String> comments;


    private static final String PREF_NAME = "MyPrefs";
    private static final String PREF_COMMENTS_KEY_PREFIX = "Comments_";

    private SharedPreferences sharedPreferences;
    private String imageKey;
    private float currentRotation = 0.0f;

    public static ImageDialogFragment newInstance(String imagePath, ArrayList<String> comments) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putString("imagePath", imagePath);
        args.putStringArrayList("comments", comments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            String imagePath = getArguments().getString("imagePath");
            comments = getArguments().getStringArrayList("comments");

            // Generate a unique key for SharedPreferences based on the image path
            imageKey = PREF_COMMENTS_KEY_PREFIX + imagePath.hashCode(); // Example

            sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            if (savedInstanceState != null) {
                comments = savedInstanceState.getStringArrayList(ARG_COMMENTS);
            } else {
                // 이미지에 대한 메모 불러오기
                comments = loadCommentsFromSharedPreferences(imageKey);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PhotoView dialogPhotoView = view.findViewById(R.id.dialog_photo_view);
        LinearLayout layoutComments = view.findViewById(R.id.layoutComments);
        EditText editTextSchedule = view.findViewById(R.id.editTextSchedule);
        ImageButton addButton = view.findViewById(R.id.addButton);
        ImageView closeButton = view.findViewById(R.id.close_button);

        currentRotation = getRotationFromSharedPreferences();
        dialogPhotoView.setRotation(currentRotation);
        ImageButton rotateClockwiseButton = view.findViewById(R.id.rotate_clockwise_button);
        ImageButton rotateCounterClockwiseButton = view.findViewById(R.id.rotate_counterclockwise_button);

        rotateClockwiseButton.setOnClickListener(v -> rotateImageClockwise(dialogPhotoView));
        rotateCounterClockwiseButton.setOnClickListener(v -> rotateImageCounterClockwise(dialogPhotoView));

        String clickedImagePath = getArguments().getString("imagePath");
        Bitmap bitmap = BitmapFactory.decodeFile(clickedImagePath);
        dialogPhotoView.setImageBitmap(bitmap);

        addExistingComments(layoutComments, comments);

        addButton.setOnClickListener(v -> {
            String newCommentText = editTextSchedule.getText().toString();
            if (!newCommentText.isEmpty()) {
                addComment(layoutComments, newCommentText);
                editTextSchedule.setText("");

                comments.add(newCommentText);
                notifyCommentChanged();
            }
        });

        closeButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .remove(ImageDialogFragment.this)
                    .addToBackStack(null)
                    .commit();
        });
    }
    private void rotateImageCounterClockwise(PhotoView photoView) {
        currentRotation = (currentRotation + 90) % 360;
        photoView.setRotation(currentRotation);
        saveRotationToSharedPreferences(currentRotation);
    }

    private void rotateImageClockwise(PhotoView photoView) {
        currentRotation = (currentRotation - 90) % 360;
        if (currentRotation < 0) currentRotation += 360;
        photoView.setRotation(currentRotation);
        saveRotationToSharedPreferences(currentRotation);
    }

    private void notifyCommentChanged() {
        if (commentChangeListener != null) {
            commentChangeListener.onCommentChanged(new ArrayList<>(comments));
        }

        // 이미지에 대한 메모 저장
        saveCommentsToSharedPreferences(imageKey, comments);
    }

    private void saveCommentsToSharedPreferences(String key, ArrayList<String> comments) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> commentSet = new HashSet<>(comments);
        editor.putStringSet(key, commentSet);
        editor.apply();
    }

    private ArrayList<String> loadCommentsFromSharedPreferences(String key) {
        Set<String> commentSet = sharedPreferences.getStringSet(key, new HashSet<>());
        return new ArrayList<>(commentSet);
    }

    private void addComment(LinearLayout layoutComments, String commentText) {
        LinearLayout commentLayout = new LinearLayout(requireContext());
        commentLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView commentTextView = new TextView(requireContext());
        commentTextView.setText(commentText);
        commentTextView.setTextColor(Color.WHITE);
        commentTextView.setTextSize(16);
        commentTextView.setBackgroundResource(R.drawable.rounded_box);

        ImageButton deleteButton = new ImageButton(requireContext());
        deleteButton.setImageResource(R.drawable.red_trash);
        deleteButton.setBackgroundColor(Color.TRANSPARENT);
        deleteButton.setOnClickListener(v -> deleteComment(commentLayout, commentText));

        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        textViewParams.setMargins(50, 0, 0, 16);
        commentTextView.setLayoutParams(textViewParams);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteButton.setLayoutParams(buttonParams);

        commentLayout.addView(commentTextView);
        commentLayout.addView(deleteButton);
        layoutComments.addView(commentLayout);
    }
    private void deleteComment(LinearLayout commentLayout, String commentText) {
        // Remove the comment view
        ((LinearLayout) commentLayout.getParent()).removeView(commentLayout);

        comments.remove(commentText);
        notifyCommentChanged();
    }

    private void addExistingComments(LinearLayout layoutComments, ArrayList<String> comments) {
        for (String existingCommentText : comments) {
            addComment(layoutComments, existingCommentText);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(ARG_COMMENTS, comments);
    }

    public void setCommentChangeListener(CommentChangeListener listener) {
        this.commentChangeListener = listener;
    }
    private void saveRotationToSharedPreferences(float rotation) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(imageKey + "_rotation", rotation);
        editor.apply();
    }

    private float getRotationFromSharedPreferences() {
        return sharedPreferences.getFloat(imageKey + "_rotation", 0.0f);
    }
}