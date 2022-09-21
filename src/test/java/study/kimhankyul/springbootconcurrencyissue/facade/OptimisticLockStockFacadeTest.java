package study.kimhankyul.springbootconcurrencyissue.facade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.kimhankyul.springbootconcurrencyissue.domain.entity.Stock;
import study.kimhankyul.springbootconcurrencyissue.domain.repository.StockRepository;
import study.kimhankyul.springbootconcurrencyissue.service.PessimisticLockStockService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OptimisticLock 은 별도의 락을 잡지 않으므로 PessimisticLock 보다 성능상 이점이 있다.
 * 그러나 업데이트 실패했을 때 재시도 로직을 개발자가 직접 작성해야한다.
 * 재시도가 일어난다는 것은 충동일 빈번하다는 뜻으로, 이런 경우에는 PessimisticLock 이 성능상 이점이다.
 */

@SpringBootTest
class OptimisticLockStockFacadeTest {
    @Autowired
    private OptimisticLockStockFacade stockFacade;
    @Autowired private StockRepository stockRepository;


    @BeforeEach
    void before() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    void after() {
        stockRepository.deleteAll();
    }

    @Test
    void 동시에_100개_요청() throws InterruptedException{
        int threadCount = 100;
        // 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 자바 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 요청이 끝날때 까지 기다림
        CountDownLatch latch = new CountDownLatch(threadCount);
        // 100개 요청 생성
        for(int i=0; i<threadCount; i++) {
            executorService.submit(()-> {
                try {
                    stockFacade.decrease(1L, 1L);
                } catch(InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0L, stock.getQuantity());
    }
}