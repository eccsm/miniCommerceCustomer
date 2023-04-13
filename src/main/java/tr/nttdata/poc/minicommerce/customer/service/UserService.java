package tr.nttdata.poc.minicommerce.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tr.nttdata.poc.minicommerce.customer.email.EmailSubject;
import tr.nttdata.poc.minicommerce.customer.email.IEmailSender;
import tr.nttdata.poc.minicommerce.customer.model.Customer;
import tr.nttdata.poc.minicommerce.customer.model.ResetPasswordModel;
import tr.nttdata.poc.minicommerce.customer.model.login.JwtTokenUtil;
import tr.nttdata.poc.minicommerce.customer.model.login.LoginRequest;
import tr.nttdata.poc.minicommerce.customer.repository.CustomerRepository;

@Service
public class UserService {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IEmailSender emailSender;

    @Resource(name = "redisTemplate")
    private HashOperations<String, Long, Customer> hashOperations;

    public String authenticateUser(LoginRequest loginRequest) {
        Customer customer = customerRepository.findByEmail(loginRequest.getEmail());
        if (customer != null && passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
        /*     ContainerCriteria criteria = LdapQueryBuilder.query()
                    .where("email").is(loginRequest.getEmail());
            Customer user = ldapTemplate.findOne(criteria, Customer.class); */

       //     if (user != null) {
              //  ldapTemplate.authenticate(criteria, loginRequest.getPassword());
                return jwtTokenUtil.generateToken(loginRequest);

         //   } else
           //     throw new BadCredentialsException("Invalid email or password");

        } else
            return null;
    }

    public boolean registerUser(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()) != null)
            throw new IllegalArgumentException("Email is already registered");

        try {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        //    ldapTemplate.bind(LdapNameBuilder.newInstance().build(), customer, null);
            customerRepository.save(customer);
            emailSender.send(EmailSubject.ACTIVATION,customer);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String confirm(String email) {
        try {
            Customer customer = customerRepository.findByEmail(email);
            //customer.setActive(true);
            customerRepository.update(customer);

            return "Update confirmed for user with email address: " + customer.getEmail();
        } catch (Exception e) {
            return "User not found";
        }
    }

    public ResetPasswordModel requestPasswordReset(String email){

        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        Customer customer = customerRepository.findByEmail(email);
        if(customer == null){
            return resetPasswordModel;
        }
        resetPasswordModel.setEmail(email);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(customer.getEmail());

        try {
            String token = emailSender.send(EmailSubject.RESET_PASSWORD,customer);
            resetPasswordModel.setToken(token);
            resetPasswordModel.setMessage("Mail başarıyla gönderildi.");

        }catch (Exception e){
            resetPasswordModel.setMessage("Gönderilirken bir hata oluştu.");
        }

        return resetPasswordModel;
    }

    public Customer passwordReset(ResetPasswordModel resetPasswordModel){

        Customer customer = customerRepository.findByEmail(resetPasswordModel.getEmail());
        if(customer == null){
            return customer;
        }
        customerRepository.update(customer);

        return customer;
    }

    public void logout(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
    //    logoutHandler.logout(request, response, authentication);
    }
}
