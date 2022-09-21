package study.kimhankyul.springbootconcurrencyissue.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.kimhankyul.springbootconcurrencyissue.domain.repository.LockRepository;
import study.kimhankyul.springbootconcurrencyissue.service.StockService;


@Component
@RequiredArgsConstructor
public class NamedLockStockFacade {
    /**
     * NamedLock 은 이름을 가진 메타데이터락이다.
     * 주로 분산락을 구현할 때 사용한다.
     * 이름을 가진 락을 획득한 후 해제될때 까지 다른 세션은 이 락을 획득할 수 없다.
     * 트랜잭션이 종료될 때 락이 자동해제 되지 않기 때문에 별도의 명령으로 해제 해주거나 선점시간이 끝날때 까지 기다려야한다.
     */
    private final LockRepository lockRepository;
    /**
     * NamedLock은 Stock에 락을 하지 않고 별도의 공간에 락을 하기 때문에 LockRepository 를 별도로 구현했다.
     */
    private final StockService stockService;

    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            stockService.decrease(id, quantity); // NamedLock 에서는 synchronized 를 사용하지 않음에 주의한다.
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}
