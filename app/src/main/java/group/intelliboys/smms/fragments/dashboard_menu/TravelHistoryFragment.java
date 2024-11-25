package group.intelliboys.smms.fragments.dashboard_menu;

import android.annotation.SuppressLint;
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
import group.intelliboys.smms.models.data.view_adapters.TravelHistoryAdapter;
import group.intelliboys.smms.orm.data.TravelHistory;
import group.intelliboys.smms.orm.repository.TravelHistoryRepository;
import group.intelliboys.smms.utils.Executor;

public class TravelHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private TravelHistoryAdapter travelHistoryAdapter;
    private List<TravelHistory> travelHistories;
    private TravelHistoryRepository travelHistoryRepository;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_history, container, false);

        recyclerView = view.findViewById(R.id.travelHistoryView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Executor.run(() -> {
            travelHistoryRepository = new TravelHistoryRepository();
            travelHistories = travelHistoryRepository.getAllTravelHistories();

            if (!travelHistories.isEmpty()) {
                travelHistoryAdapter = new TravelHistoryAdapter(travelHistories);
                recyclerView.setAdapter(travelHistoryAdapter);
            }
        });

        return view;
    }
}
