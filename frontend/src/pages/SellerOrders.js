import React, { useEffect, useState } from "react";
import api from "../api/axios";
import OrderTimeline from "../components/OrderTimeline";

const NEXT_STATUS = {
  PLACED: ["ACCEPTED", "REJECTED"],
  ACCEPTED: ["PREPARING", "REJECTED"],
  PREPARING: ["PACKED"],
  PACKED: ["READY"],
  READY: ["COMPLETED"],
};

export default function SellerOrders() {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = () => {
    api.get("/seller/orders").then((res) => setOrders(res.data));
  };

  const updateStatus = async (id, status) => {
    await api.patch(`/seller/orders/${id}/status`, { status });
    loadOrders();
  };

  return (
    <div className="container mt-4">
      <h3>Incoming Orders</h3>
      {orders.length === 0 && <p>No orders yet.</p>}
      {orders.map((order) => (
        <div key={order.id} className="card mb-3">
          <div className="card-body">
            <div className="d-flex justify-content-between">
              <h5>Order #{order.id} — {order.customer?.name}</h5>
              <span className="badge bg-secondary">{order.status}</span>
            </div>
            <ul className="list-unstyled">
              {order.items?.map((item) => (
                <li key={item.id}>{item.flower?.name || item.garland?.name} × {item.quantity} — ₹{item.priceAtPurchase}</li>
              ))}
            </ul>
            <OrderTimeline order={order} />
            <p><strong>Total: ₹{order.totalAmount}</strong> | Delivery to: {order.deliveryAddress}</p>
            <div className="d-flex gap-2">
              {(NEXT_STATUS[order.status] || []).map((next) => (
                <button key={next} className="btn btn-sm btn-outline-success" onClick={() => updateStatus(order.id, next)}>
                  Mark as {next}
                </button>
              ))}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
