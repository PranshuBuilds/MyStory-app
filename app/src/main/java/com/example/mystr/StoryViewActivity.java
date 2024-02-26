package com.example.mystr;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Locale;

public class StoryViewActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView storyTextView;
    private ImageView mImageView;
    TextToSpeech tts;
    private boolean isTtsSpeaking = false;
    private String mstory;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_story_view);
        // Initialize views
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        TextView likesTextView = findViewById(R.id.likesTextView);
        storyTextView = findViewById(R.id.storyTextView);
        mImageView = findViewById(R.id.profileImageView);
        Button shareButton = findViewById(R.id.shareButton);
        Button speakButton = findViewById(R.id.ttsButton);
        tts = new TextToSpeech(this, this);

        // Get storyId from intent
        String storyId = getIntent().getStringExtra("storyId");

        // Fetch specific story from Firebase
        DatabaseReference storyRef = FirebaseDatabase.getInstance("https://mystr-c8e64-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("stories").child(storyId);
        storyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if story exists
                if (dataSnapshot.exists()) {
                    // Get story object
                    Story story = dataSnapshot.getValue(Story.class);
                    // Display story details
                    if (story != null) {
                        titleTextView.setText(story.getTitle());
                        emailTextView.setText(story.getAuthor());
                        likesTextView.setText("likes: "+story.getLikes());

                        mstory=story.getDescription().replace("\\n","\n");
                        storyTextView.setText(mstory);
                        Picasso.get().load(story.getImageUrl()).into(mImageView);

                    }
                } else {
                    // Story does not exist
//                    storyDetailsTextView.setText("Story not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(StoryViewActivity.this, "Failed to fetch story details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Share button click listener
        shareButton.setOnClickListener(v -> {
            // Implement share functionality
        });

        // Speech button click listener
        speakButton.setOnClickListener(v -> {
            // Implement tts functionality
            if (isTtsSpeaking) {
                // Stop TTS
                if (tts != null) {
                    tts.stop();
                    isTtsSpeaking = false;
                    speakButton.setText("Speak");
                    storyTextView.setTextColor(getResources().getColor(R.color.white));

                }
            } else {
                // Start TTS
                if (tts != null) {
                    storyTextView.setTextColor(getResources().getColor(R.color.pp2));
                    startClicked(storyTextView);
                    isTtsSpeaking = true;
                    speakButton.setText("Stop");
                }}
        });
//        new TTSUrlExtractor(this).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TextToSpeech.OnInitListener (for our purposes, the "main method" of this activity)
    public void onInit(int status) {

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
                Log.i("XXX", "utterance started");
            }

            @Override
            public void onDone(String utteranceId) {
                Log.i("XXX", "utterance done");
                storyTextView.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onError(String utteranceId) {
                Log.i("XXX", "utterance error");
            }

            @Override
            public void onRangeStart(String utteranceId,
                                     final int start,
                                     final int end,
                                     int frame) {
                Log.i("XXX", "onRangeStart() ... utteranceId: " + utteranceId + ", start: " + start
                        + ", end: " + end + ", frame: " + frame);

                // onRangeStart (and all UtteranceProgressListener callbacks) do not run on main thread
                // ... so we explicitly manipulate views on the main thread:
                runOnUiThread(() -> {

                    Spannable textWithHighlights = new SpannableString(mstory);
                    textWithHighlights.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    storyTextView.setText(textWithHighlights);
                });
            }
        });
    }

    public void startClicked(View ignored) {

        tts.speak(mstory, TextToSpeech.QUEUE_FLUSH, null, "doesn't matter yet");

    }

    @Override
    public void onBackPressed() {
        if (tts != null) {
            tts.stop();
        }
        super.onBackPressed();
        finish();
    }

//    @Override
//    public void onTTSUrlExtracted(String ttsUrl) {
//        Log.i("url for audio",ttsUrl);
//    }
//
//    @Override
//    public void onError(Exception e) {
//        Log.i("url error",e.toString());
//    }

}