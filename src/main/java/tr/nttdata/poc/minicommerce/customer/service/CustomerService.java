package tr.nttdata.poc.minicommerce.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.nttdata.poc.minicommerce.customer.exception.CustomerNotFoundException;
import tr.nttdata.poc.minicommerce.customer.model.Customer;
import tr.nttdata.poc.minicommerce.customer.repository.CustomerRepository;
import tr.nttdata.poc.minicommerce.customer.service.interfaces.ICustomerService;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;



    public Customer getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with email " + email + " not found");
        }
        return customer;
    }

}
