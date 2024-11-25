package group.intelliboys.smms.models.data.view_adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import group.intelliboys.smms.R;
import group.intelliboys.smms.orm.data.TravelHistory;

public class TravelHistoryAdapter extends RecyclerView.Adapter<TravelHistoryAdapter.TravelHistoryViewHolder> {
    private List<TravelHistory> travelHistories;

    public TravelHistoryAdapter(List<TravelHistory> travelHistories) {
        this.travelHistories = travelHistories;
    }

    @NonNull
    @Override
    public TravelHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel, parent, false);
        return new TravelHistoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TravelHistoryViewHolder holder, int position) {
        TravelHistory travelHistory = travelHistories.get(position);
        holder.imageView.setImageResource(R.drawable.ic_map);

        if (!TextUtils.isEmpty(travelHistory.getStartLocationName())) {
            holder.startLocation.setText("FROM: " + travelHistory.getStartLocationName());
        }

        if (!TextUtils.isEmpty(travelHistory.getEndLocationName())) {
            holder.endLocation.setText("TO: " + travelHistory.getEndLocationName());
        }

        if (travelHistory.getStartTime() != null && travelHistory.getEndTime() != null) {
            Duration duration = Duration.between(travelHistory.getStartTime(), travelHistory.getEndTime());

            long hours = duration.toHours();
            long minutes = duration.toMinutes();

            if (hours > 0) {
                holder.duration.setText(hours + " hrs and " + minutes + " mins");
            } else holder.duration.setText(minutes + " mins");
        }

        if (travelHistory.getCreatedAt() != null) {
            LocalDate localDate = travelHistory.getCreatedAt().toLocalDate();
            holder.date.setText(localDate.toString());
        }
    }

    @Override
    public int getItemCount() {
        return travelHistories.size();
    }

    public static class TravelHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView startLocation;
        private final TextView endLocation;
        private final TextView duration;
        private final TextView date;

        public TravelHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_map);
            startLocation = itemView.findViewById(R.id.start_location);
            endLocation = itemView.findViewById(R.id.end_location);
            duration = itemView.findViewById(R.id.travel_duration);
            date = itemView.findViewById(R.id.travel_date);
        }
    }
}
