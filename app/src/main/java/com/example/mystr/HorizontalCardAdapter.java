package com.example.mystr;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

 public class HorizontalCardAdapter extends RecyclerView.Adapter<HorizontalCardAdapter.ViewHolder> {

    private Context mContext;
    private List<Story> mStories;
    private Timer mTimer;
    private int mCurrentPosition;

    public HorizontalCardAdapter(Context context, List<Story> stories) {
        mContext = context;
        mStories = stories;
        mCurrentPosition = 0;
        startAutoScroll();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView, descriptionTextView, authorTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story story = mStories.get(position);
        holder.titleTextView.setText(story.getTitle());
        holder.descriptionTextView.setText(story.getType());
        holder.authorTextView.setText(story.getAuthor());

        // Load image using Picasso
        Picasso.get().load(story.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.imageView);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // Navigate to new activity
            Intent intent = new Intent(mContext, StoryViewActivity.class);
            intent.putExtra("storyId", story.getStoryid());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    private void startAutoScroll() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (mCurrentPosition == mStories.size() - 1) {
                        mCurrentPosition = 0;
                    } else {
                        mCurrentPosition++;
                    }
                    notifyDataSetChanged();
                });
            }
        }, 5000, 5000); // Auto scroll every 5 seconds
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mTimer.cancel();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        startAutoScroll();
    }
}
