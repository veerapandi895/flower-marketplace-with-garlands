import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import { resolveImageUrl } from "../utils/imageUrl";

export default function GarlandDetail() {

  const { id } = useParams();

  const [garland, setGarland] = useState(null);
  const [quantity, setQuantity] = useState(1);

  const { user } = useAuth();

  useEffect(() => {
    api.get(`/public/garlands/${id}`)
      .then((res) => setGarland(res.data));
  }, [id]);

  const handleAddToCart = async () => {

    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as Customer.");
      return;
    }

    try {

      await api.post("/customer/cart", {
        garlandId: garland.id,
        quantity,
      });

      alert("Garland added to cart 💐");

    } catch (err) {

      alert(err.response?.data?.message || "Unable to add to cart.");

    }

  };

  const handleWishlist = async () => {

    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as Customer.");
      return;
    }

    try {

      await api.post(`/customer/wishlist/garlands/${garland.id}`);

      alert("Added to Wishlist ❤️");

    } catch (err) {

      if (err.response?.status === 400) {
        alert("Already exists in wishlist.");
      } else {
        alert(err.response?.data?.message || "Unable to add to wishlist.");
      }

    }

  };

  if (!garland) {
    return (
      <div className="container mt-4">
        Loading...
      </div>
    );
  }

  const outOfStock =
    garland.status === "OUT_OF_STOCK" ||
    garland.availableQuantity <= 0;

  return (

    <div className="container mt-4">

      <div className="row">

        <div className="col-md-6">

          <img
            src={
              resolveImageUrl(garland.images?.[0]) ||
              "https://via.placeholder.com/500x350?text=Garland"
            }
            alt={garland.name}
            className="img-fluid rounded"
          />

          {garland.images?.length > 1 && (

            <div className="d-flex flex-wrap gap-2 mt-3">

              {garland.images.slice(1).map((img, index) => (

                <img
                  key={index}
                  src={resolveImageUrl(img)}
                  alt=""
                  className="rounded"
                  style={{
                    width: 80,
                    height: 80,
                    objectFit: "cover",
                  }}
                />

              ))}

            </div>

          )}

        </div>

        <div className="col-md-6">

          <h2>{garland.name}</h2>

          <p className="text-muted">
            Sold by {garland.shopName}
          </p>

          {garland.categoryName && (

            <span className="badge bg-secondary mb-2">
              {garland.categoryName}
            </span>

          )}

          <h3>

            {garland.currentPrice != null &&
              garland.currentPrice !== garland.price && (

                <span className="text-decoration-line-through text-muted me-2">
                  ₹{garland.price}
                </span>

            )}

            ₹{garland.currentPrice ?? garland.price}

            {garland.weightGrams && (

              <small className="text-muted">
                {" "}
                / {garland.weightGrams}g
              </small>

            )}

          </h3>

          <p>{garland.description}</p>

          <p>
            <strong>Flower Composition :</strong>{" "}
            {garland.flowerComposition}
          </p>

          {garland.estimatedPrepMinutes && (

            <p>
              <strong>Preparation Time :</strong>{" "}
              {garland.estimatedPrepMinutes} minutes
            </p>

          )}

          <p>
            Available : {garland.availableQuantity}
          </p>

          <div className="d-flex gap-2 align-items-center">

            <input
              type="number"
              min="1"
              max={garland.availableQuantity}
              value={quantity}
              onChange={(e) =>
                setQuantity(Number(e.target.value))
              }
              className="form-control"
              style={{ width: "100px" }}
            />

            <button
              className="btn btn-success"
              disabled={outOfStock}
              onClick={handleAddToCart}
            >
              {outOfStock ? "Out of Stock" : "Add to Cart"}
            </button>

            <button
              className="btn btn-outline-danger"
              onClick={handleWishlist}
            >
              ❤️ Wishlist
            </button>

          </div>

        </div>

      </div>

    </div>

  );

}