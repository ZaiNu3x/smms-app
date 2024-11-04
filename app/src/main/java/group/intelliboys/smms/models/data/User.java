package group.intelliboys.smms.models.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private long version;
    private String email;
    private String phoneNumber;
    private boolean is2faEnabled;
    private String lastName;
    private String firstName;
    private String middleName;
    private String sex;
    private LocalDate birthDate;
    private byte age;
    private String address;
    private byte[] profilePic;
    private List<TravelHistory> travelHistories;
    private List<MonitoringWhitelist> monitoringWhitelists;
    private List<SearchHistory> searchHistories;
    private Settings settings;
    private String authToken;
}
