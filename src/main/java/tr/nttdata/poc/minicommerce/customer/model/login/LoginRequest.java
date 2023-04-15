package tr.nttdata.poc.minicommerce.customer.model.login;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    private String email;
    private String password;
    private String verificationCode;
}