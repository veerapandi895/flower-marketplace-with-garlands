package com.flowermarket.controller;

import com.flowermarket.entity.Wishlist;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/wishlist")
@RequiredArgsConstructor
public class CustomerWishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<Wishlist>> get(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(wishlistService.getForCustomer(principal.getUser()));
    }

    @PostMapping("/{flowerId}")
    public ResponseEntity<Wishlist> add(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long flowerId) {
        return ResponseEntity.ok(wishlistService.add(principal.getUser(), flowerId));
    }

    @DeleteMapping("/{flowerId}")
    public ResponseEntity<Map<String, String>> remove(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long flowerId) {
        wishlistService.remove(principal.getUser(), flowerId);
        return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
    }

    @PostMapping("/garland/{garlandId}")
    public ResponseEntity<Wishlist> addGarland(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long garlandId) {
        return ResponseEntity.ok(wishlistService.addGarland(principal.getUser(), garlandId));
    }

    @DeleteMapping("/garland/{garlandId}")
    public ResponseEntity<Map<String, String>> removeGarland(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long garlandId) {
        wishlistService.removeGarland(principal.getUser(), garlandId);
        return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
    }
}
