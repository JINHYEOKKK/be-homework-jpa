package com.springboot.order.service;

import com.springboot.coffee.entity.Coffee;
import com.springboot.coffee.service.CoffeeService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.entity.Stamp;
import com.springboot.member.service.MemberService;
import com.springboot.order.entity.Order;
import com.springboot.order.entity.OrderCoffee;
import com.springboot.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final MemberService memberService;
    private final OrderRepository orderRepository;
    private final CoffeeService coffeeService;

    public OrderService(MemberService memberService,
                        OrderRepository orderRepository, CoffeeService coffeeService) {
        this.memberService = memberService;
        this.orderRepository = orderRepository;
        this.coffeeService = coffeeService;

    }

    public Order createOrder(Order order) {
        // 회원이 존재하는지 확인. Member에 직접 접근하는 것이 아닌, order를 통해 접근.
        // 이는 테이블 간의 관계 설정 덕에 가능.
        // 검증된 회원은 존재하는 회원이므로 그 회원의 스탬프를 가져온다.
        // 가져온 스탬프를 통해 스탬프 카운트를 가져온다.
        Member member = memberService.findVerifiedMember(order.getMember().getMemberId());
        Stamp stamp = member.getStamp();
        int totalStamp = stamp.getStampCount();

        // 새로운 OrderCoffee 리스트 객체에 order에 있는 검증된 회원의 OrderCoffees 리스트 정보를 할당.
        // for문을 통해 OrderCoffee를 순회하며, 커피 서비스에서 주문된 커피의 커피 정보를 검증 후 저장.
        // totalStamp가 주문된 커피 수량만큼 증가.
        // 최종적으로 증가된 회원의 스탬프를 저장.
        List<OrderCoffee> orderCoffeeList = order.getOrderCoffees();
        for(OrderCoffee orderCoffee : orderCoffeeList) {
            coffeeService.findVerifiedCoffee(orderCoffee.getCoffee().getCoffeeId());
            totalStamp += orderCoffee.getQuantity();
        }
        stamp.setStampCount(totalStamp);
        stamp.setModifiedAt(LocalDateTime.now());
        memberService.updateMember(member);

        // 주문을 데이터베이스에 저장
        return orderRepository.save(order);
    }

    // 메서드 추가
    public Order updateOrder(Order order) {
        Order findOrder = findVerifiedOrder(order.getOrderId());

        Optional.ofNullable(order.getOrderStatus())
                .ifPresent(orderStatus -> findOrder.setOrderStatus(orderStatus));
        findOrder.setModifiedAt(LocalDateTime.now());
        return orderRepository.save(findOrder);
    }

    public Order findOrder(long orderId) {
        return findVerifiedOrder(orderId);
    }

    public Page<Order> findOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size,
                Sort.by("orderId").descending()));
    }

    public void cancelOrder(long orderId) {
        Order findOrder = findVerifiedOrder(orderId);
        int step = findOrder.getOrderStatus().getStepNumber();

        // OrderStatus의 step이 2 이상일 경우(ORDER_CONFIRM)에는 주문 취소가 되지 않도록한다.
        if (step >= 2) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_ORDER);
        }
        findOrder.setOrderStatus(Order.OrderStatus.ORDER_CANCEL);
        findOrder.setModifiedAt(LocalDateTime.now());
        orderRepository.save(findOrder);
    }

    private Order findVerifiedOrder(long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order findOrder =
                optionalOrder.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
        return findOrder;
    }

    private void updateStamp(Order order) {
        Member member = memberService.findMember(order.getMember().getMemberId());
        int orderStampCount = order.getOrderCoffees().stream()
                .map(orderCoffee -> orderCoffee.getQuantity())
                .mapToInt(quantity -> quantity)
                .sum();
        Stamp stamp = member.getStamp();
        stamp.setStampCount(stamp.getStampCount() + orderStampCount);
        stamp.setModifiedAt(LocalDateTime.now());
        memberService.updateMember(member);

    }



}
