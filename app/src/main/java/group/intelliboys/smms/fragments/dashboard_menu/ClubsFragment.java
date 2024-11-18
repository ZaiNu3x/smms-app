package group.intelliboys.smms.fragments.dashboard_menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import group.intelliboys.smms.R;
import group.intelliboys.smms.activities.clubs.CreateClubActivity;

public class ClubsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clubs, container, false);


        ImageButton btnAddGroup = view.findViewById(R.id.btnAddGroup);


        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CreateClubActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
