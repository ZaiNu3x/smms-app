package group.intelliboys.smms.models.data;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
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
}
