package study.kimhankyul.springbootconcurrencyissue.service;

import org.springframework.stereotype.Service;
import study.kimhankyul.springbootconcurrencyissue.domain.entity.Stock;
import study.kimhankyul.springbootconcurrencyissue.domain.repository.StockRepository;

import javax.transaction.Transactional;

@Service
public class PessimisticLockStockService {

    private final StockRepository stockRepository;

    PessimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(Long id, Long quantity) {
        // 1. lock을 걸고 데이터를 가져온다.
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);

        // 2. 수행
        stock.decrease(quantity);

        // 3. 저장
        stockRepository.saveAndFlush(stock);

        /*
        PessimisticLock 수행시 쿼리를 보면 `for update` 라는 부분이 보일 것이다.
        이 부분이 락을 걸고 데이터를 가져오는 부분이다.
        PessimisticLock은 충돌이 빈번히 일어난다면 OptimisticLock 보다 성능이 좋다.
        락을 통해 업데이트를 제어할 수 있지만, 별도의 락을 잡기 때문에 성능 감소가 있을 수 있다.
         */

        /*
        OptimisticLock은 실제로 락을 사용하진 않고 데이터에 version을 부여해 데이터 정합성을 맞추는 방식이다.

         */
    }
}
