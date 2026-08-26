import React, { useEffect, useState } from "react";
import api from "../api/axios";
import GarlandCard from "../components/GarlandCard";
import { useAuth } from "../context/AuthContext";

export default function Garlands() {
  const [garlands, setGarlands] = useState([]);
  const [categories, setCategories] = useState([]);
  const [shops, setShops] = useState([]);
  const [query, setQuery] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [sellerId, setSellerId] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [sortBy, setSortBy] = useState("");
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    api.get("/public/garlands/categories").then((res) => setCategories(res.data)).catch(() => {});
    api.get("/public/shops").then((res) => setShops(res.data)).catch(() => {});
  }, []);

  const runFilter = async () => {
    setLoading(true);
    try {
      const { data } = await api.get("/public/garlands/filter", {
        params: {
          categoryId: categoryId || undefined,
          minPrice: minPrice || undefined,
          maxPrice: maxPrice || undefined,
          sellerId: sellerId || undefined,
          sort: sortBy || undefined,
        },
      });
      setGarlands(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!query.trim()) runFilter();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [categoryId, sellerId, minPrice, maxPrice, sortBy]);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query.trim()) return runFilter();
    setLoading(true);
    try {
      const { data } = await api.get("/public/garlands/search", { params: { query, sort: sortBy || undefined } });
      setGarlands(data);
    } finally {
      setLoading(false);
    }
  };

  const clearFilters = () => {
    setQuery("");
    setCategoryId("");
    setSellerId("");
    setMinPrice("");
    setMaxPrice("");
    setSortBy("");
    runFilter();
  };

  const handleAddToCart = async (garland) => {
    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as a customer to add items to your cart.");
      return;
    }
    try {
      await api.post("/customer/cart", { garlandId: garland.id, quantity: 1 });
      alert(`${garland.name} added to cart!`);
    } catch (err) {
      alert(err.response?.data?.message || "Could not add to cart");
    }
  };

  const handleWishlist = async (garland) => {
    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as a customer to use your wishlist.");
      return;
    }
    try {
      await api.post(`/customer/wishlist/garland/${garland.id}`);
      alert(`${garland.name} added to wishlist!`);
    } catch (err) {
      alert(err.response?.data?.message || "Could not add to wishlist");
    }
  };

  return (
    <div className="container mt-4">
      <div className="p-4 mb-4 bg-light rounded-3 text-center">
        <h1>🌸 Garlands</h1>
        <p className="text-muted">Wedding, Temple, Reception, VIP and custom garlands, made fresh to order.</p>
      </div>

      <form className="row g-2 mb-3" onSubmit={handleSearch}>
        <div className="col-md-4">
          <input
            className="form-control"
            placeholder="Search garlands (e.g. Wedding, Rose)"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>
        <div className="col-md-2">
          <select className="form-select" value={categoryId} onChange={(e) => { setQuery(""); setCategoryId(e.target.value); }}>
            <option value="">All Categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        </div>
        <div className="col-md-2">
          <select className="form-select" value={sellerId} onChange={(e) => { setQuery(""); setSellerId(e.target.value); }}>
            <option value="">All Sellers</option>
            {shops.map((s) => (
              <option key={s.seller?.id} value={s.seller?.id}>{s.shopName}</option>
            ))}
          </select>
        </div>
        <div className="col-md-2">
          <select className="form-select" value={sortBy} onChange={(e) => { setQuery(""); setSortBy(e.target.value); }}>
            <option value="">Sort By</option>
            <option value="price_asc">Price: Low to High</option>
            <option value="price_desc">Price: High to Low</option>
            <option value="newest">Newest</option>
            <option value="popular">Popular</option>
          </select>
        </div>
        <div className="col-md-2">
          <button className="btn btn-success w-100" type="submit">Search</button>
        </div>
        <div className="col-md-3">
          <input
            type="number"
            className="form-control"
            placeholder="Min price (₹)"
            value={minPrice}
            onChange={(e) => { setQuery(""); setMinPrice(e.target.value); }}
          />
        </div>
        <div className="col-md-3">
          <input
            type="number"
            className="form-control"
            placeholder="Max price (₹)"
            value={maxPrice}
            onChange={(e) => { setQuery(""); setMaxPrice(e.target.value); }}
          />
        </div>
        <div className="col-md-2">
          <button type="button" className="btn btn-outline-secondary w-100" onClick={clearFilters}>
            Clear Filters
          </button>
        </div>
      </form>

      {loading ? (
        <p>Loading garlands...</p>
      ) : (
        <div className="row">
          {garlands.length === 0 && <p>No garlands found.</p>}
          {garlands.map((garland) => (
            <GarlandCard
              key={garland.id}
              garland={garland}
              onAddToCart={handleAddToCart}
              onWishlist={handleWishlist}
            />
          ))}
        </div>
      )}
    </div>
  );
}
