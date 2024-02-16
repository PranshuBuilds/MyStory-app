package com.example.mystr;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDBHelper {

    private DatabaseReference mDatabase;

    public FirebaseDBHelper() {
        mDatabase = FirebaseDatabase.getInstance("https://mystr-c8e64-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    }

    public void getStoriesByType(String type, ValueEventListener valueEventListener) {
        Query query = mDatabase.child("stories").orderByChild("type").equalTo(type);
        query.addListenerForSingleValueEvent(valueEventListener);
    }
}
