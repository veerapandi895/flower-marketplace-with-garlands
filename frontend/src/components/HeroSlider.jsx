import React from "react";
import { Link } from "react-router-dom";
import "../styles/heroslider.css";
import FlowerCategories from "./FlowerCategories";

export default function HeroSlider() {
  return (
    <>
      {/* Hero Slider */}
      <div
        id="heroCarousel"
        className="carousel slide bc-hero"
        data-bs-ride="carousel"
        data-bs-interval="3000"
      >
        {/* Indicators */}
        <div className="carousel-indicators bc-hero__indicators">
          <button
            type="button"
            data-bs-target="#heroCarousel"
            data-bs-slide-to="0"
            className="active"
            aria-label="Slide 1"
          ></button>

          <button
            type="button"
            data-bs-target="#heroCarousel"
            data-bs-slide-to="1"
            aria-label="Slide 2"
          ></button>

          <button
            type="button"
            data-bs-target="#heroCarousel"
            data-bs-slide-to="2"
            aria-label="Slide 3"
          ></button>
        </div>

        {/* Slides */}
        <div className="carousel-inner">

          {/* Slide 1 */}
          <div className="carousel-item active">
            <img
              src="https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=1600"
              className="d-block w-100"
              alt="Fresh flowers"
            />
            <div className="bc-hero__overlay"></div>

            <div className="carousel-caption bc-hero__caption">
              <span className="bc-hero__eyebrow">Bloomcycle Marketplace</span>
              <h1>Fresh Flowers</h1>
              <p>Fresh flowers from trusted sellers, picked and delivered the same day.</p>
              <Link to="/browse" className="bc-hero__cta">
                Shop Flowers
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M5 12h14" />
                  <path d="m13 6 6 6-6 6" />
                </svg>
              </Link>
            </div>
          </div>

          {/* Slide 2 */}
          <div className="carousel-item">
            <img
              src="https://images.unsplash.com/photo-1526045478516-99145907023c?w=1600"
              className="d-block w-100"
              alt="Wedding garlands"
            />
            <div className="bc-hero__overlay"></div>

            <div className="carousel-caption bc-hero__caption">
              <span className="bc-hero__eyebrow">Handcrafted Daily</span>
              <h1>Beautiful Wedding Garlands</h1>
              <p>Temple, wedding and reception garlands made to order.</p>
              <Link to="/garlands" className="bc-hero__cta">
                Explore Garlands
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M5 12h14" />
                  <path d="m13 6 6 6-6 6" />
                </svg>
              </Link>
            </div>
          </div>

          {/* Slide 3 */}
          <div className="carousel-item">
            <img
              src="https://images.unsplash.com/photo-1468327768560-75b778cbb551?w=1600"
              className="d-block w-100"
              alt="Festival offers"
            />
            <div className="bc-hero__overlay"></div>

            <div className="carousel-caption bc-hero__caption">
              <span className="bc-hero__eyebrow bc-hero__eyebrow--accent">Limited Time</span>
              <h1>Festival Special Offers</h1>
              <p>Celebrate every occasion with fresh flowers at special prices.</p>
              <Link to="/browse" className="bc-hero__cta bc-hero__cta--accent">
                View Offers
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M5 12h14" />
                  <path d="m13 6 6 6-6 6" />
                </svg>
              </Link>
            </div>
          </div>

        </div>

        {/* Previous */}
        <button
          className="carousel-control-prev bc-hero__arrow bc-hero__arrow--prev"
          type="button"
          data-bs-target="#heroCarousel"
          data-bs-slide="prev"
        >
          <span className="bc-hero__arrow-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="m15 18-6-6 6-6" />
            </svg>
          </span>
          <span className="visually-hidden">Previous</span>
        </button>

        {/* Next */}
        <button
          className="carousel-control-next bc-hero__arrow bc-hero__arrow--next"
          type="button"
          data-bs-target="#heroCarousel"
          data-bs-slide="next"
        >
          <span className="bc-hero__arrow-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="m9 18 6-6-6-6" />
            </svg>
          </span>
          <span className="visually-hidden">Next</span>
        </button>
      </div>

      {/* Flower Categories Below Slider */}
      <FlowerCategories />
    </>
  );
}