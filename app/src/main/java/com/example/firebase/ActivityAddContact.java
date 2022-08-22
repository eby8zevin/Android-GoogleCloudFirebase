package com.example.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebase.databinding.ActivityAddContactBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ActivityAddContact extends AppCompatActivity {

    private ActivityAddContactBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private Uri filePath;
    private String photoUrl;

    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setVisibility(View.INVISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.imgPhoto.setOnClickListener(v -> takeImage());

        binding.btnAdd.setOnClickListener(v -> uploadImage());
        binding.buttonBack.setOnClickListener(v -> finish());
    }

    private void saveData(String nama, String telepon, String sosmed, String alamat, String foto) {

        Map<String, Object> contactData = new HashMap<>();

        contactData.put("nama", nama);
        contactData.put("telepon", telepon);
        contactData.put("sosmed", sosmed);
        contactData.put("alamat", alamat);
        contactData.put("foto", foto);

        firebaseFirestore.collection("Contacts").document(telepon).set(contactData).isSuccessful();
    }

    private void takeImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Picasso.get().load(filePath).fit().centerInside().into(binding.imgPhoto);
        } else {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            final StorageReference ref = storageReference.child(binding.etPhone.getText().toString());
            UploadTask uploadTask = ref.putFile(filePath);

            Task<Uri> uriTask = uploadTask.continueWithTask(task -> ref.getDownloadUrl()).addOnCompleteListener(task -> {
                Uri imagePath = task.getResult();

                photoUrl = imagePath.toString();
                saveData(binding.etName.getText().toString(),
                        binding.etPhone.getText().toString(),
                        binding.etSocialMedia.getText().toString(),
                        binding.etSocialMedia.getText().toString(),
                        photoUrl);

                binding.progressBar.setProgress(0);
                binding.progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ActivityAddContact.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });

            uploadTask.addOnProgressListener(taskSnapshot -> {
                binding.progressBar.setVisibility(View.VISIBLE);
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                binding.progressBar.setProgress((int) progress);
            }).addOnFailureListener(e -> {
                binding.progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ActivityAddContact.this, "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }
}