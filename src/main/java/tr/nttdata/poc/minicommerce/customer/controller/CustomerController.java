package tr.nttdata.poc.minicommerce.customer.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.apache.commons.lang.UnhandledException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.nttdata.poc.minicommerce.customer.annotation.LogObjectAfter;
import tr.nttdata.poc.minicommerce.customer.annotation.LogObjectBefore;
import tr.nttdata.poc.minicommerce.customer.exception.CustomerNotFoundException;
import tr.nttdata.poc.minicommerce.customer.model.Customer;
import tr.nttdata.poc.minicommerce.customer.model.ResetPasswordModel;
import tr.nttdata.poc.minicommerce.customer.model.Token;
import tr.nttdata.poc.minicommerce.customer.model.login.JwtTokenUtil;
import tr.nttdata.poc.minicommerce.customer.model.login.LoginRequest;
import tr.nttdata.poc.minicommerce.customer.repository.ActivationCodeRepository;
import tr.nttdata.poc.minicommerce.customer.repository.PasswordResetRepository;
import tr.nttdata.poc.minicommerce.customer.repository.TemporaryCustomerRepository;
import tr.nttdata.poc.minicommerce.customer.service.CustomerService;
import tr.nttdata.poc.minicommerce.customer.service.UserService;

@RestController
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ActivationCodeRepository activationCodeRepository;
    @Autowired
    private PasswordResetRepository passwordResetRepository;
    @Autowired
    private TemporaryCustomerRepository temporaryCustomerRepository;

    @LogObjectBefore
    @LogObjectAfter
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        if (userService.authenticateUser(loginRequest))
            return ResponseEntity.ok("Email send to your e-mail address.");
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }


    @LogObjectBefore
    @LogObjectAfter
    @PostMapping("/login-twofactor-auth")
    public ResponseEntity<Token> loginWith2FAUser(@Valid @RequestBody LoginRequest code) {
        String token = activationCodeRepository.findByActivationCode(code.getVerificationCode());
        String newToken = null;
        try {
            String result = jwtTokenUtil.extractMail(token.toString());
            newToken = jwtTokenUtil.generateToken(result, 2 * 60 * 60 * 1000);
        }catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        else
            return ResponseEntity.ok(new Token(newToken));
    }

    @LogObjectBefore
    @LogObjectAfter
    @PostMapping("/register")
    public ResponseEntity<Customer> registerUser(@Valid @RequestBody Customer customer) {
        if (userService.registerUser(customer))
            return ResponseEntity.ok(customer);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping("/confirm")
    public ResponseEntity<Token> confirmUser(@RequestParam String token) {
        if (jwtTokenUtil.isTokenExpired(token)) {
            temporaryCustomerRepository.deleteByToken(token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String newToken = userService.confirm(token);
        return ResponseEntity.ok(new Token(newToken));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        try {
            Customer returned = customerService.getCustomerById(id);
            return ResponseEntity.ok(returned);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        try {
            Customer returned = customerService.getCustomerByEmail(email);
            return ResponseEntity.ok(returned);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @LogObjectBefore
    @LogObjectAfter
    @PutMapping("/{id}")
    public void updateCustomer(@PathVariable String id, @Valid @RequestBody Customer customer) {
        try {
            customer.setId(id);
            customerService.updateCustomer(customer);
        } catch (CustomerNotFoundException | IllegalArgumentException e) {
            throw new UnhandledException("Unknown Exception", e);
        }
    }

    @LogObjectBefore
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable String id) {
        try {
            customerService.deleteCustomerById(id);
        } catch (CustomerNotFoundException e) {
            throw new UnhandledException("Unknown Exception", e);
        }
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<ResetPasswordModel> requestReset(@RequestBody ResetPasswordModel resetPasswordModel) {

        if (resetPasswordModel == null && resetPasswordModel.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        resetPasswordModel = userService.requestPasswordReset(resetPasswordModel.getEmail());
        if (resetPasswordModel == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        passwordResetRepository.save(resetPasswordModel.getToken(), resetPasswordModel.getEmail());
        return ResponseEntity.ok(resetPasswordModel);
    }

    @GetMapping("/password-reset-request/{token}")
    public ResponseEntity<ResetPasswordModel> requestReset(@PathVariable String token) {

        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        if (token == null || token == "") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if (jwtTokenUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        resetPasswordModel.setEmail(passwordResetRepository.findByToken(token));
        resetPasswordModel.setToken(token);

        return ResponseEntity.ok(resetPasswordModel);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Customer> passwordReset(@RequestBody ResetPasswordModel resetPasswordModel) {

        if (resetPasswordModel == null || resetPasswordModel.getEmail() == null || resetPasswordModel.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Customer customer = userService.passwordReset(resetPasswordModel);
        return ResponseEntity.ok(customer);
    }
}
