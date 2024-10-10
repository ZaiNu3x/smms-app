package group.intelliboys.smms.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import group.intelliboys.smms.R;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ClubCreationActivity extends AppCompatActivity {

    private EditText clubNameEditText, clubThemeEditText;
    private ImageView clubLogoImageView;
    private Button chooseLogoButton, createClubButton;
    private Bitmap clubLogoBitmap;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_club);

        clubNameEditText = findViewById(R.id.clubName);
        clubThemeEditText = findViewById(R.id.clubTheme);
        clubLogoImageView = findViewById(R.id.clubLogo);
        chooseLogoButton = findViewById(R.id.chooseLogoButton);
        createClubButton = findViewById(R.id.createClubButton);

        // Initialize the ActivityResultLauncher for handling the image pick result
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            clubLogoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            clubLogoImageView.setImageBitmap(clubLogoBitmap);
                            clubLogoImageView.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            Log.e("ClubCreationActivity", "Error getting image", e);
                        }
                    }
                });

        chooseLogoButton.setOnClickListener(v -> openGallery());

        createClubButton.setOnClickListener(v -> {
            String clubName = clubNameEditText.getText().toString();
            String clubTheme = clubThemeEditText.getText().toString();

            if (clubName.isEmpty() || clubTheme.isEmpty() || clubLogoBitmap == null) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            } else {
                // Save club details to database or send to server
                Toast.makeText(this, "Club created successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
}
