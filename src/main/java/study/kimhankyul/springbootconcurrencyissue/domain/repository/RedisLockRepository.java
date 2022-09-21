package study.kimhankyul.springbootconcurrencyissue.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * LettuceLock은 Redis를 활용한다.
 * LettuceLock은 별도의 세션관리가 필요없지만, Retry 로직을 개발자가 구현해야 한다.
 * - setnx 명령어
 *      - key, value 셋을 생성할 때, 기존의 값이 없을때 만 셋 해준다.
 * - spinlock 방식
 *      - 락을 획득하려는 스레드가 락을 사용할 수 있는지 반복적으로 확인하면서 락 획득을 시도하는 방식이다.
 */

@Component
@RequiredArgsConstructor
public class RedisLockRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(Long key) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
    }

    public Boolean unlock(Long key) {
        return redisTemplate.delete(generateKey(key));
    }

    private String generateKey(Long key) {
        return key.toString();
    }
}
