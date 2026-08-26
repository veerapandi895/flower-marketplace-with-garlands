import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { resolveImageUrl } from "../utils/imageUrl";
import "../styles/cart.css";

export default function Cart() {
  const [items, setItems] = useState([]);
  const [total, setTotal] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    loadCart();
  }, []);

  const loadCart = async () => {
    const { data } = await api.get("/customer/cart");
    setItems(data.items);
    setTotal(data.total);
  };

  const updateQuantity = async (id, quantity) => {
    if (quantity <= 0) {
      await api.delete(`/customer/cart/${id}`);
    } else {
      await api.patch(`/customer/cart/${id}`, null, { params: { quantity } });
    }
    loadCart();
  };

  const removeItem = async (id) => {
    await api.delete(`/customer/cart/${id}`);
    loadCart();
  };

  const clearCart = async () => {
    await api.delete("/customer/cart");
    loadCart();
  };

  return (
    <div className="bc-cart">
      <h1 className="bc-cart__title">Your Cart</h1>

      {items.length === 0 ? (
        <div className="bc-cart__empty">
          <span className="bc-cart__empty-icon">🛒</span>
          <p>Your cart is empty.</p>
          <button className="bc-cart__browse-btn" onClick={() => navigate("/browse")}>
            Browse Flowers
          </button>
        </div>
      ) : (
        <div className="bc-cart__layout">

          {/* Cart items */}
          <div className="bc-cart__items">
            {items.map((item) => {
              const product = item.flower || item.garland;
              const isGarland = Boolean(item.garland);

              return (
                <div key={item.id} className="bc-cart-item">
                  <img
                    src={
                      resolveImageUrl(product?.images?.[0]) ||
                      "https://via.placeholder.com/120x120?text=Bloomcycle"
                    }
                    alt={product?.name}
                    className="bc-cart-item__image"
                  />

                  <div className="bc-cart-item__info">
                    <h3 className="bc-cart-item__name">
                      {product?.name}
                      {isGarland && <span className="bc-cart-item__badge">Garland</span>}
                    </h3>

                    {product?.shopName && (
                      <p className="bc-cart-item__seller">{product.shopName}</p>
                    )}

                    <p className="bc-cart-item__price">₹{item.priceSnapshot} each</p>

                    <div className="bc-cart-item__actions">
                      <div className="bc-cart-item__stepper">
                        <button
                          onClick={() => updateQuantity(item.id, item.quantity - 1)}
                          aria-label="Decrease quantity"
                        >
                          −
                        </button>
                        <input
                          type="number"
                          min="0"
                          value={item.quantity}
                          onChange={(e) => updateQuantity(item.id, Number(e.target.value))}
                        />
                        <button
                          onClick={() => updateQuantity(item.id, item.quantity + 1)}
                          aria-label="Increase quantity"
                        >
                          +
                        </button>
                      </div>

                      <button className="bc-cart-item__remove" onClick={() => removeItem(item.id)}>
                        Remove
                      </button>
                    </div>
                  </div>

                  <div className="bc-cart-item__subtotal">
                    ₹{(item.priceSnapshot * item.quantity).toFixed(2)}
                  </div>
                </div>
              );
            })}

            <button className="bc-cart__clear-btn" onClick={clearCart}>
              Clear Cart
            </button>
          </div>

          {/* Order summary */}
          <aside className="bc-cart__summary">
            <h2 className="bc-cart__summary-title">Order Summary</h2>

            <div className="bc-cart__summary-row">
              <span>Subtotal</span>
              <span>₹{total.toFixed(2)}</span>
            </div>

            <div className="bc-cart__summary-row bc-cart__summary-row--muted">
              <span>Delivery</span>
              <span>Calculated at checkout</span>
            </div>

            <div className="bc-cart__summary-divider"></div>

            <div className="bc-cart__summary-row bc-cart__summary-row--total">
              <span>Total</span>
              <span>₹{total.toFixed(2)}</span>
            </div>

            <button
              className="bc-cart__checkout-btn"
              onClick={() => navigate("/checkout")}
            >
              Proceed to Checkout
            </button>
          </aside>

        </div>
      )}
    </div>
  );
}