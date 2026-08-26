import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import FlowerCard from "../components/FlowerCard";
import GarlandCard from "../components/GarlandCard";
import HeroSlider from "../components/HeroSlider";
import { resolveImageUrl } from "../utils/imageUrl";
import { useAuth } from "../context/AuthContext";
import "../styles/home.css";

export default function Home() {
  const [categories, setCategories] = useState([]);
  const [garlandCategories, setGarlandCategories] = useState([]);
  const [festivals, setFestivals] = useState([]);
  const [popularFlowers, setPopularFlowers] = useState([]);
  const [popularGarlands, setPopularGarlands] = useState([]);
  const [topShops, setTopShops] = useState([]);
  const [recentFlowers, setRecentFlowers] = useState([]);
  const [testimonials, setTestimonials] = useState([]);
  const [newsletterEmail, setNewsletterEmail] = useState("");
  const [newsletterMessage, setNewsletterMessage] = useState("");

  const { user } = useAuth();

  useEffect(() => {
    api.get("/public/categories")
      .then((res) => setCategories(res.data))
      .catch(() => {});

    api.get("/public/garlands/categories")
      .then((res) => setGarlandCategories(res.data))
      .catch(() => {});

    api.get("/public/festival-offers")
      .then((res) => setFestivals(res.data))
      .catch(() => {});

    api.get("/public/flowers/trending")
      .then((res) => setPopularFlowers(res.data.slice(0, 4)))
      .catch(() => {});

    api.get("/public/garlands/trending")
      .then((res) => setPopularGarlands(res.data.slice(0, 4)))
      .catch(() => {});

    api.get("/public/shops")
      .then((res) => {
        const sorted = [...res.data].sort(
          (a, b) => (b.rating || 0) - (a.rating || 0)
        );
        setTopShops(sorted.slice(0, 4));
      })
      .catch(() => {});

    api.get("/public/flowers/filter", {
      params: { sort: "newest" },
    })
      .then((res) => setRecentFlowers(res.data.slice(0, 4)))
      .catch(() => {});

    api.get("/public/testimonials")
      .then((res) => setTestimonials(res.data))
      .catch(() => {});
  }, []);

  const handleAddToCart = async (flower) => {
    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as Customer.");
      return;
    }

    try {
      await api.post("/customer/cart", {
        flowerId: flower.id,
        quantity: 1,
      });

      alert(`${flower.name} added to cart`);
    } catch (err) {
      alert(err.response?.data?.message || "Could not add to cart");
    }
  };

  const handleAddGarlandToCart = async (garland) => {
    if (!user || user.role !== "CUSTOMER") {
      alert("Please login as Customer.");
      return;
    }

    try {
      await api.post("/customer/cart", {
        garlandId: garland.id,
        quantity: 1,
      });

      alert(`${garland.name} added to cart`);
    } catch (err) {
      alert(err.response?.data?.message || "Could not add to cart");
    }
  };
  const handleNewsletterSubmit = async (e) => {
    e.preventDefault();

    try {
      const { data } = await api.post(
        "/public/newsletter/subscribe",
        {
          email: newsletterEmail,
        }
      );

      setNewsletterMessage(data.message);
      setNewsletterEmail("");
    } catch (err) {
      setNewsletterMessage(
        err.response?.data?.message ||
          "Could not subscribe right now."
      );
    }
  };

  return (
    <>
      <HeroSlider />

      <div className="bc-home">

        {/* Categories */}
        {(categories.length > 0 ||
          garlandCategories.length > 0) && (
          <section className="bc-section bc-fade-in">
            <div className="bc-section__header">
              <span className="bc-section__eyebrow">Explore</span>
              <h2 className="bc-section__title">Browse by Category</h2>
            </div>

            <div className="bc-chip-row">

              {categories.map((c) => (
                <Link
                  key={`fc-${c.id}`}
                  to={`/browse?categoryId=${c.id}`}
                  className="bc-chip bc-chip--primary"
                >
                  🌷 {c.name}
                </Link>
              ))}

              {garlandCategories.map((c) => (
                <Link
                  key={`gc-${c.id}`}
                  to={`/garlands?categoryId=${c.id}`}
                  className="bc-chip bc-chip--accent"
                >
                  💐 {c.name}
                </Link>
              ))}

            </div>
          </section>
        )}

        {/* Festival Offers */}
        {festivals.length > 0 && (
          <section className="bc-section bc-fade-in">
            <div className="bc-section__header">
              <span className="bc-section__eyebrow">Limited Time</span>
              <h2 className="bc-section__title">🎉 Festival Offers</h2>
            </div>

            <div className="row">

              {festivals.map((f) => (
                <div key={f.id} className="col-md-4 mb-4">
                  <div className="bc-offer-card">
                    <span className="bc-offer-card__discount">{f.discountPercent}% OFF</span>
                    <h3 className="bc-offer-card__title">{f.title}</h3>
                    <p className="bc-offer-card__desc">{f.description}</p>
                  </div>
                </div>
              ))}

            </div>
          </section>
        )}

        {/* Popular Flowers */}
        {popularFlowers.length > 0 && (
          <section className="bc-section bc-fade-in">
            <div className="bc-section__header bc-section__header--row">
              <div>
                <span className="bc-section__eyebrow">Trending Now</span>
                <h2 className="bc-section__title">Popular Flowers</h2>
              </div>
              <Link to="/browse" className="bc-view-all">
                View All
                <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 12h14" /><path d="m13 6 6 6-6 6" /></svg>
              </Link>
            </div>

            <div className="row">
              {popularFlowers.map((flower) => (
                <FlowerCard
                  key={flower.id}
                  flower={flower}
                  onAddToCart={handleAddToCart}
                />
              ))}
            </div>
          </section>
        )}

        {/* Popular Garlands */}
        {popularGarlands.length > 0 && (
          <section className="bc-section bc-fade-in">
            <div className="bc-section__header bc-section__header--row">
              <div>
                <span className="bc-section__eyebrow">Handpicked</span>
                <h2 className="bc-section__title">Popular Garlands</h2>
              </div>
              <Link to="/garlands" className="bc-view-all">
                View All
                <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 12h14" /><path d="m13 6 6 6-6 6" /></svg>
              </Link>
            </div>

            <div className="row">
              {popularGarlands.map((garland) => (
                <GarlandCard
                  key={garland.id}
                  garland={garland}
                  onAddToCart={handleAddGarlandToCart}
                />
              ))}
            </div>
          </section>
        )}

        {/* Top Rated Shops */}
        {topShops.length > 0 && (
          <section className="bc-section bc-fade-in">
            <div className="bc-section__header">
              <span className="bc-section__eyebrow">Trusted Sellers</span>
              <h2 className="bc-section__title">Top Rated Shops</h2>
            </div>

            <div className="row">
              {topShops.map((shop) => (
                <div key={shop.id} className="col-md-3 col-6 mb-4">
                  <Link
                    to={`/shops/${shop.seller?.id}`}
                    className="bc-shop-card"
                  >
                    {shop.logoUrl ? (
                      <img
                        src={resolveImageUrl(shop.logoUrl)}
                        alt={shop.shopName}
                        className="bc-shop-card__logo"
                      />
                    ) : (
                      <span className="bc-shop-card__logo bc-shop-card__logo--placeholder">🌸</span>
                    )}

                    <h3 className="bc-shop-card__name">{shop.shopName}</h3>

                    <span className="bc-shop-card__rating">
                      ⭐ {shop.rating?.toFixed?.(1) ?? shop.rating}
                    </span>
                  </Link>
                </div>
              ))}
            </div>
          </section>
        )}

        {/* Recently Added */}
        {recentFlowers.length > 0 && (
          <section className="bc-section bc-fade-in">
            <div className="bc-section__header">
              <span className="bc-section__eyebrow">Just In</span>
              <h2 className="bc-section__title">Recently Added</h2>
            </div>

            <div className="row">
              {recentFlowers.map((flower) => (
                <FlowerCard
                  key={flower.id}
                  flower={flower}
                  onAddToCart={handleAddToCart}
                />
              ))}
            </div>
          </section>
        )}

        {/* Why Choose Bloomcycle */}
        <section className="bc-section bc-fade-in">
          <div className="bc-section__header">
            <span className="bc-section__eyebrow">Our Promise</span>
            <h2 className="bc-section__title">Why Choose Bloomcycle</h2>
          </div>

          <div className="bc-why-grid">
            <div className="bc-why-card">
              <span className="bc-why-card__icon">🌱</span>
              <h3>Farm Fresh</h3>
              <p>Flowers sourced directly from growers, with freshness tracked at every stage.</p>
            </div>

            <div className="bc-why-card">
              <span className="bc-why-card__icon">🤝</span>
              <h3>Trusted Sellers</h3>
              <p>Every shop on Bloomcycle is verified so you always know who you're buying from.</p>
            </div>

            <div className="bc-why-card">
              <span className="bc-why-card__icon">🚚</span>
              <h3>Same-Day Delivery</h3>
              <p>Order today, receive fresh flowers and garlands the same day in most areas.</p>
            </div>

            <div className="bc-why-card">
              <span className="bc-why-card__icon">🔒</span>
              <h3>Secure Payments</h3>
              <p>Every order is protected with secure checkout and clear order tracking.</p>
            </div>
          </div>
        </section>

        {/* Testimonials */}
        {testimonials.length > 0 && (
          <section className="bc-section bc-fade-in">
            <div className="bc-section__header">
              <span className="bc-section__eyebrow">Reviews</span>
              <h2 className="bc-section__title">What Customers Say</h2>
            </div>

            <div className="row">
              {testimonials.map((t) => (
                <div key={t.id} className="col-md-4 mb-4">
                  <div className="bc-testimonial-card">
                    <p className="bc-testimonial-card__stars">
                      {"★".repeat(t.rating)}
                      {"☆".repeat(Math.max(0, 5 - t.rating))}
                    </p>

                    <p className="bc-testimonial-card__quote">"{t.comment}"</p>

                    <span className="bc-testimonial-card__author">
                      — {t.customer?.name || "Verified Customer"}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </section>
        )}

        {/* Newsletter */}
        <section className="bc-newsletter bc-fade-in">
          <h2 className="bc-newsletter__title">Get Fresh Flower Deals</h2>
          <p className="bc-newsletter__subtitle">
            Subscribe for weekly offers and festival discounts.
          </p>

          <form
            className="bc-newsletter__form"
            onSubmit={handleNewsletterSubmit}
          >
            <input
              type="email"
              className="bc-newsletter__input"
              placeholder="you@example.com"
              value={newsletterEmail}
              onChange={(e) => setNewsletterEmail(e.target.value)}
              required
            />

            <button type="submit" className="bc-newsletter__btn">
              Subscribe
            </button>
          </form>

          {newsletterMessage && (
            <p className="bc-newsletter__message">{newsletterMessage}</p>
          )}
        </section>

      </div>
    </>
  );
}