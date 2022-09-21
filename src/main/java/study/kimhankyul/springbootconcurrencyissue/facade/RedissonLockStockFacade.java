package study.kimhankyul.springbootconcurrencyissue.facade;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import study.kimhankyul.springbootconcurrencyissue.annotation.Facade;
import study.kimhankyul.springbootconcurrencyissue.service.StockService;

import java.util.concurrent.TimeUnit;

/**
 * RedissonLock 은 Pub-Sub 기반의 락 구현 방식이다.
 * 채널을 하나 만들고 락을 점유중인 스레드가 락 획득을 대기중인 스레드에게 해제를 알려주면
 * 알림받은 스레드가 락 획득을 시도하기 때문에 성능상 LettuceLock 보다 낫다.
 * 락 관련 클래스를 제공해주기 때문에 별도의 레포지토리를 구성하지 않아도 된다.
 *
 * 재시도가 필요할 경우에 RedissonLock 을 주로 사용한다.
 * 재시도가 필요치 않을 경우에 LettuceLock 을 사용한다.
 */
@Facade
@RequiredArgsConstructor
public class RedissonLockStockFacade {
    private final RedissonClient client;
    private final StockService stockService;

    public void decrease(Long key, Long quantity) {
        RLock lock = client.getLock(key.toString());

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if(!available) {
                System.out.println("lock fail");
                return;
            }
            stockService.decrease(key, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
