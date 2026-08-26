import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "../styles/checkout.css";

export default function Checkout() {
  const navigate = useNavigate();

  const [address, setAddress] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("COD");
  const [couponCode, setCouponCode] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!address.trim()) {
      setError("Delivery address is required.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      await api.post("/customer/orders/checkout", {
        deliveryAddress: address,
        paymentMethod,
        couponCode: couponCode.trim() || null,
      });

      alert("🎉 Order placed successfully!");

      navigate("/orders");
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to place your order. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bc-checkout">
      <div className="bc-checkout__card">
        <h1 className="bc-checkout__title">Checkout</h1>

        {error && <div className="bc-checkout__error">{error}</div>}

        <form onSubmit={handleSubmit}>

          {/* Step 1: Delivery Address */}
          <div className="bc-checkout__step">
            <div className="bc-checkout__step-header">
              <span className="bc-checkout__step-number">1</span>
              <h2>Delivery Address</h2>
            </div>

            <textarea
              className="bc-checkout__textarea"
              rows="4"
              placeholder="Enter complete delivery address..."
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              required
            />
          </div>

          {/* Step 2: Payment Method */}
          <div className="bc-checkout__step">
            <div className="bc-checkout__step-header">
              <span className="bc-checkout__step-number">2</span>
              <h2>Payment Method</h2>
            </div>

            <div className="bc-checkout__payment-options">
              <label className={`bc-checkout__payment-option ${paymentMethod === "COD" ? "is-selected" : ""}`}>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="COD"
                  checked={paymentMethod === "COD"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                <span>💵 Cash on Delivery</span>
              </label>

              <label className={`bc-checkout__payment-option ${paymentMethod === "UPI" ? "is-selected" : ""}`}>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="UPI"
                  checked={paymentMethod === "UPI"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                <span>📱 UPI</span>
              </label>

              <label className={`bc-checkout__payment-option ${paymentMethod === "CARD" ? "is-selected" : ""}`}>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="CARD"
                  checked={paymentMethod === "CARD"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                <span>💳 Debit / Credit Card</span>
              </label>
            </div>
          </div>

          {/* Step 3: Coupon */}
          <div className="bc-checkout__step">
            <div className="bc-checkout__step-header">
              <span className="bc-checkout__step-number">3</span>
              <h2>Coupon Code</h2>
            </div>

            <input
              type="text"
              className="bc-checkout__input"
              placeholder="Optional"
              value={couponCode}
              onChange={(e) => setCouponCode(e.target.value)}
            />
          </div>

          <button
            type="submit"
            className="bc-checkout__submit-btn"
            disabled={loading}
          >
            {loading ? "Placing Order..." : "Place Order"}
          </button>

        </form>
      </div>
    </div>
  );
}