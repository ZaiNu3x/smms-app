package group.intelliboys.smms.models.data.view_adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import group.intelliboys.smms.R;
import group.intelliboys.smms.orm.data.AccidentHistory;
import group.intelliboys.smms.orm.data.TravelStatusUpdate;
import group.intelliboys.smms.orm.data.TravelStatusUpdateWithAccidentHistory;

public class AccidentHistoryAdapter extends RecyclerView.Adapter<AccidentHistoryAdapter.AccidentHistoryViewHolder> {
    private List<TravelStatusUpdateWithAccidentHistory> accidentHistories;

    public AccidentHistoryAdapter(List<TravelStatusUpdateWithAccidentHistory> accidentHistories) {
        this.accidentHistories = accidentHistories;
    }

    @NonNull
    @Override
    public AccidentHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accident, parent, false);
        return new AccidentHistoryAdapter.AccidentHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccidentHistoryViewHolder holder, int position) {
        TravelStatusUpdateWithAccidentHistory entry = accidentHistories.get(position);
        TravelStatusUpdate statusUpdate = entry.getStatusUpdate();
        AccidentHistory accidentHistory = entry.getAccidentHistory();

        holder.imageView.setImageResource(R.drawable.ic_map);

        if (statusUpdate != null) {
            if (!TextUtils.isEmpty(statusUpdate.getAddress())) {
                // CODES IF ADDRESS IS NOT NULL AND EMPTY
                holder.accidentLocation.setText(statusUpdate.getAddress());
            } else {
                // CODES IF ADDRESS NULL OR EMPTY
                String coordinates = "lat: " + statusUpdate.getLatitude() + ", lon: " + statusUpdate.getLongitude();
                holder.accidentLocation.setText(coordinates);
            }
        }

        if (accidentHistory != null) {
            if (accidentHistory.getCreatedAt() != null) {
                LocalTime time = accidentHistory.getCreatedAt().toLocalTime();
                String date = accidentHistory.getCreatedAt().toLocalDate().toString();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                String formattedTime = time.format(formatter);

                holder.accidentTime.setText(formattedTime);
                holder.accidentDate.setText(date);
            }
        }
    }

    @Override
    public int getItemCount() {
        return accidentHistories.size();
    }

    public static class AccidentHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView accidentLocation;
        private final TextView accidentTime;
        private final TextView accidentDate;

        public AccidentHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.accident_img);
            accidentLocation = itemView.findViewById(R.id.accident_location);
            accidentTime = itemView.findViewById(R.id.accident_time);
            accidentDate = itemView.findViewById(R.id.accident_date);
        }
    }
}
