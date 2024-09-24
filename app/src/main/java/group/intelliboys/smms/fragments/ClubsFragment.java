package group.intelliboys.smms.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

import group.intelliboys.smms.R;

public class ClubsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        ImageView clubsLogoImageView = view.findViewById(R.id.clubsLogoImageView);

        clubsLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the next activity
                Intent intent = new Intent(getActivity(), NextActivity.class);
                startActivity(intent);
                }
        });

        return view;
    }
}