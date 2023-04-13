package tr.nttdata.poc.minicommerce.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.nttdata.poc.minicommerce.customer.exception.CustomerNotFoundException;
import tr.nttdata.poc.minicommerce.customer.model.Customer;
import tr.nttdata.poc.minicommerce.customer.repository.CustomerRepository;
import tr.nttdata.poc.minicommerce.customer.service.interfaces.ICustomerService;

@Service
public class CustomerService implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer getCustomerById(String id) {
        Customer customer = customerRepository.findById(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with id " + id + " not found");
        }
        return customer;
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with email " + email + " not found");
        }
        return customer;
    }

    @Override
    public void updateCustomer(Customer customer) {
        if (customer.getId() == null) {
            throw new IllegalArgumentException("Customer id cannot be null for update operation.");
        }
        Customer existingCustomer = customerRepository.findById(customer.getId().toString());
        if (existingCustomer == null) {
            throw new CustomerNotFoundException(
                    "Customer with id " + customer.getId() + " not found for update operation.");
        }
        customerRepository.update(customer);
    }

    @Override
    public void deleteCustomerById(String id) {
        Customer existingCustomer = customerRepository.findById(id);
        if (existingCustomer == null) {
            throw new CustomerNotFoundException("Customer with id " + id + " not found for delete operation.");
        }
        customerRepository.deleteById(id);
    }

}
