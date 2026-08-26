package com.flowermarket.controller;

import com.flowermarket.dto.CheckoutRequest;
import com.flowermarket.entity.Order;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<List<Order>> checkout(@AuthenticationPrincipal UserPrincipal principal,
                                                 @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(orderService.checkout(principal.getUser(), request));
    }

    @GetMapping
    public ResponseEntity<List<Order>> myOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(orderService.getCustomerOrders(principal.getUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getByIdForUser(principal.getUser(), id));
    }
}
