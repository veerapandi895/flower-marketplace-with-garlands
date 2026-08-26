import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Navbar from "./components/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";
import Footer from "./components/Footer";

import Home from "./pages/Home";
import BrowseFlowers from "./pages/BrowseFlowers";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import FlowerDetail from "./pages/FlowerDetail";
import Garlands from "./pages/Garlands";
import GarlandDetail from "./pages/GarlandDetail";
import ShopProfile from "./pages/ShopProfile";
import Cart from "./pages/Cart";
import Checkout from "./pages/Checkout";
import OrderHistory from "./pages/OrderHistory";
import Wishlist from "./pages/Wishlist";
import Notifications from "./pages/Notifications";

import SellerShop from "./pages/SellerShop";
import SellerFlowers from "./pages/SellerFlowers";
import SellerGarlands from "./pages/SellerGarlands";
import SellerOrders from "./pages/SellerOrders";
import SellerDashboard from "./pages/SellerDashboard";

import AdminDashboard from "./pages/AdminDashboard";
import AdminManagement from "./pages/AdminManagement";
import AdminReports from "./pages/AdminReports";
import AdminCoupons from "./pages/AdminCoupons";
import AdminFestivalOffers from "./pages/AdminFestivalOffers";
import AdminGarlandCategories from "./pages/AdminGarlandCategories";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />
        <Routes>
          {/* Public */}
          <Route path="/" element={<Home />} />
          <Route path="/browse" element={<BrowseFlowers />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/flowers/:id" element={<FlowerDetail />} />
          <Route path="/garlands" element={<Garlands />} />
          <Route path="/garlands/:id" element={<GarlandDetail />} />
          <Route path="/shops/:sellerId" element={<ShopProfile />} />

          {/* Customer */}
          <Route path="/cart" element={<ProtectedRoute roles={["CUSTOMER"]}><Cart /></ProtectedRoute>} />
          <Route path="/checkout" element={<ProtectedRoute roles={["CUSTOMER"]}><Checkout /></ProtectedRoute>} />
          <Route path="/orders" element={<ProtectedRoute roles={["CUSTOMER"]}><OrderHistory /></ProtectedRoute>} />
          <Route path="/wishlist" element={<ProtectedRoute roles={["CUSTOMER"]}><Wishlist /></ProtectedRoute>} />
          <Route path="/notifications" element={<ProtectedRoute roles={["CUSTOMER", "SELLER", "ADMIN"]}><Notifications /></ProtectedRoute>} />

          {/* Seller */}
          <Route path="/seller/shop" element={<ProtectedRoute roles={["SELLER"]}><SellerShop /></ProtectedRoute>} />
          <Route path="/seller/flowers" element={<ProtectedRoute roles={["SELLER"]}><SellerFlowers /></ProtectedRoute>} />
          <Route path="/seller/garlands" element={<ProtectedRoute roles={["SELLER"]}><SellerGarlands /></ProtectedRoute>} />
          <Route path="/seller/orders" element={<ProtectedRoute roles={["SELLER"]}><SellerOrders /></ProtectedRoute>} />
          <Route path="/seller/dashboard" element={<ProtectedRoute roles={["SELLER"]}><SellerDashboard /></ProtectedRoute>} />

          {/* Admin */}
          <Route path="/admin/dashboard" element={<ProtectedRoute roles={["ADMIN"]}><AdminDashboard /></ProtectedRoute>} />
          <Route path="/admin/management" element={<ProtectedRoute roles={["ADMIN"]}><AdminManagement /></ProtectedRoute>} />
          <Route path="/admin/reports" element={<ProtectedRoute roles={["ADMIN"]}><AdminReports /></ProtectedRoute>} />
          <Route path="/admin/coupons" element={<ProtectedRoute roles={["ADMIN"]}><AdminCoupons /></ProtectedRoute>} />
          <Route path="/admin/festival-offers" element={<ProtectedRoute roles={["ADMIN"]}><AdminFestivalOffers /></ProtectedRoute>} />
          <Route path="/admin/garland-categories" element={<ProtectedRoute roles={["ADMIN"]}><AdminGarlandCategories /></ProtectedRoute>} />
        </Routes>
        <Footer />
      </AuthProvider>
    </BrowserRouter>
  );
}
