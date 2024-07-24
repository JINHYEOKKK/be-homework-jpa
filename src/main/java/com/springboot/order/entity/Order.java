package com.springboot.order.entity;

import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column
    private OrderStatus orderStatus = OrderStatus.ORDER_REQUEST;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, name = "LAST_MODIFIED_AT")
    private LocalDateTime modifiedAt = LocalDateTime.now();

    // 다대1 일땐 joinColumn 연결할 테이블의 기본키.
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    //질문

    // 1대N 일때는 리스트, mappedBy 로 연결. 연결할 테이블에서 설정한 변수명
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderCoffee> orderCoffees = new ArrayList<>();

    // 이 메서드는 Order 객체에 OrderCoffee 객체를 추가하는 역할
    public void addOrderCoffee(OrderCoffee orderCoffee) {
        // 현재 order 객체의 오더커피스 리스트에 오더커피 객체를 추가.
        orderCoffees.add(orderCoffee);

        if(orderCoffee.getOrder() != this) {
            orderCoffee.addOrder(this);
        }
    }


    public void setMember(Member member) {
        this.member = member;
        if(!member.getOrders().contains(this)) {
            member.setOrder(this);
        }
    }

    public enum OrderStatus {
        ORDER_REQUEST(1, "주문 요청"),
        ORDER_CONFIRM(2, "주문 확정"),
        ORDER_COMPLETE(3, "주문 처리 완료"),
        ORDER_CANCEL(4, "주문 취소");

        @Getter
        private int stepNumber;

        @Getter
        private String stepDescription;

        OrderStatus(int stepNumber, String stepDescription) {
            this.stepNumber = stepNumber;
            this.stepDescription = stepDescription;
        }
    }
}