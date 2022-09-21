package study.kimhankyul.springbootconcurrencyissue.facade;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import study.kimhankyul.springbootconcurrencyissue.domain.repository.RedisLockRepository;
import study.kimhankyul.springbootconcurrencyissue.service.StockService;

@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {
    private final RedisLockRepository redisLockRepository;
    private final StockService stockService;

    public void decrease(Long key, Long quantity) throws InterruptedException {
        while(!redisLockRepository.lock(key)) { // 락 획득 시도 로직
            Thread.sleep(100); // 실패시 sleep 을 통해 Redis에 갈 수 있는 부하를 줄인다.
        }
        try {
            stockService.decrease_sync(key, quantity);
        } finally {
            redisLockRepository.unlock(key);
        }
    }
}
