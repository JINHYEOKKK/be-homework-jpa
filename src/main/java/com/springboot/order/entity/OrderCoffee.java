package com.springboot.order.entity;

import com.springboot.coffee.entity.Coffee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OrderCoffee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderCoffeeId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    private Order.OrderStatus orderStatus = Order.OrderStatus.ORDER_REQUEST;


    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "COFFEE_ID")
    private Coffee coffee;

    // Order 객체가 이 OrderCoffee 객체를 포함하고 있지 않으면, 추가해줌
    public void addOrder(Order order) {
        if (!order.getOrderCoffees().contains(this)) {
            order.addOrderCoffee(this);
        }
        this.order = order;
    }


    public enum orderStatus {
        ORDER_REQUEST(1, "주문 요청"),
        ORDER_CONFIRM(2, "주문 확정"),
        ORDER_COMPLETE(3, "주문 완료 "),
        ORDER_CANCEL(4, "주문 취소");

        @Getter
        private int stepNumber;
        @Getter
        private String stepDescription;

        orderStatus(int stepNumber, String stepDescription) {
            this.stepNumber = stepNumber;
            this.stepDescription = stepDescription;
        }
    }

}