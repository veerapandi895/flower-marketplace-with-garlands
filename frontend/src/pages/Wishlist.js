import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import { resolveImageUrl } from "../utils/imageUrl";
import "../styles/wishlist.css";

export default function Wishlist() {

  const [items, setItems] = useState([]);

  useEffect(() => {
    loadWishlist();
  }, []);

  const loadWishlist = async () => {
    try {

      const res = await api.get("/customer/wishlist");

      setItems(res.data);

    } catch (err) {

      console.error(err);

      alert("Unable to load wishlist.");

    }
  };

  const remove = async (item) => {

    try {

      if (item.garland) {

        await api.delete(
          `/customer/wishlist/garlands/${item.garland.id}`
        );

      } else {

        await api.delete(
          `/customer/wishlist/flowers/${item.flower.id}`
        );

      }

      loadWishlist();

    } catch (err) {

      console.error(err);

      alert("Unable to remove wishlist item.");

    }
  };

  return (

    <div className="bc-wishlist">

      <h1 className="bc-wishlist__title">❤️ My Wishlist</h1>

      {items.length === 0 && (
        <div className="bc-wishlist__empty">
          <span className="bc-wishlist__empty-icon">💐</span>
          <p>Your wishlist is empty.</p>
          <Link to="/browse" className="bc-wishlist__browse-btn">
            Browse Flowers
          </Link>
        </div>
      )}

      <div className="row">

        {items.map((item) => {

          const product = item.flower || item.garland;

          const image =
            resolveImageUrl(product?.images?.[0]) ||
            "https://via.placeholder.com/300x200?text=No+Image";

          const price =
            item.flower?.currentPrice ??
            item.flower?.basePrice ??
            item.garland?.price;

          const detailUrl = item.flower
            ? `/flowers/${item.flower.id}`
            : `/garlands/${item.garland.id}`;

          return (

            <div className="col-md-4 col-lg-3 mb-4" key={item.id}>

              <div className="bc-wishlist-card">

                <div className="bc-wishlist-card__media">
                  <img src={image} alt={product?.name} />

                  {item.garland && (
                    <span className="bc-wishlist-card__badge">Garland</span>
                  )}

                  <button
                    className="bc-wishlist-card__remove"
                    onClick={() => remove(item)}
                    aria-label="Remove from wishlist"
                    title="Remove from wishlist"
                  >
                    <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8Z" /></svg>
                  </button>
                </div>

                <div className="bc-wishlist-card__body">
                  <h3 className="bc-wishlist-card__name">{product?.name}</h3>
                  <p className="bc-wishlist-card__price">₹{price}</p>

                  <Link to={detailUrl} className="bc-wishlist-card__view-btn">
                    View Details
                  </Link>
                </div>

              </div>

            </div>

          );

        })}

      </div>

    </div>

  );
}