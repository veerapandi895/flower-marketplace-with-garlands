import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import api from "../api/axios";
import FlowerCard from "../components/FlowerCard";
import { useAuth } from "../context/AuthContext";
import "../styles/browseflowers.css";

export default function BrowseFlowers() {
  const [flowers, setFlowers] = useState([]);
  const [categories, setCategories] = useState([]);
  const [shops, setShops] = useState([]);
  const [festivals, setFestivals] = useState([]);
  const [searchParams] = useSearchParams();

  const [query, setQuery] = useState(searchParams.get("query") || "");
  const [categoryId, setCategoryId] = useState(searchParams.get("categoryId") || "");
  const [sellerId, setSellerId] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [sortBy, setSortBy] = useState("");
  const [loading, setLoading] = useState(true);
  const [filtersOpen, setFiltersOpen] = useState(false);
  const { user } = useAuth();

  useEffect(() => {
    api.get("/public/categories").then((res) => setCategories(res.data)).catch(() => {});
    api.get("/public/shops").then((res) => setShops(res.data)).catch(() => {});
    api.get("/public/festival-offers").then((res) => setFestivals(res.data)).catch(() => {});
  }, []);

  useEffect(() => {
    if (query.trim()) {
      runSearch();
    } else {
      runFilter();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [categoryId, sellerId, minPrice, maxPrice, sortBy]);

  useEffect(() => {
    // initial load, respecting any query param from the global search bar
    if (query.trim()) runSearch();
    else runFilter();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const runFilter = async () => {
    setLoading(true);
    try {
      const { data } = await api.get("/public/flowers/filter", {
        params: {
          categoryId: categoryId || undefined,
          minPrice: minPrice || undefined,
          maxPrice: maxPrice || undefined,
          sellerId: sellerId || undefined,
          sort: sortBy || undefined,
        },
      });
      setFlowers(data);
    } finally {
      setLoading(false);
    }
  };

  const runSearch = async () => {
    setLoading(true);
    try {
      const { data } = await api.get("/public/flowers/search", { params: { query } });
      setFlowers(data);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (!query.trim()) return runFilter();
    runSearch();
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

  const handleAddToCart = async (flower) => {
    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as a customer to add items to your cart.");
      return;
    }
    try {
      await api.post("/customer/cart", { flowerId: flower.id, quantity: 1 });
      alert(`${flower.name} added to cart!`);
    } catch (err) {
      alert(err.response?.data?.message || "Could not add to cart");
    }
  };

  const handleWishlist = async (flower) => {
    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as a customer to use your wishlist.");
      return;
    }
    try {
      await api.post(`/customer/wishlist/${flower.id}`);
      alert(`${flower.name} added to wishlist!`);
    } catch (err) {
      alert(err.response?.data?.message || "Could not add to wishlist");
    }
  };

  const FilterFields = (
    <>
      <div className="bc-filter-group">
        <label className="bc-filter-group__label">Category</label>
        <select
          className="bc-filter-select"
          value={categoryId}
          onChange={(e) => { setQuery(""); setCategoryId(e.target.value); }}
        >
          <option value="">All Categories</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
      </div>

      <div className="bc-filter-group">
        <label className="bc-filter-group__label">Seller</label>
        <select
          className="bc-filter-select"
          value={sellerId}
          onChange={(e) => { setQuery(""); setSellerId(e.target.value); }}
        >
          <option value="">All Sellers</option>
          {shops.map((s) => (
            <option key={s.seller?.id} value={s.seller?.id}>{s.shopName}</option>
          ))}
        </select>
      </div>

      <div className="bc-filter-group">
        <label className="bc-filter-group__label">Price Range (₹)</label>
        <div className="bc-filter-group__range">
          <input
            type="number"
            className="bc-filter-input"
            placeholder="Min"
            value={minPrice}
            onChange={(e) => { setQuery(""); setMinPrice(e.target.value); }}
          />
          <span>–</span>
          <input
            type="number"
            className="bc-filter-input"
            placeholder="Max"
            value={maxPrice}
            onChange={(e) => { setQuery(""); setMaxPrice(e.target.value); }}
          />
        </div>
      </div>

      <button type="button" className="bc-filter-clear" onClick={clearFilters}>
        Clear Filters
      </button>
    </>
  );

  return (
    <div className="bc-browse">

      <div className="bc-browse__hero">
        <h1>🌸 Fresh Flowers, Fair Prices</h1>
        <p>Prices drop as flowers age — always the freshest deal.</p>
      </div>

      {festivals.length > 0 && (
        <div className="bc-browse__banner">
          🎉 {festivals.map((f) => f.title).join(" · ")} — festival pricing is active on eligible items!
        </div>
      )}

      <form className="bc-browse__search" onSubmit={handleSearch}>
        <input
          className="bc-browse__search-input"
          placeholder="Search flowers (e.g. Rose, Jasmine)"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <button className="bc-browse__search-btn" type="submit">Search</button>
      </form>

      <div className="bc-browse__layout">

        {/* Sidebar filters (desktop) */}
        <aside className="bc-browse__sidebar">
          <h3 className="bc-browse__sidebar-title">Filters</h3>
          {FilterFields}
        </aside>

        {/* Mobile filter drawer */}
        <div className={`bc-browse__drawer ${filtersOpen ? "is-open" : ""}`}>
          <div className="bc-browse__drawer-panel">
            <div className="bc-browse__drawer-header">
              <h3>Filters</h3>
              <button onClick={() => setFiltersOpen(false)} aria-label="Close filters">✕</button>
            </div>
            {FilterFields}
            <button className="bc-browse__drawer-apply" onClick={() => setFiltersOpen(false)}>
              Show Results
            </button>
          </div>
          <div className="bc-browse__drawer-backdrop" onClick={() => setFiltersOpen(false)}></div>
        </div>

        {/* Results */}
        <div className="bc-browse__results">

          <div className="bc-browse__results-header">
            <div>
              <span className="bc-browse__results-count">
                {loading ? "Loading..." : `${flowers.length} result${flowers.length === 1 ? "" : "s"}`}
              </span>
            </div>

            <div className="bc-browse__results-actions">
              <select
                className="bc-filter-select bc-browse__sort"
                value={sortBy}
                onChange={(e) => { setQuery(""); setSortBy(e.target.value); }}
              >
                <option value="">Sort By</option>
                <option value="price_asc">Price: Low to High</option>
                <option value="price_desc">Price: High to Low</option>
                <option value="newest">Newest</option>
                <option value="popular">Popular</option>
              </select>

              <button
                type="button"
                className="bc-browse__filter-toggle"
                onClick={() => setFiltersOpen(true)}
              >
                Filters
              </button>
            </div>
          </div>

          {loading ? (
            <p className="bc-browse__loading">Loading flowers...</p>
          ) : (
            <div className="row">
              {flowers.length === 0 && <p className="bc-browse__empty">No flowers found.</p>}
              {flowers.map((flower) => (
                <FlowerCard
                  key={flower.id}
                  flower={flower}
                  onAddToCart={handleAddToCart}
                  onWishlist={handleWishlist}
                />
              ))}
            </div>
          )}
        </div>

      </div>
    </div>
  );
}