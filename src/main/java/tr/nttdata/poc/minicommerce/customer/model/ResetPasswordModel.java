package tr.nttdata.poc.minicommerce.customer.model;

import lombok.Data;

@Data
public class ResetPasswordModel {
    private String email;
    private String token;
    private String message;
    private String password;
}