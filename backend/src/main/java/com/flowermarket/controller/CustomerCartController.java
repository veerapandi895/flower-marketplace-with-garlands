package com.flowermarket.controller;

import com.flowermarket.dto.CartItemRequest;
import com.flowermarket.entity.CartItem;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/cart")
@RequiredArgsConstructor
public class CustomerCartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        List<CartItem> items = cartService.getCart(principal.getUser());
        double total = cartService.getCartTotal(principal.getUser());
        return ResponseEntity.ok(Map.of("items", items, "total", total));
    }

    @PostMapping
    public ResponseEntity<CartItem> add(@AuthenticationPrincipal UserPrincipal principal,
                                         @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(principal.getUser(), request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CartItem> updateQuantity(@AuthenticationPrincipal UserPrincipal principal,
                                                    @PathVariable Long id,
                                                    @RequestParam Double quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(principal.getUser(), id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remove(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable Long id) {
        cartService.removeItem(principal.getUser(), id);
        return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> clear(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getUser());
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }
}
