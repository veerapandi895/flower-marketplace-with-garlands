import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import { resolveImageUrl } from "../utils/imageUrl";

export default function FlowerDetail() {

  const { id } = useParams();

  const [flower, setFlower] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [comparisons, setComparisons] = useState([]);
  const [quantity, setQuantity] = useState(1);

  const { user } = useAuth();

  useEffect(() => {

    api.get(`/public/flowers/${id}`).then((res) => {

      setFlower(res.data);

      api
        .get("/public/flowers/compare", {
          params: {
            name: res.data.name,
          },
        })
        .then((r) => setComparisons(r.data));

    });

    api
      .get(`/public/reviews/flower/${id}`)
      .then((res) => setReviews(res.data));

  }, [id]);

  const handleAddToCart = async () => {

    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as a customer.");
      return;
    }

    try {

      await api.post("/customer/cart", {
        flowerId: flower.id,
        quantity,
      });

      alert("Added to cart 🌸");

    } catch (err) {

      alert(err.response?.data?.message || "Could not add to cart");

    }
  };

  const handleWishlist = async () => {

    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as a customer.");
      return;
    }

    try {

      await api.post(`/customer/wishlist/flowers/${flower.id}`);

      alert("Added to Wishlist ❤️");

    } catch (err) {

      if (err.response?.status === 400) {
        alert("Flower already exists in wishlist.");
      } else {
        alert(err.response?.data?.message || "Unable to add to wishlist");
      }

    }
  };

  if (!flower) {
    return (
      <div className="container mt-4">
        Loading...
      </div>
    );
  }

  return (

    <div className="container mt-4">

      <div className="row">

        <div className="col-md-6">

          <img
            src={
              resolveImageUrl(flower.images?.[0]) ||
              "https://via.placeholder.com/500x350?text=Flower"
            }
            className="img-fluid rounded"
            alt={flower.name}
          />

        </div>

        <div className="col-md-6">

          <h2>{flower.name}</h2>

          <p className="text-muted">
            Sold by {flower.shopName}
          </p>

          <span className="badge bg-info mb-2">
            {flower.priceStage}
          </span>

          {flower.qualityScore != null && (

            <div className="mb-2">

              <span
                className={`badge ${
                  flower.qualityScore >= 70
                    ? "bg-success"
                    : flower.qualityScore >= 40
                    ? "bg-warning text-dark"
                    : "bg-danger"
                }`}
              >
                AI Quality Score : {flower.qualityScore}/100
              </span>

              <p className="small text-muted mt-1 mb-0">
                {flower.qualityVerdict}
              </p>

            </div>

          )}

          <h3>

            {flower.currentPrice < flower.basePrice && (

              <span className="text-decoration-line-through text-muted me-2">
                ₹{flower.basePrice}
              </span>

            )}

            ₹{flower.currentPrice}

            <small className="text-muted">
              {" "}
              / {flower.unit}
            </small>

          </h3>

          <p>{flower.description}</p>

          <p>
            Available : {flower.quantity} {flower.unit}
          </p>

          <div className="d-flex align-items-center gap-2 mb-3">

            <input
              type="number"
              className="form-control"
              style={{ width: "100px" }}
              min="1"
              max={flower.quantity}
              value={quantity}
              onChange={(e) =>
                setQuantity(Number(e.target.value))
              }
            />

            <button
              className="btn btn-success"
              disabled={flower.outOfStock}
              onClick={handleAddToCart}
            >
              Add to Cart
            </button>

            <button
              className="btn btn-outline-danger"
              onClick={handleWishlist}
            >
              ❤️ Add to Wishlist
            </button>

          </div>

        </div>

      </div>

      {comparisons.length > 1 && (

        <div className="mt-5">

          <h4>Compare Prices Across Shops</h4>

          <table className="table table-bordered">

            <thead>

              <tr>

                <th>Shop</th>
                <th>Price</th>

              </tr>

            </thead>

            <tbody>

              {comparisons.map((c) => (

                <tr
                  key={c.id}
                  className={
                    c.id === flower.id
                      ? "table-success"
                      : ""
                  }
                >

                  <td>{c.shopName}</td>

                  <td>
                    ₹{c.currentPrice}
                  </td>

                </tr>

              ))}

            </tbody>

          </table>

        </div>

      )}

      <div className="mt-5">

        <h4>Reviews</h4>

        {reviews.length === 0 && (
          <p className="text-muted">
            No reviews yet.
          </p>
        )}

        {reviews.map((r) => (

          <div
            key={r.id}
            className="border-bottom py-2"
          >

            <strong>
              {"⭐".repeat(r.rating)}
            </strong>

            <p className="mb-0">
              {r.comment}
            </p>

          </div>

        ))}

      </div>

    </div>

  );
}