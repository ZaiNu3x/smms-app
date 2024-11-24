package group.intelliboys.smms.fragments.profile_update;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import group.intelliboys.smms.R;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.security.SecurityContextHolder;
import group.intelliboys.smms.utils.converters.ImageConverter;
import group.intelliboys.smms.utils.ServerAPIs;
import lombok.Getter;

public class ProfileFragment extends Fragment {
    private CircleImageView profilePicView;
    private TextView usernameTxtView;
    private TextView emailTxtView;
    private EditText lastNameEditTxt;
    private EditText firstNameEditTxt;
    private EditText middleNameEditTxt;
    private RadioButton maleRadioBtn;
    private RadioButton femaleRadioBtn;
    private EditText birthDateEditTxt;
    private EditText addressEditTxt;

    @Getter
    private ProgressBar loadingIndicator;

    @Getter
    private Button saveButton;

    private User authenticatedUser;
    private ServerAPIs serverAPIs;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePicView = view.findViewById(R.id.userProfilePic);
        usernameTxtView = view.findViewById(R.id.userDisplayName);
        emailTxtView = view.findViewById(R.id.userDisplayEmail);
        lastNameEditTxt = view.findViewById(R.id.editLastName);
        firstNameEditTxt = view.findViewById(R.id.editFirstName);
        middleNameEditTxt = view.findViewById(R.id.editMiddleName);
        maleRadioBtn = view.findViewById(R.id.male);
        femaleRadioBtn = view.findViewById(R.id.female);
        birthDateEditTxt = view.findViewById(R.id.userBirthDate);
        addressEditTxt = view.findViewById(R.id.userAddress);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);

        profilePicView.setOnClickListener(v -> {
            openImagePicker();
        });

        saveButton = view.findViewById(R.id.saveButton);

        showUserInfo();
        serverAPIs = new ServerAPIs(requireActivity());

        saveButton.setOnClickListener(v -> {
            saveButton.setEnabled(false);
            loadingIndicator.setVisibility(View.VISIBLE);

            String lastName = lastNameEditTxt.getText().toString();
            String firstName = firstNameEditTxt.getText().toString();
            String middleName = middleNameEditTxt.getText().toString();

            char sex = '\0';

            if (maleRadioBtn.isChecked()) {
                sex = 'm';
            } else if (femaleRadioBtn.isChecked()) {
                sex = 'f';
            }

            LocalDate birthDate = LocalDate.parse(birthDateEditTxt.getText().toString());
            String address = addressEditTxt.getText().toString();

            Drawable drawable = profilePicView.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            byte[] profilePic = ImageConverter.bitmapToByteArray(bitmap);

            try {
                JSONObject profile = new JSONObject();

                profile.put("email", authenticatedUser.getEmail());
                profile.put("lastName", lastName);
                profile.put("firstName", firstName);
                profile.put("middleName", middleName);
                profile.put("sex", sex);
                profile.put("birthDate", birthDate);
                profile.put("address", address);
                profile.put("profilePic", Base64.getEncoder()
                        .encodeToString(profilePic));

                serverAPIs.updateUserProfileInfo(profile);
            } catch (Exception e) {
                loadingIndicator.setVisibility(View.INVISIBLE);
                saveButton.setEnabled(true);
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }
        });

        return view;
    }

    public void showUserInfo() {
        authenticatedUser = SecurityContextHolder.getInstance()
                .getAuthenticatedUser();

        StringBuilder builder = new StringBuilder();
        builder.append(authenticatedUser.getLastName()).append(", ")
                .append(authenticatedUser.getFirstName());

        profilePicView.setImageBitmap(ImageConverter.byteArrayToBitmap(authenticatedUser.getProfilePic()));
        usernameTxtView.setText(new String(builder));
        emailTxtView.setText(authenticatedUser.getEmail());
        lastNameEditTxt.setText(authenticatedUser.getLastName());
        firstNameEditTxt.setText(authenticatedUser.getFirstName());
        middleNameEditTxt.setText(authenticatedUser.getMiddleName());

        if (authenticatedUser.getSex() == 'm') {
            maleRadioBtn.setChecked(true);
        } else if (authenticatedUser.getSex() == 'f') {
            femaleRadioBtn.setChecked(true);
        }

        birthDateEditTxt.setText(authenticatedUser.getBirthDate().toString());
        addressEditTxt.setText(authenticatedUser.getAddress());
    }

    public void refreshUserInfo() {
        showUserInfo();
    }

    private static final int PICK_IMAGE_REQUEST = 1;
    private byte[] profilePicData;

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                profilePicData = ImageConverter.bitmapToByteArray(bitmap);
                profilePicView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }
        }
    }
}
