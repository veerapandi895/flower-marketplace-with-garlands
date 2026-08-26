import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";
import { resolveImageUrl } from "../utils/imageUrl";
import GlobalSearchBar from "./GlobalSearchBar";
import NotificationBell from "./NotificationBell";
import "./Navbar.css";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [shopLogo, setShopLogo] = useState(null);
  const [navOpen, setNavOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);

  useEffect(() => {
    if (user?.role === "SELLER") {
      api.get("/seller/shop")
        .then((res) => setShopLogo(res.data.logoUrl || null))
        .catch(() => setShopLogo(null));
    } else {
      setShopLogo(null);
    }
  }, [user]);

  const handleLogout = () => {
    logout();
    navigate("/login");
    setNavOpen(false);
    setProfileOpen(false);
  };

  const closeMobileNav = () => setNavOpen(false);

  return (
    <header className="bc-navbar">
      {/* ===== Top row: brand, search, actions ===== */}
      <div className="bc-navbar__top">
        <div className="bc-navbar__container">
          <Link className="bc-navbar__brand" to="/" onClick={closeMobileNav}>
            {shopLogo ? (
              <img
                src={resolveImageUrl(shopLogo)}
                alt="Shop logo"
                className="bc-navbar__shop-logo"
              />
            ) : (
              <span className="bc-navbar__logo-mark">🌸</span>
            )}
            <span className="bc-navbar__brand-name">Bloomcycle</span>
          </Link>

          <div className="bc-navbar__search">
            <GlobalSearchBar />
          </div>

          <div className="bc-navbar__actions">
            {user ? (
              <>
                {user.role === "CUSTOMER" && (
                  <Link to="/wishlist" className="bc-navbar__icon-btn" title="Wishlist" aria-label="Wishlist">
                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8Z" />
                    </svg>
                  </Link>
                )}

                {user.role === "CUSTOMER" && (
                  <Link to="/cart" className="bc-navbar__icon-btn" title="Cart" aria-label="Cart">
                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z" />
                      <path d="M3 6h18" />
                      <path d="M16 10a4 4 0 0 1-8 0" />
                    </svg>
                  </Link>
                )}

                <div className="bc-navbar__icon-btn bc-navbar__bell">
                  <NotificationBell />
                </div>

                <div className="bc-navbar__profile">
                  <button
                    className="bc-navbar__profile-btn"
                    onClick={() => setProfileOpen((prev) => !prev)}
                    aria-haspopup="true"
                    aria-expanded={profileOpen}
                  >
                    <span className="bc-navbar__avatar">{user.name?.charAt(0)?.toUpperCase() || "U"}</span>
                    <span className="bc-navbar__profile-text">
                      <span className="bc-navbar__profile-name">{user.name}</span>
                      <span className="bc-navbar__profile-role">{user.role}</span>
                    </span>
                    <svg className="bc-navbar__chevron" viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="m6 9 6 6 6-6" />
                    </svg>
                  </button>

                  {profileOpen && (
                    <div className="bc-navbar__dropdown" onMouseLeave={() => setProfileOpen(false)}>
                      <div className="bc-navbar__dropdown-header">
                        <strong>{user.name}</strong>
                        <span className="bc-navbar__role-badge">{user.role}</span>
                      </div>
                      <button className="bc-navbar__dropdown-logout" onClick={handleLogout}>
                        Logout
                      </button>
                    </div>
                  )}
                </div>
              </>
            ) : (
              <div className="bc-navbar__auth-actions">
                <Link to="/login" className="bc-btn bc-btn--ghost">Login</Link>
                <Link to="/register" className="bc-btn bc-btn--primary">Register</Link>
              </div>
            )}

            <button
              className={`bc-navbar__hamburger ${navOpen ? "is-open" : ""}`}
              type="button"
              onClick={() => setNavOpen((prev) => !prev)}
              aria-label="Toggle navigation"
              aria-expanded={navOpen}
            >
              <span></span>
              <span></span>
              <span></span>
            </button>
          </div>
        </div>

        {/* Search bar full-width on mobile */}
        <div className="bc-navbar__search-mobile">
          <GlobalSearchBar />
        </div>
      </div>

      {/* ===== Bottom row: category / role links ===== */}
      <nav className={`bc-navbar__links ${navOpen ? "is-open" : ""}`}>
        <ul className="bc-navbar__link-list" onClick={closeMobileNav}>
          <li><Link className="bc-navbar__link" to="/">Home</Link></li>
          <li><Link className="bc-navbar__link" to="/browse">Flowers</Link></li>
          <li><Link className="bc-navbar__link" to="/garlands">Garlands</Link></li>

          {user?.role === "CUSTOMER" && (
            <>
              <li><Link className="bc-navbar__link" to="/orders">My Orders</Link></li>
              <li className="bc-navbar__link--mobile-only"><Link className="bc-navbar__link" to="/wishlist">Wishlist</Link></li>
              <li className="bc-navbar__link--mobile-only"><Link className="bc-navbar__link" to="/cart">Cart</Link></li>
            </>
          )}

          {user?.role === "SELLER" && (
            <>
              <li><Link className="bc-navbar__link" to="/seller/dashboard">Dashboard</Link></li>
              <li><Link className="bc-navbar__link" to="/seller/shop">My Shop</Link></li>
              <li><Link className="bc-navbar__link" to="/seller/flowers">My Flowers</Link></li>
              <li><Link className="bc-navbar__link" to="/seller/garlands">My Garlands</Link></li>
              <li><Link className="bc-navbar__link" to="/seller/orders">Orders</Link></li>
            </>
          )}

          {user?.role === "ADMIN" && (
            <>
              <li><Link className="bc-navbar__link" to="/admin/dashboard">Dashboard</Link></li>
              <li><Link className="bc-navbar__link" to="/admin/management">Manage</Link></li>
              <li><Link className="bc-navbar__link" to="/admin/reports">Reports</Link></li>
              <li><Link className="bc-navbar__link" to="/admin/coupons">Coupons</Link></li>
              <li><Link className="bc-navbar__link" to="/admin/festival-offers">Festival Offers</Link></li>
              <li><Link className="bc-navbar__link" to="/admin/garland-categories">Garland Categories</Link></li>
            </>
          )}

          {!user && (
            <li className="bc-navbar__link--mobile-only bc-navbar__link--seller-cta">
              <Link className="bc-navbar__link" to="/register">Become a Seller</Link>
            </li>
          )}

          {user && (
            <li className="bc-navbar__link--mobile-only">
              <button className="bc-navbar__mobile-logout" onClick={handleLogout}>Logout</button>
            </li>
          )}
        </ul>
      </nav>
    </header>
  );
}