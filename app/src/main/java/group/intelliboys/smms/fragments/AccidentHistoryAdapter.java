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

public class AccidentHistoryAdapter extends RecyclerView.Adapter<AccidentHistoryAdapter.AccidentViewHolder> {

    private List<Accident> accidentList;

    public AccidentHistoryAdapter(List<Accident> accidentList) {
        this.accidentList = accidentList;
    }

    @NonNull
    @Override
    public AccidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //  (item_accident.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accident, parent, false);
        return new AccidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccidentViewHolder holder, int position) {

        Accident accident = accidentList.get(position);


        holder.locationTextView.setText(accident.getLocation());
        holder.timeTextView.setText(accident.getTime());
        holder.dateTextView.setText(accident.getDate());
        holder.mapImageView.setImageResource(R.drawable.ic_map);
    }

    @Override
    public int getItemCount() {
        return accidentList.size();
    }

    static class AccidentViewHolder extends RecyclerView.ViewHolder {

        ImageView mapImageView;
        TextView locationTextView;
        TextView timeTextView;
        TextView dateTextView;

        public AccidentViewHolder(@NonNull View itemView) {
            super(itemView);
            mapImageView = itemView.findViewById(R.id.img_map);
            locationTextView = itemView.findViewById(R.id.tv_location);
            timeTextView = itemView.findViewById(R.id.tv_time);
            dateTextView = itemView.findViewById(R.id.tv_date);
        }
    }
}
