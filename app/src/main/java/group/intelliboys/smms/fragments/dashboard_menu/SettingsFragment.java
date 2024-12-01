package group.intelliboys.smms.fragments.dashboard_menu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import group.intelliboys.smms.MainActivity;
import group.intelliboys.smms.R;

public class SettingsFragment extends Fragment {
    private ImageView syncImageView;
    private EditText accidentEmailRecipient;
    private Button saveButton;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        accidentEmailRecipient = view.findViewById(R.id.accidentNotifEmail);
        saveButton = view.findViewById(R.id.accidentEmailSaveBtn);

        saveButton.setOnClickListener(v -> {
            MainActivity.accidentEmailRecipient = accidentEmailRecipient.getText().toString();
            Log.i("", "Updated!");
        });

        return view;
    }
}
