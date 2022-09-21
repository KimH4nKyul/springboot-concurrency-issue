package study.kimhankyul.springbootconcurrencyissue.facade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.kimhankyul.springbootconcurrencyissue.domain.entity.Stock;
import study.kimhankyul.springbootconcurrencyissue.domain.repository.LockRepository;
import study.kimhankyul.springbootconcurrencyissue.domain.repository.StockRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MySQL 환경에서 트라이해야 한다.
 * H2 는 get_lock, release 등의 락 관련 메소드를 제공하지 않기 때문이다.
 */

@SpringBootTest
class NamedLockStockFacadeTest {
    @Autowired
    private NamedLockStockFacade stockFacade;
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