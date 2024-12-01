package group.intelliboys.smms.fragments.dashboard_menu;

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
import java.util.concurrent.Executors;

import group.intelliboys.smms.R;
import group.intelliboys.smms.models.data.view_adapters.AccidentHistoryAdapter;
import group.intelliboys.smms.orm.data.TravelStatusUpdateWithAccidentHistory;
import group.intelliboys.smms.orm.repository.TravelStatusUpdateAccidentHistoryRepository;

public class AccidentHistoryFragment extends Fragment {
    private RecyclerView accidentListView;
    private List<TravelStatusUpdateWithAccidentHistory> accidentHistories;
    private TravelStatusUpdateAccidentHistoryRepository accidentHistoryRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accident_history, container, false);
        accidentListView = view.findViewById(R.id.recycler_accident_history);
        accidentListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Executors.newSingleThreadExecutor().submit(() -> {
            accidentHistoryRepository = new TravelStatusUpdateAccidentHistoryRepository();
            accidentHistories = accidentHistoryRepository.getAccidentHistories();

            if (!accidentHistories.isEmpty()) {
                AccidentHistoryAdapter adapter = new AccidentHistoryAdapter(accidentHistories);
                accidentListView.setAdapter(adapter);
            }
        });

        return view;
    }
}
