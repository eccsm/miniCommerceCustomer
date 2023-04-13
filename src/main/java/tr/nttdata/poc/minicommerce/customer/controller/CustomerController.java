package tr.nttdata.poc.minicommerce.customer.controller;

import org.apache.commons.lang.UnhandledException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tr.nttdata.poc.minicommerce.customer.annotation.LogObjectAfter;
import tr.nttdata.poc.minicommerce.customer.annotation.LogObjectBefore;
import tr.nttdata.poc.minicommerce.customer.exception.CustomerNotFoundException;
import tr.nttdata.poc.minicommerce.customer.model.Customer;
import tr.nttdata.poc.minicommerce.customer.model.login.LoginRequest;
import tr.nttdata.poc.minicommerce.customer.service.CustomerService;
import tr.nttdata.poc.minicommerce.customer.service.UserService;


@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @LogObjectBefore
    @LogObjectAfter
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        String token = userService.authenticateUser(loginRequest);
        if (userService.authenticateUser(loginRequest) != null)
            return ResponseEntity.ok(token);
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
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
    public void updateCustomer(@PathVariable String id,@Valid @RequestBody Customer customer) {
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

}
