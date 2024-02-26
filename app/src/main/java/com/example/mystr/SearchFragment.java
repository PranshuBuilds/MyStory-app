package com.example.mystr;

import static android.content.ContentValues.TAG;
import static com.example.mystr.MainActivity.mStoriesRef;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements StoryAdapter.OnItemClickListener, ButtonAdapter.OnButtonClickListener{
    private EditText searchEditText;
    private Button actionButton;
    private RecyclerView recyclerView;
    private StoryAdapter adapter;
    private List<Story> stories;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        searchEditText = root.findViewById(R.id.searchEditText);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stories = new ArrayList<>();
        adapter = new StoryAdapter(stories,2,this);
        recyclerView.setAdapter(adapter);

        List<String> buttonList = new ArrayList<>();
        buttonList.add("Action");
        buttonList.add("Drama");
        buttonList.add("Adventure");
        buttonList.add("Comedy");
        buttonList.add("Horror");
        buttonList.add("Emotional");
        buttonList.add("Divine");
        // Set up RecyclerView for buttons
        RecyclerView recyclerViewButtons = root.findViewById(R.id.searchViewButtons);
        ButtonAdapter buttonAdapter = new ButtonAdapter(buttonList, this);
        LinearLayoutManager layoutManagerButtons = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewButtons.setLayoutManager(layoutManagerButtons);
        recyclerViewButtons.setAdapter(buttonAdapter);

        // Set up search edit text listener
        searchEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                // Filter stories by search query
                filterStoriesByTitle(query);
                return true;
            }
            return false;
        });

        // Fetch all stories from Firebase
        fetchAllStories();
        return  root;
    }
    private void fetchAllStories() {

        mStoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    stories.add(story);
                }

                // Notify the adapter of the dataset change
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch stories: " + databaseError.getMessage());
                Toast.makeText(getContext(), "An error occurred while fetching stories. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void filterStories(String genre) {
        List<Story> filteredList = new ArrayList<>();
        for (Story story : stories) {
            if (story.getType().equalsIgnoreCase(genre)) {
                filteredList.add(story);
            }
        } adapter.setStories(filteredList);
    }

    private void filterStoriesByTitle(String query) {
        // Query for stories with titles containing the search query
        Query titleQuery = mStoriesRef.orderByChild("title").startAt(query).endAt(query + "\uf8ff");

        titleQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Story> filteredStories = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    filteredStories.add(story);
                }
                // Update the RecyclerView with filtered stories
                adapter.setStories(filteredStories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to filter stories by title: " + databaseError.getMessage());
                Toast.makeText(getContext(), "An error occurred while filtering stories. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onItemClick(Story story) {
        Intent intent = new Intent(getContext(), StoryViewActivity.class);
        intent.putExtra("storyId", story.getStoryid());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(int position) {

        switch (position) {
            case 0:
                filterStories("Action");
                break;
            case 1:
                filterStories("shiv");
                break;
            case 2:
                filterStories("ram");
                break;
            case 6:
                filterStories("divine");
                break;
            default:
                break;
        }

    }
}