package com.kartavya.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class ViewTournamentResultActivity extends AppCompatActivity {

    private TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tournament_result);

        resultTextView = findViewById(R.id.result_text_view);

        // Retrieve tournament key from Paper
        String tournamentKey = Paper.book().read("TournamentKey");

        // Get a reference to the specific tournament result node in Firebase
        DatabaseReference tournamentResultRef = FirebaseDatabase.getInstance().getReference()
                .child("tournaments")
                .child(tournamentKey)
                .child("Result")
                .child("result");

        // Read the result data from Firebase
        tournamentResultRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the result data exists
                if (dataSnapshot.exists()) {
                    String result = dataSnapshot.getValue(String.class);
                    // Display the result in the TextView
                    resultTextView.setText("Result: " + result);
                } else {
                    // Handle case where no result data is available
                    resultTextView.setText("Result not available");
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(ViewTournamentResultActivity.this, "Error retrieving result data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}