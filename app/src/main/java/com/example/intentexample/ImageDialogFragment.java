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

    private static final String ARG_POSITION = "position";
    private static final String ARG_IMAGE_PATHS = "image_paths";
    private static final String ARG_COMMENTS = "comments";

    private int position;
    private ArrayList<Bitmap> images;
    private ArrayList<String> comments;
    private ArrayList<String> imagePaths;


    private static final String PREF_NAME = "MyPrefs";
    private static final String PREF_COMMENTS_KEY_PREFIX = "Comments_";

    private SharedPreferences sharedPreferences;
    private String imageKey;

    public static ImageDialogFragment newInstance(int position, ArrayList<String> imagePaths, ArrayList<String> comments) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putStringArrayList(ARG_IMAGE_PATHS, imagePaths); // Updated
        args.putStringArrayList(ARG_COMMENTS, comments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            imagePaths = getArguments().getStringArrayList(ARG_IMAGE_PATHS);

            // 이미지에 대한 고유한 키 생성
            imageKey = PREF_COMMENTS_KEY_PREFIX + position;

            // SharedPreferences 초기화
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

        String clickedImagePath = imagePaths.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(clickedImagePath);
        dialogPhotoView.setImageBitmap(bitmap);

        addExistingComments(layoutComments, comments);

        addButton.setOnClickListener(v -> {
            String commentText = editTextSchedule.getText().toString();
            if (!commentText.isEmpty()) {
                addComment(layoutComments, commentText);
                editTextSchedule.setText("");

                comments.add(commentText);

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
        TextView commentTextView = new TextView(requireContext());
        commentTextView.setText(commentText);
        commentTextView.setTextColor(Color.WHITE);
        commentTextView.setTextSize(16);
        commentTextView.setBackgroundResource(R.drawable.rounded_box);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 0, 50, 16);
        commentTextView.setLayoutParams(params);

        layoutComments.addView(commentTextView);
    }

    private void addExistingComments(LinearLayout layoutComments, ArrayList<String> comments) {
        for (String commentText : comments) {
            addComment(layoutComments, commentText);
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
}