import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axios";
import { resolveImageUrl } from "../utils/imageUrl";
import FlowerCard from "../components/FlowerCard";
import GarlandCard from "../components/GarlandCard";
import { useAuth } from "../context/AuthContext";

export default function ShopProfile() {
  const { sellerId } = useParams();
  const [shop, setShop] = useState(null);
  const [flowers, setFlowers] = useState([]);
  const [garlands, setGarlands] = useState([]);
  const { user } = useAuth();

  useEffect(() => {
    api.get(`/public/shops/${sellerId}`).then((res) => setShop(res.data));
    api.get(`/public/shops/${sellerId}/flowers`).then((res) => setFlowers(res.data));
    api.get(`/public/shops/${sellerId}/garlands`).then((res) => setGarlands(res.data));
  }, [sellerId]);

  const handleAddFlowerToCart = async (flower) => {
    if (!user || user.role !== "CUSTOMER") return alert("Please login as a customer to add items to your cart.");
    await api.post("/customer/cart", { flowerId: flower.id, quantity: 1 });
    alert(`${flower.name} added to cart!`);
  };

  const handleAddGarlandToCart = async (garland) => {
    if (!user || user.role !== "CUSTOMER") return alert("Please login as a customer to add items to your cart.");
    await api.post("/customer/cart", { garlandId: garland.id, quantity: 1 });
    alert(`${garland.name} added to cart!`);
  };

  if (!shop) return <div className="container mt-4">Loading shop...</div>;

  return (
    <div>
      {shop.bannerUrl && (
        <img
          src={resolveImageUrl(shop.bannerUrl)}
          alt={`${shop.shopName} banner`}
          className="w-100"
          style={{ height: 220, objectFit: "cover" }}
        />
      )}
      <div className="container mt-4">
        <div className="d-flex align-items-center gap-3 mb-4">
          {shop.logoUrl && (
            <img
              src={resolveImageUrl(shop.logoUrl)}
              alt={`${shop.shopName} logo`}
              className="rounded-circle border"
              style={{ width: 90, height: 90, objectFit: "cover" }}
            />
          )}
          <div>
            <h2 className="mb-0">{shop.shopName}</h2>
            <p className="text-muted mb-0">{shop.address}</p>
            {shop.businessHours && <p className="text-muted small mb-0">🕑 {shop.businessHours}</p>}
            <p className="mb-0">⭐ {shop.rating?.toFixed?.(1) ?? shop.rating} · {shop.totalOrders} orders completed</p>
          </div>
        </div>

        {shop.description && <p className="mb-4">{shop.description}</p>}

        {garlands.length > 0 && (
          <>
            <h4>Garlands from {shop.shopName}</h4>
            <div className="row mb-4">
              {garlands.map((g) => (
                <GarlandCard key={g.id} garland={g} onAddToCart={handleAddGarlandToCart} />
              ))}
            </div>
          </>
        )}

        {flowers.length > 0 && (
          <>
            <h4>Flowers from {shop.shopName}</h4>
            <div className="row mb-4">
              {flowers.map((f) => (
                <FlowerCard key={f.id} flower={f} onAddToCart={handleAddFlowerToCart} />
              ))}
            </div>
          </>
        )}

        {garlands.length === 0 && flowers.length === 0 && (
          <p className="text-muted">This shop has no active listings right now.</p>
        )}
      </div>
    </div>
  );
}
