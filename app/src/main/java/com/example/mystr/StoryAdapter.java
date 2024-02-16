package com.example.mystr;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int mViewType;

    private List<Story> mStories;
    private OnItemClickListener mListener;
    private static final int VIEW_TYPE_GRID = 1;
    private static final int VIEW_TYPE_LIST= 2;

    // Your other adapter methods and variables here

    @Override
    public int getItemViewType(int position) {
        // Return the view type based on the position or any other logic you have
        // For example:
        return mViewType;
    }
    public interface OnItemClickListener {
        void onItemClick(Story story);
    }

    public void setStories(List<Story> stories) {
        this.mStories = stories;
        notifyDataSetChanged();
    }
    public static class ViewHolderGrid extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView, descriptionTextView, typeTextView, authorTextView;

        public ViewHolderGrid(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageviewitem);
            titleTextView = itemView.findViewById(R.id.texttitle);
            descriptionTextView = itemView.findViewById(R.id.textlikes);
            typeTextView = itemView.findViewById(R.id.texttype);
            authorTextView = itemView.findViewById(R.id.textauth);
        }
    }
    public static class ViewHolderList extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView, descriptionTextView, typeTextView, authorTextView;
        public LinearLayout linearLayout;
        public ViewHolderList(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageviewitem);
            titleTextView = itemView.findViewById(R.id.texttitle);
            descriptionTextView = itemView.findViewById(R.id.textgetlikes);
            typeTextView = itemView.findViewById(R.id.texttype);
            authorTextView = itemView.findViewById(R.id.textauth);
            linearLayout=itemView.findViewById(R.id.linerlay);
        }
    }

    public StoryAdapter(List<Story> stories, int viewType, OnItemClickListener listener) {
        mStories = stories;
        mViewType = viewType;
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_GRID:
                view = inflater.inflate(R.layout.item_grid_layout, parent, false);
                return new ViewHolderGrid(view);
            case VIEW_TYPE_LIST:
                view = inflater.inflate(R.layout.item_list_layout, parent, false);
                return new ViewHolderList(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Story story = mStories.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_GRID:
                ViewHolderGrid viewHolderOne = (ViewHolderGrid) holder;
                viewHolderOne.titleTextView.setText(story.getTitle());
                viewHolderOne.descriptionTextView.setText("Likes • " +story.getLikes());
                viewHolderOne.typeTextView.setText("Genre • "+story.getType());
                viewHolderOne.authorTextView.setText(story.getAuthor());

                // Load image using Picasso or Glide
                Picasso.get().load(story.getImageUrl()).into(viewHolderOne.imageView);

                viewHolderOne.itemView.setOnClickListener(v -> {
                    if (mListener != null) {
                        mListener.onItemClick(story);
                    }
                });
                break;
            case VIEW_TYPE_LIST:
                ViewHolderList viewHoldertwo = (ViewHolderList) holder;
                viewHoldertwo.titleTextView.setText(story.getTitle());
                viewHoldertwo.descriptionTextView.setText("Likes • "+ story.getLikes());
                viewHoldertwo.typeTextView.setText("Genre • "+story.getType());
                viewHoldertwo.authorTextView.setText(story.getAuthor());

                // Load image using Picasso or Glide
                Picasso.get().load(story.getImageUrl()).into(viewHoldertwo.imageView);
                viewHoldertwo.itemView.setOnClickListener(v -> {
                    if (mListener != null) {
                        mListener.onItemClick(story);
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }
}



