package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;
/*
    @Embedded
    @AttributeOverride(name = "zipCode", column = @Column(name = "home_zipCode"))
    @AttributeOverride(name = "address1", column = @Column(name = "home_address1"))
    @AttributeOverride(name = "address2", column = @Column(name = "home_address2"))
    private Address homeAddress;
*/

    @Enumerated(EnumType.STRING) // ordinal 이면 숫자로 자동 매칭되서 들어감
    private DeliveryStatus status; // enum으로 READY, COMP


}
