package group.intelliboys.smms.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import group.intelliboys.smms.R;
import group.intelliboys.smms.services.remote.DataSynchronizationService;

public class SettingsFragment extends Fragment {
    private ImageView syncImageView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        syncImageView = view.findViewById(R.id.syncButton);

        syncImageView.setOnClickListener(v -> {
            Toast.makeText(requireActivity(), "Syncing.....", Toast.LENGTH_LONG).show();
        });

        DataSynchronizationService service = new DataSynchronizationService(SettingsFragment.this);

        try {
            service.synchronizeUserData();
        } catch (Exception e) {
            Log.i("", Objects.requireNonNull(e.getMessage()));
        }
        return view;
    }
}
