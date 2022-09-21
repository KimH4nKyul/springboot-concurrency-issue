package study.kimhankyul.springbootconcurrencyissue.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import study.kimhankyul.springbootconcurrencyissue.domain.entity.Stock;
import study.kimhankyul.springbootconcurrencyissue.domain.repository.StockRepository;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(Long id, Long quantity) {
        // get stock
        // decrease
        // store

        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);

        // 이 코드에서는 경쟁 상태가 발생할 것이라 여러 스래드가 동시에 재고를 감소시킨다면 원하는 결과가 나오지 않을 것임
    }

//    @Transactional
//    NamedLock에서 StockService 는 부모의 트랜잭션과 별도로 실행되어야 하기 때문에 propagation 을 변경한다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void decrease_sync(Long id, Long quantity) {
        // 1. Synchronized 키워드로 해결
        // 1-1. 자바의 synchronized는 하나의 프로세스 안에서만 보장됨
        //      그렇다면, 서버가 두 대 이상일 경우에는? = 경쟁 상태
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);

        // 2. synchronized 키워드로도 해결이 안됨
        // 3. @Transactional을 빼서 내부적으로 새 스레드가 접근할 타이밍을 없앤다.
    }
}
