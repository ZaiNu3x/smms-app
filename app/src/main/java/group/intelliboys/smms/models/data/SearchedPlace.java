package group.intelliboys.smms.models.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchedPlace {
    private String name;
    private String displayName;
    private double latitude;
    private double longitude;
    private String boundingBox;

    @Override
    public String toString() {
        return displayName;
    }
}
