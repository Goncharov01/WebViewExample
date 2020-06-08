package com.example.webviewexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SecondActivity extends AppCompatActivity {

    TextView textView;
    String secret = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textView = findViewById(R.id.textSecret);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbSecret = database.getReference("secret");

        dbSecret.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                secret = dataSnapshot.getValue(String.class);
                textView.setText(secret);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }
}
