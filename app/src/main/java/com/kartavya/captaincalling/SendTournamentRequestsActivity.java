package com.kartavya.captaincalling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class SendTournamentRequestsActivity extends AppCompatActivity {
    ImageView backButton;
    RecyclerView recyclerView;
    DatabaseReference sendTournamentRef;
    FirebaseRecyclerAdapter<SendTournamentRequests, SendTournamentRequestsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_tournament_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.back_send_tournament_request);
        recyclerView = findViewById(R.id.send_tournament_recycler_view);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        sendTournamentRef = FirebaseDatabase.getInstance().getReference()
                        .child("AllTeam");

        loadTeams();
    }

    private void loadTeams(){
        FirebaseRecyclerOptions<SendTournamentRequests> options = new FirebaseRecyclerOptions.Builder<SendTournamentRequests>()
                .setQuery(sendTournamentRef, SendTournamentRequests.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<SendTournamentRequests, SendTournamentRequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SendTournamentRequestsViewHolder sendTournamentRequestsViewHolder, int i, @NonNull SendTournamentRequests sendTournamentRequests) {
                String teamKey = getRef(i).getKey();
                assert teamKey != null;

                DatabaseReference sendTournamentRef2 = FirebaseDatabase.getInstance().getReference()
                        .child("AllTeam")
                        .child(teamKey);

                sendTournamentRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String teamName = dataSnapshot.child("TeamName").getValue(String.class);
                            String teamCaptainName = dataSnapshot.child("Captain").getValue(String.class);
                            String teamSport = dataSnapshot.child("Sport").getValue(String.class);
                            String teamState = dataSnapshot.child("State").getValue(String.class);
                            String teamDistrict = dataSnapshot.child("District").getValue(String.class);
                            String teamAddress = dataSnapshot.child("Address").getValue(String.class);
                            String teamLevel = dataSnapshot.child("Level").getValue(String.class);

                            sendTournamentRequestsViewHolder.sendTournamentTeamName.setText(teamName);
                            sendTournamentRequestsViewHolder.sendTournamentTeamCaptianName.setText(teamCaptainName);
                            sendTournamentRequestsViewHolder.sendTournamentSport.setText(teamSport);
                            sendTournamentRequestsViewHolder.sendTournamentState.setText(teamState);
                            sendTournamentRequestsViewHolder.sendTournamentDistrict.setText(teamDistrict);
                            sendTournamentRequestsViewHolder.sendTournamentAddress.setText(teamAddress);
                            sendTournamentRequestsViewHolder.sendTournamentLevel.setText(teamLevel);

                            sendTournamentRequestsViewHolder.sendTournamentButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (teamName != null) {
                                        HashMap<String, Object> sendTournamentHashmap = new HashMap<>();

                                        sendTournamentHashmap.put("TeamName", teamName);
                                        sendTournamentHashmap.put("TeamCaptainName", teamCaptainName);
                                        sendTournamentHashmap.put("TeamSport", teamSport);
                                        sendTournamentHashmap.put("TeamDistrict", teamDistrict);
                                        sendTournamentHashmap.put("TeamAddress", teamAddress);

                                        DatabaseReference sendTournamentRef3 = FirebaseDatabase.getInstance().getReference()
                                                .child("tournaments")
                                                .child(Paper.book().read("TournamentKey"))
                                                .child("Requests")
                                                .child("Sent");

                                        // Add the request to Firebase
                                        sendTournamentRef3.child(teamName).setValue(sendTournamentHashmap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(SendTournamentRequestsActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SendTournamentRequestsActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(SendTournamentRequestsActivity.this, "Team name is null", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public SendTournamentRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(SendTournamentRequestsActivity.this).inflate(R.layout.send_request_tournament_model,viewGroup, false);
                return new SendTournamentRequestsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}