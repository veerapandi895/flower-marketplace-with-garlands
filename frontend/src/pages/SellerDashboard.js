import React, { useEffect, useState } from "react";
import api from "../api/axios";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from "recharts";

export default function SellerDashboard() {
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    api.get("/seller/dashboard").then((res) => setSummary(res.data));
  }, []);

  if (!summary) return <div className="container mt-4">Loading...</div>;

  return (
    <div className="container mt-4">
      <h3>Seller Dashboard</h3>
      <div className="row">
        <StatCard label="Total Revenue" value={`₹${summary.totalRevenue ?? 0}`} />
        <StatCard label="Total Orders" value={summary.totalOrders} />
        <StatCard label="Pending Orders" value={summary.pendingOrders} />
        <StatCard label="Today's Orders" value={summary.todaysOrders} />
        <StatCard label="Monthly Orders" value={summary.monthlyOrders} />
        <StatCard label="Cancelled Orders" value={summary.cancelledOrders} />
        <StatCard label="Best Selling Flower" value={summary.bestSellingFlower} />
        <StatCard label="Best Selling Garland" value={summary.bestSellingGarland} />
      </div>

      <div className="row mt-3">
        <div className="col-md-6 mb-4">
          <div className="card shadow-sm">
            <div className="card-body">
              <h6>Monthly Revenue (last 6 months)</h6>
              <ResponsiveContainer width="100%" height={260}>
                <LineChart data={summary.monthlySales}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip formatter={(v) => `₹${v}`} />
                  <Line type="monotone" dataKey="revenue" stroke="#198754" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
        <div className="col-md-6 mb-4">
          <div className="card shadow-sm">
            <div className="card-body">
              <h6>Monthly Orders (last 6 months)</h6>
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={summary.monthlySales}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="orders" fill="#0d6efd" name="Orders" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-md-6 mb-4">
          <h5>Low Stock Flowers</h5>
          {summary.lowStockFlowers?.length === 0 ? (
            <p className="text-muted">No low stock flowers.</p>
          ) : (
            <ul>
              {summary.lowStockFlowers?.map((f) => (
                <li key={f.id}>{f.name} — {f.quantity} {f.unit} left</li>
              ))}
            </ul>
          )}
        </div>
        <div className="col-md-6 mb-4">
          <h5>Low Stock Garlands</h5>
          {summary.lowStockGarlands?.length === 0 ? (
            <p className="text-muted">No low stock garlands.</p>
          ) : (
            <ul>
              {summary.lowStockGarlands?.map((g) => (
                <li key={g.id}>{g.name} — {g.availableQuantity} left</li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <h5>Recent Orders</h5>
      <div className="table-responsive mb-4">
        <table className="table align-middle">
          <thead>
            <tr>
              <th>Order #</th><th>Customer</th><th>Items</th><th>Total</th><th>Status</th><th>Placed</th>
            </tr>
          </thead>
          <tbody>
            {summary.recentOrders?.length === 0 && (
              <tr><td colSpan="6" className="text-muted">No orders yet.</td></tr>
            )}
            {summary.recentOrders?.map((o) => (
              <tr key={o.id}>
                <td>#{o.id}</td>
                <td>{o.customerName}</td>
                <td>{o.itemCount}</td>
                <td>₹{o.totalAmount}</td>
                <td><span className="badge bg-info">{o.status}</span></td>
                <td>{new Date(o.placedAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function StatCard({ label, value }) {
  return (
    <div className="col-md-3 mb-3">
      <div className="card text-center shadow-sm">
        <div className="card-body">
          <h6 className="text-muted">{label}</h6>
          <h4>{value}</h4>
        </div>
      </div>
    </div>
  );
}
