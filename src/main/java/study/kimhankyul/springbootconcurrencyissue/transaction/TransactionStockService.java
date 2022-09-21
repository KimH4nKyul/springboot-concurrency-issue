package study.kimhankyul.springbootconcurrencyissue.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import study.kimhankyul.springbootconcurrencyissue.service.StockService;

public class TransactionStockService {

    private final StockService stockService;

    TransactionStockService(StockService stockService) {
        this.stockService = stockService;
    }

    public void decrease_sync(Long id, Long quantity) {

        /*
        @Transactional은 내부적으로 특정 서비스를 실행했을 때,
        그 서비스를 start/endTransaction()으로 감싼다.
        이 때, 서비스 호출이 완료되고 endTransaction()으로 넘어가는 타이밍에
        새로운 스레드가 아직 갱신되지 않은 데이터에 접근해 경쟁 상태가 된다.
        이에 대한 해결책으로,
        1. synchronized 키워드를 사용할 때는 @Transactional을 사용하지 않는다.
         */

        startTransaction();

        stockService.decrease_sync(id, quantity);

        endTransaction();
    }

    public void startTransaction() {

    }

    public void endTransaction() {

    }
}
