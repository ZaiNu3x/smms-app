package group.intelliboys.smms.models.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountVersion {
    private String email;
    private long version;
}
