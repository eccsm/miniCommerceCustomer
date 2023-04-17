package tr.nttdata.poc.minicommerce.customer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetRepository {
    private static final String HASH_NAME = "password_resets";
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String findByToken(String token) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(HASH_NAME, token);
    }

    public void save(String token, String email) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(HASH_NAME, token, email);
    }
}
