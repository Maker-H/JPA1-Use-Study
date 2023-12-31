package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity; // 아이템 전체에 대한 수량

    @ManyToMany(mappedBy = "items")
    private List<Category> items = new ArrayList<>();

    //==비즈니스 로직==//

    /**
     * stock 증가
     * @param stockQuantity
     */
    public void addStock(int stockQuantity) {
        this.stockQuantity += stockQuantity;
    }

    /**
     * stock 감소
     * @param stockQuantity
     */
    public void removeStock(int stockQuantity) {
        int restStock = this.stockQuantity - stockQuantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
