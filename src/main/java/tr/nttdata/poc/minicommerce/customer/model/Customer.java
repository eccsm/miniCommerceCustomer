package tr.nttdata.poc.minicommerce.customer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.io.*;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("customers")
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotBlank(message = "firstName is mandatory")
    private String firstName;
    @NotBlank(message = "lastName is mandatory")
    private String lastName;
    @Email
    private String email;
    @NotBlank(message = "Name is mandatory")
    private String password;
    @DateTimeFormat
    private Date dateOfBirth;
    @Pattern(regexp = "^5\\d{9}$", message = "Invalid Turkish phone number")
    private String mobile;

}
