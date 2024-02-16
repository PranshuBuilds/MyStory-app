package com.example.mystr;

import static com.example.mystr.MainActivity.mStoriesRef;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements StoryAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private StoryAdapter mAdapter;
    private Button floatingActionButton;
    private FirebaseAuth mAuth;
    private Button logoutButton;

    List<Story> stories = new ArrayList<>();
    int totalLikes = 0;
    int totalStories=0;
    private TextView mTextViewEmail;
    private TextView mTotalLikesTextView;
    private TextView mTotalStoriesTextView;
    private View fadingOverlay;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Show fading overlay
        fadingOverlay = view.findViewById(R.id.fadingOverlay);

        fadingOverlay.setVisibility(View.VISIBLE);
        floatingActionButton = view.findViewById(R.id.addStoryButton);
        floatingActionButton.setOnClickListener(v -> startActivity(new Intent(getContext(), WriteActivity.class)));

        mRecyclerView = view.findViewById(R.id.recyclerViewStories);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Fetch story data from Firebase and populate RecyclerView
        fetchStories();

        // Initialize views
        mTextViewEmail = view.findViewById(R.id.emailTextView);
        mTotalLikesTextView = view.findViewById(R.id.totalLikesTextView);
        mTotalStoriesTextView = view.findViewById(R.id.totalStoriesTextView);
        mAuth = FirebaseAuth.getInstance();
        logoutButton = view.findViewById(R.id.logoutButton);

        // Assuming mStories is the list of stories authored by the user

        logoutButton.setOnClickListener(v -> logoutUser());
        // Update TextViews
        mTextViewEmail.setText(getCurrentUsername());

        return view;
    }

    private void fetchStories() {
        mStoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Stories node exists, fetch stories
                stories = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);

                    if (story.getAuthor().equals(getCurrentUsername())) {
                        stories.add(story);
                        totalLikes += story.getLikes();
                        Log.i("message",story.getTitle()+totalLikes);
                    }
                }
                totalStories = stories.size();
                mTotalLikesTextView.setText(getString(R.string.total_likes, totalLikes));
                mTotalStoriesTextView.setText(getString(R.string.total_stories, totalStories));

                mAdapter = new StoryAdapter(stories,1,ProfileFragment.this::onItemClick);
                mRecyclerView.setAdapter(mAdapter);
                fadingOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch stories: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Method to get the current user's username from Firebase Auth
    private String getCurrentUsername() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getEmail();
        } else {
            return ""; // Return an empty string if user is not logged in
        }
    }
    private void logoutUser() {
        // Sign out the user
        mAuth.signOut();
        // Navigate to the login activity
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish(); // Close the current activity
    }
    @Override
    public void onItemClick(Story story) {

        // Handle item click here, for example, start a new activity with the selected story
        Intent intent = new Intent(getContext(), StoryViewActivity.class);
        intent.putExtra("storyId", story.getStoryid());
        startActivity(intent);
    }
}
