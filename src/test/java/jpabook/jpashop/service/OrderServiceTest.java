package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("상품주문")
    public void createOrder() {
        // given
        Member member = createMember();

        Item item = createItem("책", 100, 1000);

        // when
        Long orderId = orderService.order(member.getId(), item.getId(), 2);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertThat(getOrder.getStatus()).isSameAs(OrderStatus.ORDER);
        Assertions.assertThat(getOrder.getOrderItemList().size()).isSameAs(1);
        Assertions.assertThat(getOrder.getTotalPrice()).isEqualTo(1000 * 2);
        Assertions.assertThat(item.getStockQuantity()).isEqualTo(100 - 2);
    }

    @Test
    @DisplayName("재고_수량_초과_테스트")
    public void stockExceedTest() {
        // given
        Member member = createMember();
        Item item = createItem("책", 10, 1000);

        int orderCount = 11;

        // when

        // then
        NotEnoughStockException e = org.junit.jupiter.api.Assertions.assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(), orderCount));

        Assertions.assertThat(e.getMessage()).isEqualTo("need more stock");
    }

    @Test
    @DisplayName("주문취소")
    public void cancelOrder() {
        // given
        Member member = createMember();
        Item item = createItem("책", 1000, 1000);
        int orderCount = 3;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        // when
        orderService.cancelOrder(orderId);

        // then
        Assertions.assertThat(item.getStockQuantity()).isEqualTo(1000);
    }

    private Item createItem(String name, int stockQuantity, int price) {
        Item item = new Book();
        item.setName(name);
        item.setStockQuantity(stockQuantity);
        item.setPrice(price);
        em.persist(item);
        return item;
    }

    private Member createMember() {
        Member member = new Member();
        member.setUsername("진희솜");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}