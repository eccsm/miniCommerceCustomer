package tr.nttdata.poc.minicommerce.customer.service.interfaces;

import tr.nttdata.poc.minicommerce.customer.exception.CustomerNotFoundException;
import tr.nttdata.poc.minicommerce.customer.model.Customer;

public interface ICustomerService {

    Customer getCustomerById(String id) throws CustomerNotFoundException;

    Customer getCustomerByEmail(String email) throws CustomerNotFoundException;

    void updateCustomer(Customer customer) throws CustomerNotFoundException, IllegalArgumentException;

    void deleteCustomerById(String id) throws CustomerNotFoundException;

}