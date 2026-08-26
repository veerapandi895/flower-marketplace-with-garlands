package com.flowermarket.controller;

import com.flowermarket.dto.OrderStatusUpdateRequest;
import com.flowermarket.entity.Order;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/orders")
@RequiredArgsConstructor
public class SellerOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> myOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(orderService.getSellerOrders(principal.getUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getByIdForUser(principal.getUser(), id));
    }

    // covers Accept / Reject / Pack / Ready for Pickup / Complete Order
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@AuthenticationPrincipal UserPrincipal principal,
                                               @PathVariable Long id,
                                               @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(principal.getUser(), id, request.getStatus()));
    }
}
