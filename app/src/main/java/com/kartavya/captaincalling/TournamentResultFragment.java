package com.kartavya.captaincalling;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kartavya.captaincalling.R;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class TournamentResultFragment extends Fragment {

    private EditText resultTextView;
    private Button uploadButton;
    private FirebaseFirestore db;




    public TournamentResultFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tournament_result, container, false);
        resultTextView = view.findViewById(R.id.result_text_view);
        uploadButton = view.findViewById(R.id.upload_button);

        // Assuming tournamentId is obtained or set somewhere in your app
        String tournamentId = Paper.book().read("TournamentKey");

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadResultToFirebase(tournamentId); // Pass the tournamentId
            }
        });

        return view;
    }

    private void uploadResultToFirebase(String tournamentId) {
        String result = resultTextView.getText().toString().trim();

        // Get a reference to the specific tournament node in the database
        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference()
                .child("tournaments")
                .child(tournamentId)
                .child("Result");


        // Create a map to hold the result data
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("result", result);

        // Set the result data to the tournament node in the database
        tournamentRef.setValue(resultData)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}