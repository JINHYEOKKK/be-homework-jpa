package com.springboot.order.mapper;

import com.springboot.coffee.entity.Coffee;
import com.springboot.member.entity.Member;
import com.springboot.order.dto.OrderCoffeeResponseDto;
import com.springboot.order.dto.OrderPatchDto;
import com.springboot.order.dto.OrderPostDto;
import com.springboot.order.dto.OrderResponseDto;
import com.springboot.order.entity.Order;
import com.springboot.order.entity.OrderCoffee;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
// '오더 포스트 dto'를 엔티티로
//    default Order orderPostDtoToOrder(OrderPostDto orderPostDto){
//        // 새로운 Order 객체를 생성
//        Order order = new Order();
//        // 새로운 Member 객체를 생성하고, orderPostDto에서 memberId를 설정
//        Member member = new Member();
//        member.setMemberId(orderPostDto.getMemberId()); // 이 맴버에는 ID만 있다
//        // OrderPostDto에서 OrderCoffee 리스트를 가져와서 OrderCoffee 객체로 변환
//        List<OrderCoffee> orderCoffees = orderPostDto.getOrderCoffees().stream()
//                        .map(orderCoffeeDto -> {
//                            // 새로운 OrderCoffee 객체를 생성하고 수량 설정
//                            OrderCoffee orderCoffee = new OrderCoffee();
//                            orderCoffee.setQuantity(orderCoffeeDto.getQuantity());
//                            // 새로운 Coffee 객체를 생성하고 COffeeId를 설정
//                            Coffee coffee = new Coffee();
//                            coffee.setCoffeeId(orderCoffeeDto.getCoffeeId());
//                            // OrderCoffee 객체레 Coffee 객체를 생성
//                            orderCoffee.setCoffee(coffee);
//                            // OrderCoffee 객체에 Order 객체를 설정
//                            orderCoffee.addOrder(order);
//                            // OrderCoffee 객체를 반환
//                            return orderCoffee;
//                        })
//                                .collect(Collectors.toList());
//
//        order.setMember(member);
//        return order;
//    }
    // '오더 포스트 dto'를 엔티티로 변환
    default Order orderPostDtoToOrder(OrderPostDto orderPostDto){
        // 새로운 Order 객체를 생성
        Order order = new Order();

        // 새로운 Member 객체를 생성하고, orderPostDto에서 memberId를 설정
        Member member = new Member();
        member.setMemberId(orderPostDto.getMemberId()); // 이 맴버에는 ID만 있다
        order.setMember(member); // Order 객체에 Member 객체를 설정

        // OrderPostDto에서 OrderCoffee 리스트를 가져와서 OrderCoffee 객체로 변환
        List<OrderCoffee> orderCoffees = orderPostDto.getOrderCoffees().stream()
                .map(orderCoffeeDto -> {
                    // 새로운 OrderCoffee 객체를 생성하고 수량 설정
                    OrderCoffee orderCoffee = new OrderCoffee();
                    orderCoffee.setQuantity(orderCoffeeDto.getQuantity());

                    // 새로운 Coffee 객체를 생성하고 CoffeeId를 설정
                    Coffee coffee = new Coffee();
                    coffee.setCoffeeId(orderCoffeeDto.getCoffeeId());

                    // OrderCoffee 객체에 Coffee 객체를 설정
                    orderCoffee.setCoffee(coffee);

                    // OrderCoffee 객체에 Order 객체를 설정
                    orderCoffee.setOrder(order); // `addOrder` 대신 `setOrder` 사용

                    // OrderCoffee 객체를 반환
                    return orderCoffee;
                })
                .collect(Collectors.toList());

        // Order 객체에 OrderCoffee 리스트를 설정
        order.setOrderCoffees(orderCoffees);

        return order;
    }



    Order orderPatchDtoToOrder(OrderPatchDto orderPatchDto);


    default OrderResponseDto orderToOrderResponseDto(Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
           orderResponseDto.setOrderId(order.getOrderId());
           orderResponseDto.setMemberId(order.getMember().getMemberId());
           orderResponseDto.setOrderStatus(order.getOrderStatus());
           List<OrderCoffeeResponseDto> orderCoffeeResponseDtos = order.getOrderCoffees().stream()
                   .map(orderCoffee -> orderCoffeeToOrderCoffeeResponseDto(orderCoffee))
                   .collect(Collectors.toList());
           orderResponseDto.setOrderCoffees(orderCoffeeResponseDtos);
           orderResponseDto.setCreatedAt(order.getCreatedAt());
           return orderResponseDto;
    }



    default OrderCoffeeResponseDto orderCoffeeToOrderCoffeeResponseDto(OrderCoffee orderCoffee) {
        OrderCoffeeResponseDto orderCoffeeResponseDto = new OrderCoffeeResponseDto(
                orderCoffee.getCoffee().getCoffeeId(),
                orderCoffee.getCoffee().getKorName(),
                orderCoffee.getCoffee().getEngName(),
                orderCoffee.getCoffee().getPrice(),
                orderCoffee.getQuantity()
        );
        return orderCoffeeResponseDto;
    }

    List<OrderResponseDto> ordersToOrderResponseDtos(List<Order> orders);


//    default List<OrderResponseDto> orderListToOrderResponseDtoList(List<Order> orders) {
//        return  orders.stream()
//                .map(order -> orderToOrderResponseDto(order))
//                .collect(Collectors.toList());
//    }



}
