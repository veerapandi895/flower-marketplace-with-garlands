package com.flowermarket.service;

import com.flowermarket.dto.CheckoutRequest;
import com.flowermarket.entity.*;
import com.flowermarket.enums.GarlandStatus;
import com.flowermarket.enums.OrderStatus;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.exception.ResourceNotFoundException;
import com.flowermarket.repository.FlowerRepository;
import com.flowermarket.repository.GarlandRepository;
import com.flowermarket.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final FlowerRepository flowerRepository;
    private final GarlandRepository garlandRepository;
    private final CartService cartService;
    private final CouponService couponService;
    private final NotificationService notificationService;

    /**
     * Places one order per seller if the cart spans multiple shops
     * (a customer's cart can contain flowers and/or garlands from different sellers).
     */
    @Transactional
    public List<Order> checkout(User customer, CheckoutRequest req) {
        List<CartItem> cartItems = cartService.getCart(customer);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        // group cart items by seller (a cart can span multiple shops)
        Map<Long, java.util.List<CartItem>> grouped = new HashMap<>();
        for (CartItem item : cartItems) {
            grouped.computeIfAbsent(sellerIdOf(item), k -> new java.util.ArrayList<>()).add(item);
        }

        List<Order> createdOrders = new java.util.ArrayList<>();

        for (Map.Entry<Long, java.util.List<CartItem>> entry : grouped.entrySet()) {
            List<CartItem> sellerItems = entry.getValue();
            User seller = sellerOf(sellerItems.get(0));

            double subtotal = sellerItems.stream()
                    .mapToDouble(i -> i.getPriceSnapshot() * i.getQuantity())
                    .sum();

            double discount = 0.0;
            if (req.getCouponCode() != null && !req.getCouponCode().isBlank()) {
                discount = couponService.applyCoupon(req.getCouponCode(), subtotal);
            }

            double total = Math.max(subtotal - discount, 0);

            Order order = Order.builder()
                    .customer(customer)
                    .seller(seller)
                    .subtotal(subtotal)
                    .discountAmount(discount)
                    .totalAmount(total)
                    .couponCode(req.getCouponCode())
                    .deliveryAddress(req.getDeliveryAddress())
                    .paymentMethod(req.getPaymentMethod())
                    .status(OrderStatus.PLACED)
                    .build();

            for (CartItem ci : sellerItems) {
                OrderItem orderItem;
                if (ci.getGarland() != null) {
                    Garland garland = ci.getGarland();
                    if (ci.getQuantity() > garland.getAvailableQuantity()) {
                        throw new BadRequestException("Insufficient stock for " + garland.getName());
                    }
                    garland.setAvailableQuantity((int) (garland.getAvailableQuantity() - ci.getQuantity()));
                    garland.setOrderCount((garland.getOrderCount() == null ? 0 : garland.getOrderCount()) + 1);
                    if (garland.getAvailableQuantity() <= 0) garland.setStatus(GarlandStatus.OUT_OF_STOCK);
                    garlandRepository.save(garland);

                    orderItem = OrderItem.builder()
                            .order(order)
                            .garland(garland)
                            .quantity(ci.getQuantity())
                            .priceAtPurchase(ci.getPriceSnapshot())
                            .build();
                } else {
                    Flower flower = ci.getFlower();
                    if (!flower.isAvailable() || flower.isOutOfStock()) {
                        throw new BadRequestException(flower.getName() + " is no longer available");
                    }
                    if (ci.getQuantity() > flower.getQuantity()) {
                        throw new BadRequestException("Insufficient stock for " + flower.getName());
                    }
                    flower.setQuantity(flower.getQuantity() - ci.getQuantity());
                    flower.setOrderCount((flower.getOrderCount() == null ? 0 : flower.getOrderCount()) + 1);
                    if (flower.getQuantity() <= 0) flower.setOutOfStock(true);
                    flowerRepository.save(flower);

                    orderItem = OrderItem.builder()
                            .order(order)
                            .flower(flower)
                            .quantity(ci.getQuantity())
                            .priceAtPurchase(ci.getPriceSnapshot())
                            .build();
                }
                order.getItems().add(orderItem);
            }

            order.getStatusHistory().add(OrderStatusHistory.builder().order(order).status(OrderStatus.PLACED).build());
            createdOrders.add(orderRepository.save(order));

            notificationService.notify(seller, "New Order Received",
                    "You have a new order worth ₹" + total + " from " + customer.getName());
            notificationService.notify(customer, "Order Placed",
                    "Your order with " + seller.getName() + " has been placed successfully.");
        }

        cartService.clearCart(customer);
        return createdOrders;
    }

    private Long sellerIdOf(CartItem item) {
        return item.getGarland() != null ? item.getGarland().getSeller().getId() : item.getFlower().getSeller().getId();
    }

    private User sellerOf(CartItem item) {
        return item.getGarland() != null ? item.getGarland().getSeller() : item.getFlower().getSeller();
    }

    public Order updateStatus(User seller, Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("This order does not belong to your shop");
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        order.getStatusHistory().add(OrderStatusHistory.builder().order(order).status(newStatus).build());

        if (newStatus == OrderStatus.ACCEPTED && order.getEstimatedDeliveryAt() == null) {
            order.setEstimatedDeliveryAt(LocalDateTime.now().plusMinutes(estimatePrepMinutes(order) + 30));
        }

        Order saved = orderRepository.save(order);

        notificationService.notify(order.getCustomer(), "Order Update",
                "Your order #" + order.getId() + " is now " + newStatus);

        return saved;
    }

    public List<Order> getCustomerOrders(User customer) {
        return orderRepository.findByCustomer(customer);
    }

    public List<Order> getSellerOrders(User seller) {
        return orderRepository.findBySeller(seller);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /** Longest item prep time in the order (garlands have estimatedPrepMinutes; flowers default to 20 mins handling). */
    private long estimatePrepMinutes(Order order) {
        return order.getItems().stream()
                .mapToLong(item -> {
                    if (item.getGarland() != null && item.getGarland().getEstimatedPrepMinutes() != null) {
                        return item.getGarland().getEstimatedPrepMinutes();
                    }
                    return 20L; // default flower handling/bunching time
                })
                .max()
                .orElse(20L);
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    /** Phase 12: used by the tracking endpoint - only the order's own customer or seller may view it. */
    public Order getByIdForUser(User user, Long id) {
        Order order = getById(id);
        boolean isOwner = order.getCustomer().getId().equals(user.getId()) || order.getSeller().getId().equals(user.getId());
        if (!isOwner) {
            throw new BadRequestException("You do not have access to this order");
        }
        return order;
    }
}
