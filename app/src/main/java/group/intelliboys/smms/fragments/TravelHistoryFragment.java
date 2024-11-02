package group.intelliboys.smms.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import group.intelliboys.smms.R;
import group.intelliboys.smms.models.data.TravelHistory;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.services.Utils;
import group.intelliboys.smms.services.local.LocalDbTravelHistoryService;

public class TravelHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private TravelHistoryAdapter travelHistoryAdapter;
    private List<TravelHistory> travelHistories;
    private LocalDbTravelHistoryService travelHistoryService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_history, container, false);
        travelHistoryService = new LocalDbTravelHistoryService(getActivity());

        recyclerView = view.findViewById(R.id.travelHistoryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        User user = Utils.getInstance().getLoggedInUser().getUserModel().getValue();

        if (user != null) {
            travelHistories = travelHistoryService.getTravelHistoriesByUser(user.getEmail());
            travelHistoryAdapter = new TravelHistoryAdapter(travelHistories);
            recyclerView.setAdapter(travelHistoryAdapter);
        }

        return view;
    }
}
