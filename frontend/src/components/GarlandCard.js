import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { resolveImageUrl } from "../utils/imageUrl";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";
import "../styles/garlandcard.css";

export default function GarlandCard({
  garland,
  onAddToCart,
  onWishlist,
}) {
  const navigate = useNavigate();
  const { user } = useAuth();

  const outOfStock =
    garland.status === "OUT_OF_STOCK" ||
    garland.availableQuantity <= 0;

  const displayPrice = garland.currentPrice ?? garland.price;
  const discounted = displayPrice < garland.price;
  const surged = displayPrice > garland.price;
  const discountPercent = discounted
    ? Math.round(((garland.price - displayPrice) / garland.price) * 100)
    : 0;

  const handleAddToCart = async () => {
    if (onAddToCart) {
      onAddToCart(garland);
      return;
    }

    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as Customer.");
      navigate("/login");
      return;
    }

    try {
      await api.post("/customer/cart", {
        garlandId: garland.id,
        quantity: 1,
      });

      alert("Garland added to cart 💐");
    } catch (err) {
      alert(err.response?.data?.message || "Unable to add to cart.");
    }
  };

  const handleWishlist = async () => {
    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as Customer.");
      navigate("/login");
      return;
    }

    if (onWishlist) {
      onWishlist(garland);
      return;
    }

    try {
      await api.post(`/customer/wishlist/garlands/${garland.id}`);
      alert("Added to Wishlist ❤️");
    } catch (err) {
      if (err.response?.status === 400) {
        alert("Already in wishlist.");
      } else {
        alert(err.response?.data?.message || "Unable to add to wishlist.");
      }
    }
  };

  return (
    <div className="col-md-4 col-lg-3 mb-4">
      <div className="bc-garland-card">

        <div className="bc-garland-card__media">
          <Link to={`/garlands/${garland.id}`}>
            <img
              src={
                resolveImageUrl(garland.images?.[0]) ||
                "https://via.placeholder.com/300x200?text=Garland"
              }
              className="bc-garland-card__image"
              alt={garland.name}
            />
          </Link>

          <button
            className="bc-garland-card__wishlist"
            onClick={handleWishlist}
            aria-label="Add to wishlist"
            title="Add to wishlist"
          >
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8Z" />
            </svg>
          </button>

          {garland.categoryName && (
            <span className="bc-garland-card__category">{garland.categoryName}</span>
          )}

          {discounted && (
            <span className="bc-garland-card__discount">{discountPercent}% OFF</span>
          )}
        </div>

        <div className="bc-garland-card__body">

          <h3 className="bc-garland-card__name">
            <Link to={`/garlands/${garland.id}`}>{garland.name}</Link>
          </h3>

          <Link
            to={`/shops/${garland.sellerId}`}
            className="bc-garland-card__seller"
          >
            💐 {garland.shopName}
          </Link>

          {garland.flowerComposition && (
            <p className="bc-garland-card__composition">{garland.flowerComposition}</p>
          )}

          <div className="bc-garland-card__price-row">
            {(discounted || surged) && (
              <span className="bc-garland-card__price-original">₹{garland.price}</span>
            )}
            <span className="bc-garland-card__price">₹{displayPrice}</span>
            {garland.weightGrams && (
              <span className="bc-garland-card__unit">/ {garland.weightGrams}g</span>
            )}
          </div>

          <p className="bc-garland-card__stock">
            Available: {garland.availableQuantity}
          </p>

          <div className="bc-garland-card__actions">
            <button
              className="bc-garland-card__add-btn"
              disabled={outOfStock}
              onClick={handleAddToCart}
            >
              {outOfStock ? "Out of Stock" : "Add to Cart"}
            </button>

            <Link to={`/garlands/${garland.id}`} className="bc-garland-card__view-btn">
              View Details
            </Link>
          </div>

        </div>

      </div>
    </div>
  );
}