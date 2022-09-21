package study.kimhankyul.springbootconcurrencyissue.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.kimhankyul.springbootconcurrencyissue.service.OptimisticLockStockService;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {
        // OptimisticLock은 실패했을 때 재시도를 위해 이 Facade가 필요하다.
        while(true) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                break; // 정상적으로 업데이트 된다면 루프를 빠져나온다. 아니라면 sleep후 재시도한다.
            } catch(Exception e) {
                Thread.sleep(50);
            }
        }
    }
}
