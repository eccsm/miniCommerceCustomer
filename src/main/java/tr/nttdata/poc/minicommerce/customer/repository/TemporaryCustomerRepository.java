package tr.nttdata.poc.minicommerce.customer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tr.nttdata.poc.minicommerce.customer.model.Customer;

import java.util.UUID;

@Repository
public class TemporaryCustomerRepository {
    private static final String HASH_NAME = "temporary_customers";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Customer findByToken(String token) {
        HashOperations<String, String, Customer> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(HASH_NAME, token);
    }

    public Customer save(String token, Customer customer) {
        HashOperations<String, String, Customer> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(HASH_NAME, token, customer);
        return customer;
    }

    public void deleteByToken(String token) {
        HashOperations<String, String, Customer> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(HASH_NAME, token);
    }

}
