package group.intelliboys.smms.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import de.hdodenhof.circleimageview.CircleImageView;
import group.intelliboys.smms.R;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.services.Utils;
import group.intelliboys.smms.services.local.LocalDbUserService;

public class ProfileFragment extends Fragment {

    private byte[] profilePicData;
    private CircleImageView profilePic;
    private ImageView changeProfilePic;
    private TextView userName;
    private TextView userEmail;
    private EditText lastName;
    private EditText firstName;
    private EditText middleName;
    private RadioButton male;
    private RadioButton female;
    private char sex;
    private EditText birthDate;
    private EditText address;
    private Button saveButton;
    private User loggedInUser;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = view.findViewById(R.id.userProfilePic);
        changeProfilePic = view.findViewById(R.id.changeProfilePic);
        userName = view.findViewById(R.id.userDisplayName);
        userEmail = view.findViewById(R.id.userDisplayEmail);
        lastName = view.findViewById(R.id.editLastName);
        firstName = view.findViewById(R.id.editFirstName);
        middleName = view.findViewById(R.id.editMiddleName);
        male = view.findViewById(R.id.male);
        female = view.findViewById(R.id.female);
        birthDate = view.findViewById(R.id.userBirthDate);
        address = view.findViewById(R.id.userAddress);
        saveButton = view.findViewById(R.id.saveButton);

        loggedInUser = Utils.getInstance().getLoggedInUser();

        if (loggedInUser != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(loggedInUser.getProfilePic(),
                    0, loggedInUser.getProfilePic().length);
            profilePic.setImageBitmap(bitmap);

            StringBuilder builder = new StringBuilder();
            builder.append(loggedInUser.getLastName())
                    .append(" ")
                    .append(loggedInUser.getFirstName()).append(" ")
                    .append(loggedInUser.getMiddleName().charAt(0))
                    .append(".");

            userName.setText(new String(builder));
            userEmail.setText(loggedInUser.getEmail());
            lastName.setText(loggedInUser.getLastName());
            firstName.setText(loggedInUser.getFirstName());
            middleName.setText(loggedInUser.getMiddleName());

            if (loggedInUser.getSex().equals("m")) {
                male.setChecked(true);
            } else if (loggedInUser.getSex().equals("f")) {
                female.setChecked(true);
            }

            birthDate.setText(loggedInUser.getBirthDate().toString());
            address.setText(loggedInUser.getAddress());
        }

        changeProfilePic.setOnClickListener((v) -> {
            openImagePicker();
        });

        saveButton.setOnClickListener((v) -> {
            try {
                save();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return view;
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Context context = Utils.getInstance().getApplicationContext();

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                profilePicData = readBytes(imageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            profilePic.setImageURI(imageUri);
        }
    }

    public byte[] readBytes(Uri uri) throws IOException {
        Context context = Utils.getInstance().getApplicationContext();

        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        inputStream.close();

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public void save() throws Exception {
        if (male.isChecked()) {
            sex = 'm';
        } else if (female.isChecked()) {
            sex = 'f';
        }

        User user = User.builder()
                .email(loggedInUser.getEmail())
                .lastName(lastName.getText().toString())
                .firstName(firstName.getText().toString())
                .middleName(middleName.getText().toString())
                .sex(Character.toString(sex))
                .birthDate(LocalDate.parse(birthDate.getText().toString()))
                .address(address.getText().toString())
                .build();

        Log.i("", user.toString());

        LocalDbUserService userService = new LocalDbUserService(this.getActivity());
        userService.updateLoggedInUserInfo(user);
    }
}
