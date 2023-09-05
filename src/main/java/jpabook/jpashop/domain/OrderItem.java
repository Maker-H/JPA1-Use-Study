package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    private int orderPrice;

    private int count; // 주문한 수량

    // 관계 item, order
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    //==생성 메서드==//

    /**
     *
     * 1. 관계에 있는 Item을 받은 다음 관계 설정
     * (Order을 넣지 않는 이유는 OrderItem은 Order이 생성될 때만 생성되기에 Order에서 설정해주는 것이 맞기 때문이다.)
     *
     * 2. item의 재고 감산
     *
     * = 주문 생성 => 주문에 따른 주문아이템 생성
     * = 주문 아이템 생성
     */
    public static OrderItem orderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); // item에 대한 재고 설정
        return orderItem;
    }

    //==비즈니스 로직==//
    /**
     * 주문항목을 취소하면 그에 대한 재고가 다시 item으로 들어가야한다
     */
    public void cancel() {
        getItem().addStock(count);
    }

    public int getTotalPrice() {
        return getCount() * getOrderPrice();
    }
}
