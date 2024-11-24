package group.intelliboys.smms.orm.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(tableName = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    @NonNull
    @PrimaryKey
    private String email;

    @NonNull
    private String lastName;

    @NonNull
    private String firstName;

    private String middleName;

    private char sex;

    @NonNull
    private LocalDate birthDate;

    private int age;

    @NonNull
    private String address;

    private byte[] profilePic;

    @NonNull
    private String token;
}
