package study.kimhankyul.springbootconcurrencyissue.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import study.kimhankyul.springbootconcurrencyissue.domain.entity.Stock;

import javax.persistence.LockModeType;

public interface StockRepository extends JpaRepository<Stock, Long> {
    /*
        PessimisticLock은 데이터(Table or row)에 락을 건다.
     */
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)   //  스프링 Data JPA 에서는 Lock 애노테이션으로 PessimisticLock 쉽게 구현
    @Query("select s from Stock s where s.id = :id")
    Stock findByIdWithPessimisticLock(Long id);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from Stock s where s.id = :id")
    Stock findByIdWithOptimisticLock(Long id);
}
