package study.kimhankyul.springbootconcurrencyissue.domain.entity;

import javax.persistence.*;

@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

//    @Version
//    private Long version; // OptimisticLock을 위한 칼럼

    public Stock() {}

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void decrease(Long quantity) {
        if(this.quantity - quantity < 0) {
            throw new RuntimeException("foo");
        }
        this.quantity = this.quantity - quantity;
    }
}
