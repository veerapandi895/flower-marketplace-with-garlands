import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

export default function GlobalSearchBar() {
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [open, setOpen] = useState(false);
  const boxRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (boxRef.current && !boxRef.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    if (!query.trim()) {
      setSuggestions([]);
      return;
    }
    const timer = setTimeout(() => {
      api.get("/public/search/autocomplete", { params: { query } })
        .then((res) => {
          setSuggestions(res.data);
          setOpen(true);
        })
        .catch(() => setSuggestions([]));
    }, 250); // debounce
    return () => clearTimeout(timer);
  }, [query]);

  const goTo = (link) => {
    setOpen(false);
    setQuery("");
    navigate(link);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    setOpen(false);
    navigate(`/browse?query=${encodeURIComponent(query)}`);
  };

  const typeLabel = {
    FLOWER: "🌸 Flower",
    GARLAND: "💐 Garland",
    CATEGORY: "📁 Category",
    GARLAND_CATEGORY: "📁 Garland Category",
    SHOP: "🏬 Shop",
  };

  return (
    <div ref={boxRef} className="position-relative flex-grow-1 mx-3" style={{ maxWidth: 420 }}>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          className="form-control"
          placeholder="Search flowers, garlands, shops..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => suggestions.length > 0 && setOpen(true)}
        />
      </form>
      {open && suggestions.length > 0 && (
        <div className="list-group position-absolute w-100 shadow" style={{ zIndex: 1000, top: "100%" }}>
          {suggestions.map((s, idx) => (
            <button
              type="button"
              key={`${s.type}-${s.id}-${idx}`}
              className="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
              onClick={() => goTo(s.link)}
            >
              <span>{s.name}</span>
              <small className="text-muted">{typeLabel[s.type] || s.type}</small>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
