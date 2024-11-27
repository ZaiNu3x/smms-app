package group.intelliboys.smms.orm.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(tableName = "accident_history", foreignKeys = @ForeignKey(
        entity = TravelStatusUpdate.class,
        parentColumns = "travelStatusUpdateId",
        childColumns = "travelStatusUpdateId",
        onDelete = ForeignKey.CASCADE))
public class AccidentHistory {
    @NonNull
    @PrimaryKey
    private String accidentHistoryId;
    @NonNull
    private String travelStatusUpdateId;
    private byte[] frontCameraSnap;
    private byte[] backCameraSnap;
    @NonNull
    private String message;
    private LocalDateTime createdAt;
}
