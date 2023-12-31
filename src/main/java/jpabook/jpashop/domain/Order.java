package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [order, cancel] enum으로 만들거임

    // 관계들 === Member, orderitems, Delivery
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;



    /*
    public static void main(String[] args) {
        Member member = new Member();
        Order order = new Order();
        member.getOrders().add(order);
        order.setMember(member);
    } -> 이런식으로 하지 않고 미리 연관관계 넣어두는 것이 좋다

    */

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문을 취소하면
     * 1. 배달 완료된지 체크
     * 2. 배달이 완료되지 않았으면 주문 상태를 CANCEL로 변경
     * 3. 주문에 포함된 아이템들의 재고를 원복해야함 (cancel)
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItemList) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    public int getTotalPrice() {
        int totalPrice = 0;

        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }

        return totalPrice;
    }

}
