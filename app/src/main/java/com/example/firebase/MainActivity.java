package com.example.firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.databinding.ActivityMainBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_contact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(
                binding.recyclerView.getContext(),
                lm.getOrientation()));
        binding.recyclerView.setLayoutManager(lm);

        binding.floatingActionButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ActivityAddContact.class)));

        getData();
    }

    private void getData() {
        Query query = firebaseFirestore.collection("Contacts");

        FirestoreRecyclerOptions<ClassContact> response = new FirestoreRecyclerOptions.Builder<ClassContact>()
                .setQuery(query, ClassContact.class).build();

        adapter = new FirestoreRecyclerAdapter<ClassContact, ContactsHolder>(response) {

            @NonNull
            @Override
            public ContactsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_contact, parent, false);
                return new ContactsHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContactsHolder holder, int position, @NonNull final ClassContact model) {
                binding.progressBar.setVisibility(View.GONE);
                if (model.getFoto() != null) {
                    Picasso.get().load(model.getFoto()).fit().into(holder.photoContact);
                } else {
                    Picasso.get().load(R.drawable.icon_contact).fit().into(holder.photoContact);
                }
                holder.nameContact.setText(model.getNama());
                holder.phoneContact.setText(model.getTelepon());

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ActivityDetailContact.class);
                    intent.putExtra("telepon", model.getTelepon());
                    startActivity(intent);
                });
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                Log.e("Error Found: ", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        binding.recyclerView.setAdapter(adapter);
    }

    public static class ContactsHolder extends RecyclerView.ViewHolder {
        CircleImageView photoContact;
        TextView nameContact;
        TextView phoneContact;
        ConstraintLayout constraintLayout;

        public ContactsHolder(@NonNull View itemView) {
            super(itemView);
            photoContact = itemView.findViewById(R.id.image_Photo);
            nameContact = itemView.findViewById(R.id.tv_Name);
            phoneContact = itemView.findViewById(R.id.tv_Phone);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }

    public static class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "meet a I.O.O.B.E in RecyclerView");
            }
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