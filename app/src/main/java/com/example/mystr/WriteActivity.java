package com.example.mystr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WriteActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private ImageView imageView;
    private Uri imageUri;
    Button saveButton,generateButton;
    Spinner genreSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_write);

        titleEditText = findViewById(R.id.title);
        descriptionEditText = findViewById(R.id.discription);
        genreSpinner = findViewById(R.id.genreSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);
        imageView = findViewById(R.id.imageselect);

        saveButton = findViewById(R.id.uploadbutton);
        generateButton = findViewById(R.id.generatebutton);

        imageView.setOnClickListener(v -> pickImageFromGallery());
        generateButton.setOnClickListener(v ->{
            generateViaAI("generate full story on genre of divine and fantasy");

        });

        saveButton.setOnClickListener(v -> saveStory());
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void saveStory() {
        saveButton.setEnabled(false);
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String type = genreSpinner.getSelectedItem().toString();
        String author = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(type) || TextUtils.isEmpty(author)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if an image is selected
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Firebase Storage
        uploadImageToStorage(title, description, type, author);
    }

    private void uploadImageToStorage(String title, String description, String type, String author) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("story_images");

        // Generate a unique ID for the image
        String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(imageId);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Save story data with image URL to Firebase database
                        saveStoryData(title, description, type, author, imageUrl);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveStoryData(String title, String description, String type, String author, String imageUrl) {
        DatabaseReference storiesRef = FirebaseDatabase.getInstance("https://mystr-c8e64-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("stories");

        // Generate a unique ID for the story
        String storyId = storiesRef.push().getKey();
        if (storyId != null) {
            // Create a Story object with image URL
            Story story = new Story(storyId,title, description, type, author, imageUrl,0);

            // Save the story data in Firebase Realtime Database
            storiesRef.child(storyId).setValue(story)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getBaseContext(), "Story saved successfully", Toast.LENGTH_SHORT).show();
                        // Start a new activity to show stories
                        startActivity(new Intent(WriteActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getBaseContext(), "Failed to save story", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    public String generateViaAI(String text){
        final String[] resultText = new String[1];
        // For text-only input, use the gemini-pro model
        GenerativeModel gm = new GenerativeModel("gemini-pro", BuildConfig.apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(text)
                .build();
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                resultText[0] = result.getText();
                assert resultText[0] != null;
                Log.i("tag",resultText[0]);
                descriptionEditText.setText(resultText[0]);
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        return resultText[0];
    }
}




