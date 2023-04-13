package tr.nttdata.poc.minicommerce.customer.repository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import tr.nttdata.poc.minicommerce.customer.model.Customer;

@Repository
public class CustomerRepository {

    private static final String HASH_NAME = "customers";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Customer findById(String id) {
        HashOperations<String, String, Customer> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(HASH_NAME, id);
    }

    public Customer findByEmail(String email) {
        HashOperations<String, String, Customer> hashOperations = redisTemplate.opsForHash();
        return hashOperations.values(HASH_NAME)
                .stream()
                .filter(customer -> email.equals(customer.getEmail()))
                .findFirst()
                .orElse(null);
    }

    public Customer save(Customer customer) {
        HashOperations<String, String, Customer> hashOperations = redisTemplate.opsForHash();
        if (customer.getId() == null)
            customer.setId(UUID.randomUUID().toString());

        hashOperations.put(HASH_NAME, customer.getId().toString(), customer);
        return customer;
    }

    public void deleteById(String id) {
        HashOperations<String, String, Customer> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(HASH_NAME, id);
    }

    public Customer update(Customer customer) {
        HashOperations<String, String, Customer> hashOperations = redisTemplate.opsForHash();
        String id = customer.getId().toString();
        if (id == null)
            throw new IllegalArgumentException("Customer id cannot be null for update operation.");

        Customer existingCustomer = findById(id);
        if (existingCustomer == null)
            throw new IllegalArgumentException("Customer with id " + id + " not found for update operation.");

        if (customer.getFirstName() != null && existingCustomer.getFirstName() != customer.getFirstName())
            existingCustomer.setFirstName(customer.getFirstName());
        if (customer.getLastName() != null && existingCustomer.getLastName() != customer.getLastName())
            existingCustomer.setLastName(customer.getLastName());
        if (customer.getEmail() != null && existingCustomer.getEmail() != customer.getEmail())
            existingCustomer.setEmail(customer.getEmail());
        if (customer.getPassword() != null && existingCustomer.getPassword() != customer.getPassword())
            existingCustomer.setPassword(customer.getPassword());

        hashOperations.put(HASH_NAME, existingCustomer.getId(), existingCustomer);
        return existingCustomer;
    }

}
