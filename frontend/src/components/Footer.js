import React from "react";
import { Link } from "react-router-dom";
import "../styles/footer.css";

export default function Footer() {
  return (
    <footer className="bc-footer">
      <div className="bc-footer__container">
        <div className="bc-footer__grid">

          <div className="bc-footer__col bc-footer__col--brand">
            <h3 className="bc-footer__brand">🌸 Bloomcycle</h3>
            <p className="bc-footer__desc">
              Fresh flowers and garlands, straight from local sellers, priced fairly with our
              dynamic waste-reduction pricing.
            </p>

            <div className="bc-footer__social">
              <a href="#" className="bc-footer__social-icon" aria-label="Facebook" title="Facebook">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M13.5 21v-7.5H16l.5-3H13.5V8.5c0-.87.24-1.46 1.5-1.46H16.5V4.35C16.24 4.32 15.36 4.25 14.34 4.25c-2.13 0-3.59 1.3-3.59 3.68V10.5H8.25v3h2.5V21h2.75Z" /></svg>
              </a>
              <a href="#" className="bc-footer__social-icon" aria-label="Instagram" title="Instagram">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.8"><rect x="3" y="3" width="18" height="18" rx="5" /><circle cx="12" cy="12" r="4" /><circle cx="17.2" cy="6.8" r="1" fill="currentColor" stroke="none" /></svg>
              </a>
              <a href="#" className="bc-footer__social-icon" aria-label="Twitter" title="Twitter">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M22 5.9c-.7.3-1.4.5-2.2.6.8-.5 1.4-1.2 1.6-2.2-.7.5-1.6.8-2.4 1a3.8 3.8 0 0 0-6.5 3.5A10.8 10.8 0 0 1 4.5 4.7a3.8 3.8 0 0 0 1.2 5.1c-.6 0-1.2-.2-1.7-.5v.1c0 1.9 1.3 3.4 3.1 3.8-.6.1-1.2.2-1.8.1.5 1.6 2 2.7 3.7 2.7A7.6 7.6 0 0 1 2 17.5a10.7 10.7 0 0 0 5.8 1.7c7 0 10.8-5.8 10.8-10.8v-.5c.8-.5 1.4-1.2 1.9-2Z" /></svg>
              </a>
            </div>
          </div>

          <div className="bc-footer__col">
            <h4 className="bc-footer__heading">Shop</h4>
            <ul className="bc-footer__links">
              <li><Link to="/browse">Browse Flowers</Link></li>
              <li><Link to="/garlands">Garlands</Link></li>
            </ul>
          </div>

          <div className="bc-footer__col">
            <h4 className="bc-footer__heading">Customer</h4>
            <ul className="bc-footer__links">
              <li><Link to="/orders">Track an Order</Link></li>
              <li><Link to="/wishlist">Wishlist</Link></li>
              <li><Link to="/cart">Cart</Link></li>
            </ul>
          </div>

          <div className="bc-footer__col">
            <h4 className="bc-footer__heading">Sell on Bloomcycle</h4>
            <ul className="bc-footer__links">
              <li><Link to="/register">Become a Seller</Link></li>
              <li><Link to="/seller/shop">Seller Dashboard</Link></li>
            </ul>
            <p className="bc-footer__contact">support@bloomcycle.example</p>
          </div>

        </div>

        <div className="bc-footer__bottom">
          <p>© {new Date().getFullYear()} Bloomcycle. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}