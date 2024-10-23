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

import java.util.ArrayList;
import java.util.List;

import group.intelliboys.smms.R;

public class AccidentHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private AccidentHistoryAdapter adapter;
    private List<Accident> accidentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_accident_history, container, false);

        // Setup RecyclerView
        recyclerView = rootView.findViewById(R.id.recycler_accident_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize accident data
        accidentList = new ArrayList<>();
        accidentList.add(new Accident("Main St & 1st Ave", "10:30 AM", "2023-09-01"));
        accidentList.add(new Accident("Elm St & 3rd Ave", "12:15 PM", "2023-08-15"));
        accidentList.add(new Accident("Broadway & 5th Ave", "2:45 PM", "2023-07-21"));

        // Set the adapter with the accident list
        adapter = new AccidentHistoryAdapter(accidentList);
        recyclerView.setAdapter(adapter);

        // Return the inflated view
        return rootView;
    }
}
