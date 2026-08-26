import React from "react";
import { Link } from "react-router-dom";
import "../styles/flowercategories.css";

const flowers = [
  {
    name: "Jasmine",
    image: "https://images.unsplash.com/photo-1597848212624-e6f0f4d9d05b?w=600",
    query: "Jasmine",
  },
  {
    name: "Rose",
    image: "https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=600",
    query: "Rose",
  },
  {
    name: "Lotus",
    image: "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?w=600",
    query: "Lotus",
  },
  {
    name: "Lily",
    image: "https://images.unsplash.com/photo-1468327768560-75b778cbb551?w=600",
    query: "Lily",
  },
];

export default function FlowerCategories() {
  return (
    <section className="bc-categories">
      <div className="bc-categories__container">
        <div className="bc-categories__header">
          <span className="bc-categories__eyebrow">Shop by Type</span>
          <h2 className="bc-categories__title">Browse Flowers</h2>
        </div>

        <div className="bc-categories__row">
          {flowers.map((flower) => (
            <Link
              to={`/browse?query=${flower.query}`}
              className="bc-category-card"
              key={flower.name}
            >
              <span className="bc-category-card__ring">
                <img
                  src={flower.image}
                  alt={flower.name}
                  className="bc-category-card__image"
                />
              </span>
              <span className="bc-category-card__name">{flower.name}</span>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
}