package group.intelliboys.smms.orm.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(tableName = "user")
public class User {
    @PrimaryKey
    private String email;
    private String lastName;
    private String firstName;
    private String middleName;
    private char sex;
    private LocalDate birthDate;
    private int age;
    private String address;
    private byte[] profilePic;
}
