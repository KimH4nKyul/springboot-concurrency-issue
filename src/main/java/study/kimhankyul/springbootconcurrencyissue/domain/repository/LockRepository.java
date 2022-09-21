package study.kimhankyul.springbootconcurrencyissue.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import study.kimhankyul.springbootconcurrencyissue.domain.entity.Stock;

public interface LockRepository extends JpaRepository<Stock, Long> {
    // 실무에서는 레포지토리가 아니라 별도의 JDBC를 사용해야한다.
    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}
