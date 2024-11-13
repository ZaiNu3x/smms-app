package group.intelliboys.smms.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import group.intelliboys.smms.R;

public class ClubsFragment extends Fragment {

    private LinearLayout groupListLayout;
    private ImageButton btnAddGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        groupListLayout = view.findViewById(R.id.groupListLayout);
        btnAddGroup = view.findViewById(R.id.btnAddGroup);

        btnAddGroup.setOnClickListener(v -> openCreateClubFragment());

        return view;
    }

    private void openCreateClubFragment() {
        CreateClubFragment createClubFragment = new CreateClubFragment();

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, createClubFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void addNewGroupToList(String groupName, Uri logoUri) {

        View newGroupView = getLayoutInflater().inflate(R.layout.group_item, null);


        TextView groupNameTextView = newGroupView.findViewById(R.id.tvGroupName);
        groupNameTextView.setText(groupName);


        ImageView groupLogoImageView = newGroupView.findViewById(R.id.ivGroupLogo);
        if (logoUri != null) {
            groupLogoImageView.setImageURI(logoUri);
        }


        groupListLayout.addView(newGroupView);
        groupListLayout.requestLayout();


        Log.d("ClubsFragment", "Group added: " + groupName);
        Toast.makeText(getActivity(), "New group '" + groupName + "' added", Toast.LENGTH_SHORT).show();
    }
}
