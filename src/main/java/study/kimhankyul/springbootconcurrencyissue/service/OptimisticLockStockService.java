package study.kimhankyul.springbootconcurrencyissue.service;

import org.springframework.stereotype.Service;
import study.kimhankyul.springbootconcurrencyissue.domain.entity.Stock;
import study.kimhankyul.springbootconcurrencyissue.domain.repository.StockRepository;

import javax.transaction.Transactional;

@Service
public class OptimisticLockStockService {

    private final StockRepository stockRepository;

    public OptimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithOptimisticLock(id);
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
        // OptimsitcLock은 실패 했을 때 재시도하는 로직이 필요하기 때문에 facade가 필요하다.
    }
}
