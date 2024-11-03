package group.intelliboys.smms.models.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Observer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
    private long version;
    private String email;
    private String phoneNumber;
    private String lastName;
    private String firstName;
    private String middleName;
    private String sex;
    private LocalDate birthDate;
    private byte age;
    private String address;
    private String authToken;
    private byte[] profilePic;
    private List<TravelHistory> travelHistories;
}
