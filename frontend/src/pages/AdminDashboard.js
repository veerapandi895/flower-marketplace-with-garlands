import React, { useEffect, useState } from "react";
import api from "../api/axios";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from "recharts";

const PIE_COLORS = ["#0d6efd", "#198754", "#ffc107", "#fd7e14", "#6f42c1", "#20c997", "#dc3545", "#6c757d"];

export default function AdminDashboard() {
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    api.get("/admin/dashboard").then((res) => setSummary(res.data));
  }, []);

  if (!summary) return <div className="container mt-4">Loading...</div>;

  return (
    <div className="container mt-4">
      <h3>Admin Dashboard</h3>
      <div className="row">
        <StatCard label="Total Users" value={summary.totalUsers} />
        <StatCard label="Customers" value={summary.totalCustomers} />
        <StatCard label="Sellers" value={summary.totalSellers} />
        <StatCard label="Flowers" value={summary.totalFlowers} />
        <StatCard label="Garlands" value={summary.totalGarlands} />
        <StatCard label="Coupons" value={summary.totalCoupons} />
        <StatCard label="Festival Offers" value={summary.totalFestivalOffers} />
        <StatCard label="Monthly Revenue" value={`₹${summary.monthlyRevenue ?? 0}`} />
      </div>

      <div className="row mt-3">
        <div className="col-md-4 mb-3">
          <div className="card text-center shadow-sm h-100 border-success">
            <div className="card-body">
              <h6 className="text-muted">Waste Reduction</h6>
              <h3 className="text-success">{summary.wasteReduction?.wasteReductionPercent ?? 0}%</h3>
              <p className="small text-muted mb-0">
                {summary.wasteReduction?.soldBeforeWaste} sold vs {summary.wasteReduction?.expiredUnsold} expired unsold
                (of {summary.wasteReduction?.totalListed} listed)
              </p>
            </div>
          </div>
        </div>
        <div className="col-md-8 mb-3">
          <div className="card shadow-sm h-100">
            <div className="card-body">
              <h6>Order Status Breakdown</h6>
              <ResponsiveContainer width="100%" height={200}>
                <PieChart>
                  <Pie
                    data={summary.orderStatusBreakdown}
                    dataKey="count"
                    nameKey="status"
                    cx="50%"
                    cy="50%"
                    outerRadius={80}
                    label={(entry) => `${entry.status} (${entry.count})`}
                  >
                    {summary.orderStatusBreakdown?.map((_, idx) => (
                      <Cell key={idx} fill={PIE_COLORS[idx % PIE_COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-md-6 mb-4">
          <div className="card shadow-sm">
            <div className="card-body">
              <h6>Platform Revenue (last 6 months)</h6>
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
              <h6>Platform Orders (last 6 months)</h6>
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
        <div className="col-md-4 mb-4">
          <h5>Top Sellers</h5>
          <ol>
            {summary.topSellers?.map((s) => (
              <li key={s.sellerId}>{s.shopName} — ₹{s.revenue} ({s.rating?.toFixed?.(1) ?? s.rating}⭐)</li>
            ))}
          </ol>
        </div>
        <div className="col-md-4 mb-4">
          <h5>Top Flowers</h5>
          <ol>
            {summary.topSellingFlowers?.map((f) => (
              <li key={f.id}>{f.name} — {f.orderCount} orders</li>
            ))}
          </ol>
        </div>
        <div className="col-md-4 mb-4">
          <h5>Top Garlands</h5>
          <ol>
            {summary.topSellingGarlands?.map((g) => (
              <li key={g.id}>{g.name} — {g.orderCount} orders</li>
            ))}
          </ol>
        </div>
      </div>

      <h5>Low Stock Flowers (platform-wide)</h5>
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
