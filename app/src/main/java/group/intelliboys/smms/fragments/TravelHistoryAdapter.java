package group.intelliboys.smms.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import group.intelliboys.smms.R;
import group.intelliboys.smms.models.data.TravelHistory;

public class TravelHistoryAdapter extends RecyclerView.Adapter<TravelHistoryAdapter.TravelHistoryViewHolder> {
    private List<TravelHistory> travelHistories;

    public TravelHistoryAdapter(List<TravelHistory> travelHistories) {
        this.travelHistories = travelHistories;
    }

    @NonNull
    @Override
    public TravelHistoryAdapter.TravelHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel, parent, false);
        return new TravelHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelHistoryAdapter.TravelHistoryViewHolder holder, int position) {
        TravelHistory travelHistory = travelHistories.get(position);

        StringBuilder startCoordinatesBuilder = new StringBuilder();
        startCoordinatesBuilder.append(travelHistory.getStartLatitude())
                .append(",").append(travelHistory.getStartLongitude()).append(",")
                .append(travelHistory.getStartAltitude());

        String startCoordinates = new String(startCoordinatesBuilder);

        // Set the data to the views
        holder.locationTextView.setText(startCoordinates);
        holder.timeTextView.setText(travelHistory.getStartTime().toString());
        holder.dateTextView.setText(String.valueOf(travelHistory.getStartTime().getDayOfMonth()));
        holder.mapImageView.setImageResource(R.drawable.ic_map);
    }

    @Override
    public int getItemCount() {
        return travelHistories.size();
    }

    static class TravelHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mapImageView;
        private final TextView locationTextView;
        private final TextView timeTextView;
        private final TextView dateTextView;

        public TravelHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mapImageView = itemView.findViewById(R.id.img_map);
            locationTextView = itemView.findViewById(R.id.tv_location);
            timeTextView = itemView.findViewById(R.id.tv_time);
            dateTextView = itemView.findViewById(R.id.tv_date);
        }
    }
}