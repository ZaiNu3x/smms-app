package group.intelliboys.smms.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import group.intelliboys.smms.R;

public class CreateClubFragment extends Fragment {

    private EditText etClubName;
    private Button btnCreateClub;
    private ImageButton btnAddLogo;
    private Uri selectedLogoUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    private OnGroupCreatedListener listener;

    public interface OnGroupCreatedListener {
        void onGroupCreated(String groupName, Uri logoUri);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedLogoUri = result.getData().getData();
                    btnAddLogo.setImageURI(selectedLogoUri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_club, container, false);

        etClubName = view.findViewById(R.id.etClubName);
        btnCreateClub = view.findViewById(R.id.btnCreateClub);
        btnAddLogo = view.findViewById(R.id.btnAddLogo);

        if (getActivity() instanceof OnGroupCreatedListener) {
            listener = (OnGroupCreatedListener) getActivity();
        }

        btnAddLogo.setOnClickListener(v -> openImagePicker());

        btnCreateClub.setOnClickListener(v -> {
            String clubName = etClubName.getText().toString().trim();

            if (clubName.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter a club name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedLogoUri == null) {
                Toast.makeText(getActivity(), "Please select a logo for the club", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                listener.onGroupCreated(clubName, selectedLogoUri);
            }

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
}
