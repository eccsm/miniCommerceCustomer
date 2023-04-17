package tr.nttdata.poc.minicommerce.customer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ActivationCodeRepository {
    private static final String HASH_NAME = "activation_codes";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String findByActivationCode(String activationCode) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(HASH_NAME, activationCode);
    }

    public void save(String activationCode, String token) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(HASH_NAME, activationCode, token);
    }
}
