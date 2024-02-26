package com.example.mystr;

import static com.example.mystr.MainActivity.mStoriesRef;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ViewStoryFragment extends Fragment implements StoryAdapter.OnItemClickListener,ButtonAdapter.OnButtonClickListener {

    private FirebaseDBHelper  mFirebaseDBHelper = new FirebaseDBHelper();
    private RecyclerView stackrecycler;
    private HorizontalCardAdapter stackadapter;
    private List<Story> stackdataList;
    private View fadingOverlay;
    private Handler mHandler;
    private int mCurrentPosition = 0;
    private List<Story> mdataList = new ArrayList<>();
    StoryAdapter storyAdapter;
    RecyclerView recyclerView;
    public ViewStoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_view_story, container, false);
        // Show fading overlay
        fadingOverlay = root.findViewById(R.id.fadingOverlay);

        fadingOverlay.setVisibility(View.VISIBLE);
// Fetch quotes from Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://mystr-c8e64-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("quotes");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> quotes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String quote = snapshot.getValue(String.class);
                    quotes.add(quote);
                }
                // Set up RecyclerView with fetched quotes
                RecyclerView recyclerView = root.findViewById(R.id.quoteRecycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                QuoteAdapter adapter = new QuoteAdapter(quotes);
                recyclerView.setAdapter(adapter);
                fadingOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
        stackrecycler = root.findViewById(R.id.stackRecycler);
        stackdataList = new ArrayList<>();
        stackrecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        stackrecycler.setLayoutManager(layoutManager);
        stackadapter =new HorizontalCardAdapter(getContext(), stackdataList);
        stackrecycler.setAdapter(stackadapter );

        // Add your data to the list
        Query query = mStoriesRef.orderByChild("likes").limitToLast(7);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Story story = snapshot.getValue(Story.class);
                        if (story != null) {
                            // Handle the most liked story
                            stackdataList.add(story);
                            Log.d("Most Liked Story", "Title: " + story.getTitle() + ", Likes: " + story.getLikes());
                        }
                        stackadapter.notifyDataSetChanged();
                    }
                } else {
                    // No stories found
                    Log.d("Most Liked Story", "no story");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error occurred while fetching data
            }
        });

        // Initialize the Handler
        mHandler = new Handler(Looper.getMainLooper());

        // Start automatic scrolling
        startAutomaticScrolling();


        // Initialize button and data lists
        List<String> buttonList = new ArrayList<>();
        buttonList.add("All");
        buttonList.add("Action");
        buttonList.add("Drama");
        buttonList.add("Adventure");
        buttonList.add("Comedy");
        buttonList.add("Horror");
        buttonList.add("Emotional");
        buttonList.add("Divine");
        // Set up RecyclerView for buttons
        RecyclerView recyclerViewButtons = root.findViewById(R.id.recyclerViewButtons);
        ButtonAdapter buttonAdapter = new ButtonAdapter(buttonList, this);
        LinearLayoutManager layoutManagerButtons = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewButtons.setLayoutManager(layoutManagerButtons);
        recyclerViewButtons.setAdapter(buttonAdapter);
// Initialize RecyclerView
        // Initialize RecyclerView
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchall();

        return root;
    }

    void fetchall(){
        mStoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Stories node exists, fetch stories
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    mdataList.add(story);
                }
                // Initialize adapter with listener
                storyAdapter = new StoryAdapter(mdataList,2,ViewStoryFragment.this);
                recyclerView.setAdapter(storyAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch stories: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void startAutomaticScrolling() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Scroll to the next item
                if (mCurrentPosition < stackadapter.getItemCount() - 1) {
                    mCurrentPosition++;
                } else {
                    mCurrentPosition = 0; // Start from the beginning
                }
                stackrecycler.smoothScrollToPosition(mCurrentPosition);

                // Schedule the next scroll after 5 seconds
                mHandler.postDelayed(this, 5000);
            }
        }, 5000); // Start after 5 seconds
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to prevent memory leaks
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onButtonClick(int position) {
        switch (position) {
            case 0:
                filterStories(" ");
                break;
            case 1:
                filterStories("Action");
                break;
            case 2:
                filterStories("shiv");
                break;
            case 7:
                filterStories("divine");
                break;
            default:
                break;
        }
    }
    private void filterStories(String genre) {
        List<Story> filteredList = new ArrayList<>();
        for (Story story : mdataList) {
            if(genre.equalsIgnoreCase(" ")){
                filteredList.add(story);
            }
            else if (story.getType().equalsIgnoreCase(genre)) {
                filteredList.add(story);
            }
        } storyAdapter.setStories(filteredList);
    }
    public void onItemClick(Story story) {
        // Handle item click here, for example, start a new activity with the selected story
        Intent intent = new Intent(getContext(), StoryViewActivity.class);
        intent.putExtra("storyId", story.getStoryid());
        startActivity(intent);
    }
}