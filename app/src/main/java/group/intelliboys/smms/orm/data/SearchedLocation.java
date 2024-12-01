package group.intelliboys.smms.orm.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class SearchedLocation {
    @NonNull
    @PrimaryKey
    private String displayName;
    private float latitude;
    private float longitude;

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}
