import React, { useState } from "react";
import api from "../api/axios";

export default function AdminReports() {
  const [date, setDate] = useState("");
  const [month, setMonth] = useState("");
  const [year, setYear] = useState(new Date().getFullYear());
  const [wasteFrom, setWasteFrom] = useState("");
  const [wasteTo, setWasteTo] = useState("");
  const [result, setResult] = useState(null);

  const fetchDaily = async () => {
    const { data } = await api.get("/admin/reports/daily", { params: { date } });
    setResult(data);
  };
  const fetchMonthly = async () => {
    const { data } = await api.get("/admin/reports/monthly", { params: { month } });
    setResult(data);
  };
  const fetchYearly = async () => {
    const { data } = await api.get("/admin/reports/yearly", { params: { year } });
    setResult(data);
  };
  const fetchWaste = async () => {
    const { data } = await api.get("/admin/reports/waste-reduction", { params: { from: wasteFrom, to: wasteTo } });
    setResult(data);
  };

  return (
    <div className="container mt-4">
      <h3>Reports</h3>

      <div className="row mb-4">
        <div className="col-md-3">
          <h6>Daily Sales</h6>
          <input type="date" className="form-control mb-2" value={date} onChange={(e) => setDate(e.target.value)} />
          <button className="btn btn-success btn-sm" onClick={fetchDaily}>View</button>
        </div>
        <div className="col-md-3">
          <h6>Monthly Sales</h6>
          <input type="month" className="form-control mb-2" value={month} onChange={(e) => setMonth(e.target.value)} />
          <button className="btn btn-success btn-sm" onClick={fetchMonthly}>View</button>
        </div>
        <div className="col-md-3">
          <h6>Yearly Sales</h6>
          <input type="number" className="form-control mb-2" value={year} onChange={(e) => setYear(e.target.value)} />
          <button className="btn btn-success btn-sm" onClick={fetchYearly}>View</button>
        </div>
        <div className="col-md-3">
          <h6>Waste Reduction</h6>
          <input type="date" className="form-control mb-1" placeholder="From" value={wasteFrom} onChange={(e) => setWasteFrom(e.target.value)} />
          <input type="date" className="form-control mb-2" placeholder="To" value={wasteTo} onChange={(e) => setWasteTo(e.target.value)} />
          <button className="btn btn-success btn-sm" onClick={fetchWaste}>View</button>
        </div>
      </div>

      {result && (
        <pre className="bg-light p-3 rounded">{JSON.stringify(result, null, 2)}</pre>
      )}
    </div>
  );
}
