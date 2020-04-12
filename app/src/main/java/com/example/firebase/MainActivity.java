package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;

    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_contact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        firebaseFirestore = FirebaseFirestore.getInstance();

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        getData();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ActivityAddContact.class));
            }
        });
    }

    private void getData() {
        Query query = firebaseFirestore.collection("Contacts");

        FirestoreRecyclerOptions<ClassContact> response = new FirestoreRecyclerOptions.Builder<ClassContact>()
                .setQuery(query, ClassContact.class).build();

        adapter = new FirestoreRecyclerAdapter<ClassContact, ContactsHolder>(response) {

            @NonNull
            @Override
            public ContactsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_class_contact, parent, false);
                return new ContactsHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContactsHolder holder, int position, @NonNull final ClassContact model) {
                progressBar.setVisibility(View.GONE);
                if( model.getFoto() != null) {
                    Picasso.get().load(model.getFoto()).fit().into(holder.fotoContact);
                }else{
                    Picasso.get().load(R.drawable.icon_contact).fit().into(holder.fotoContact);
                }
                holder.namaContact.setText(model.getNama());
                holder.teleponContact.setText(model.getTelepon());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ActivityDetailContact.class);
                        intent.putExtra("telepon", model.getTelepon());
                        startActivity(intent);

                        //Snackbar.make(recyclerView, model.getNama()+", " +model.getTelepon(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                Log.e("Ditemukan Error: ", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public class ContactsHolder extends RecyclerView.ViewHolder{

        CircleImageView fotoContact;
        TextView namaContact;
        TextView teleponContact;
        ConstraintLayout constraintLayout;

        public ContactsHolder(@NonNull View itemView) {
            super(itemView);
            fotoContact = itemView.findViewById(R.id.imageViewFoto);
            namaContact = itemView.findViewById(R.id.textViewNama);
            teleponContact = itemView.findViewById(R.id.textViewTelepon);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}