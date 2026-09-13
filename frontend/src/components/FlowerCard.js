import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { resolveImageUrl } from "../utils/imageUrl";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";
import "../styles/flowercard.css";

const stageColors = {
  Fresh: "success",
  "12 Hours": "info",
  "18 Hours": "warning",
  "24 Hours": "warning",
  "Clearance Sale": "danger",
};

export default function FlowerCard({ flower, onAddToCart, onWishlist }) {

  const navigate = useNavigate();
  const { user } = useAuth();

  const badgeColor = stageColors[flower.priceStage] || "secondary";
  const discounted = flower.currentPrice < flower.basePrice;
  const discountPercent = discounted
    ? Math.round(((flower.basePrice - flower.currentPrice) / flower.basePrice) * 100)
    : 0;

  const handleWishlist = async () => {

    if (!user) {
      alert("Please login first");
      navigate("/login");
      return;
    }

    if (onWishlist) {
      onWishlist(flower);
      return;
    }

    try {

      await api.post(`/customer/wishlist/${flower.id}`);
      alert("Added to Wishlist ❤️");

    } catch (error) {

      console.error(error);

      if (error.response?.status === 400) {
        alert("Flower already exists in wishlist.");
      } else {
        alert("Unable to add to wishlist.");
      }

    }
  };

  const handleAddToCart = async () => {

    if (onAddToCart) {
      onAddToCart(flower);
      return;
    }

    if (!user) {
      alert("Please login first");
      navigate("/login");
      return;
    }

    try {

      await api.post("/customer/cart", {
        flowerId: flower.id,
        quantity: 1,
      });

      alert("Flower added to cart 🌸");

    } catch (error) {

      console.error(error);
      alert("Unable to add flower to cart.");

    }
  };

  return (
    <div className="col-md-4 col-lg-3 mb-4">

      <div className="bc-flower-card">

        <div className="bc-flower-card__media">
          <Link to={`/flowers/${flower.id}`}>
            <img
              src={
                resolveImageUrl(flower.images?.[0]) ||
                "https://via.placeholder.com/300x200?text=Flower"
              }
              className="bc-flower-card__image"
              alt={flower.name}
            />
          </Link>

          <button
            className="bc-flower-card__wishlist"
            onClick={handleWishlist}
            aria-label="Add to wishlist"
            title="Add to wishlist"
          >
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8Z" />
            </svg>
          </button>

          <span className={`bc-flower-card__stage bg-${badgeColor}`}>
            {flower.priceStage}
          </span>

          {discounted && (
            <span className="bc-flower-card__discount">{discountPercent}% OFF</span>
          )}
        </div>

        <div className="bc-flower-card__body">

          <h3 className="bc-flower-card__name">
            <Link to={`/flowers/${flower.id}`}>{flower.name}</Link>
          </h3>

          <Link
            to={`/shops/${flower.sellerId}`}
            className="bc-flower-card__seller"
          >
            🌸 {flower.shopName}
          </Link>

          {flower.rating != null && (
            <span className="bc-flower-card__rating">
              ★ {Number(flower.rating).toFixed(1)}
            </span>
          )}

          {flower.qualityScore != null && (
            <span
              className={`bc-flower-card__quality ${
                flower.qualityScore >= 70
                  ? "is-good"
                  : flower.qualityScore >= 40
                  ? "is-mid"
                  : "is-low"
              }`}
              title={flower.qualityVerdict}
            >
              AI Quality {flower.qualityScore}/100
            </span>
          )}

          <div className="bc-flower-card__price-row">
            <span className="bc-flower-card__price">₹{flower.currentPrice}</span>
            {discounted && (
              <span className="bc-flower-card__price-original">₹{flower.basePrice}</span>
            )}
            <span className="bc-flower-card__unit">/ {flower.unit}</span>
          </div>

          <p className="bc-flower-card__stock">
            Available: {flower.quantity} {flower.unit}
          </p>

          <div className="bc-flower-card__actions">
            <button
              className="bc-flower-card__add-btn"
              disabled={flower.outOfStock || !flower.available}
              onClick={handleAddToCart}
            >
              {flower.outOfStock ? "Out of Stock" : "Add to Cart"}
            </button>

            <Link to={`/flowers/${flower.id}`} className="bc-flower-card__view-btn">
              View Details
            </Link>
          </div>

        </div>

      </div>

    </div>
  );
}