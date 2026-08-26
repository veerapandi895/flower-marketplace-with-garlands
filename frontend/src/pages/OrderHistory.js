import React, { useEffect, useState } from "react";
import api from "../api/axios";
import OrderTimeline from "../components/OrderTimeline";
import "../styles/orderhistory.css";

const statusColors = {
  PLACED: "neutral",
  ACCEPTED: "info",
  PREPARING: "primary",
  PACKED: "warning",
  READY: "warning",
  COMPLETED: "success",
  REJECTED: "danger",
  CANCELLED: "danger",
};

export default function OrderHistory() {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    api.get("/customer/orders").then((res) => setOrders(res.data));
  }, []);

  return (
    <div className="bc-orders">
      <h1 className="bc-orders__title">My Orders</h1>

      {orders.length === 0 && (
        <div className="bc-orders__empty">
          <span className="bc-orders__empty-icon">📦</span>
          <p>You haven't placed any orders yet.</p>
        </div>
      )}

      {orders.map((order) => (
        <div key={order.id} className="bc-order-card">

          <div className="bc-order-card__header">
            <h2>Order #{order.id}</h2>
            <span className={`bc-order-card__status bc-order-card__status--${statusColors[order.status] || "neutral"}`}>
              {order.status}
            </span>
          </div>

          <p className="bc-order-card__seller">Seller: {order.seller?.name}</p>

          <ul className="bc-order-card__items">
            {order.items?.map((item) => (
              <li key={item.id}>
                <span>{item.flower?.name || item.garland?.name} × {item.quantity}</span>
                <span>₹{item.priceAtPurchase}</span>
              </li>
            ))}
          </ul>

          <div className="bc-order-card__timeline">
            <OrderTimeline order={order} />
          </div>

          <div className="bc-order-card__totals">
            <span>Subtotal: ₹{order.subtotal}</span>
            <span>Discount: ₹{order.discountAmount}</span>
            <span className="bc-order-card__total-amount">Total: ₹{order.totalAmount}</span>
          </div>

        </div>
      ))}
    </div>
  );
}