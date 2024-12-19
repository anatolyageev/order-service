package com.polarbookshop.orderservice.order.service;

import com.polarbookshop.orderservice.order.domain.Order;
import com.polarbookshop.orderservice.order.domain.OrderStatus;
import com.polarbookshop.orderservice.order.repo.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
/// Stereotype annotation that marks a class to be a service managed by Spring
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Flux<Order> getAllOrders() {  /// A Flux is used to publish multiple orders (0...N)
        return orderRepository.findAll();
    }

    public Mono<Order> submitOrder(String isbn, int quantity) {
        return Mono.just(buildRejectedOrder(isbn, quantity))   /// Creates a “Mono” out of an “Order” object
                .flatMap(orderRepository::save);               /// Saves the Order object produced asynchronously by the previous step of the reactive
                                                               /// stream into the database
    }

    public static Order buildRejectedOrder(String bookIsbn, int quantity)  /// When an order is rejected, we only specify ISBN, quantity, and status. Spring Data takes
    {                                                                       /// care of adding identifier, version, and audit metadata.
        return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
    }
}
