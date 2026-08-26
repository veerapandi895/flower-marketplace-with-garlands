import React, { useEffect, useState } from "react";
import api from "../api/axios";

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadNotifications();
  }, []);

  const loadNotifications = async () => {
    try {
      setLoading(true);
      const { data } = await api.get("/notifications");
      setNotifications(data);
    } catch (err) {
      console.error(err);
      alert("Unable to load notifications.");
    } finally {
      setLoading(false);
    }
  };

  const markRead = async (id) => {
    try {
      await api.patch(`/notifications/${id}/read`);

      setNotifications((prev) =>
        prev.map((n) =>
          n.id === id
            ? {
                ...n,
                read: true,
              }
            : n
        )
      );
    } catch (err) {
      console.error(err);
    }
  };

  const markAllRead = async () => {
    try {
      await api.patch("/notifications/read-all");

      setNotifications((prev) =>
        prev.map((n) => ({
          ...n,
          read: true,
        }))
      );
    } catch (err) {
      console.error(err);
    }
  };

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div className="container py-4">

      <div className="d-flex justify-content-between align-items-center flex-wrap mb-4">
        <div>
          <h3 className="mb-0">🔔 Notifications</h3>
          <small className="text-muted">
            {unreadCount} unread notification{unreadCount !== 1 && "s"}
          </small>
        </div>

        {notifications.length > 0 && (
          <button
            className="btn btn-outline-success btn-sm mt-2 mt-md-0"
            onClick={markAllRead}
          >
            Mark All Read
          </button>
        )}
      </div>

      {loading ? (
        <div className="text-center py-5">
          <div
            className="spinner-border text-success"
            role="status"
          />
          <p className="mt-3">Loading notifications...</p>
        </div>
      ) : notifications.length === 0 ? (
        <div className="alert alert-light border text-center">
          <h5>No Notifications</h5>
          <p className="mb-0">
            You're all caught up.
          </p>
        </div>
      ) : (
        notifications.map((notification) => (
          <div
            key={notification.id}
            className={`card shadow-sm mb-3 ${
              notification.read
                ? ""
                : "border-primary"
            }`}
            style={{
              cursor: notification.read
                ? "default"
                : "pointer",
            }}
            onClick={() =>
              !notification.read &&
              markRead(notification.id)
            }
          >
            <div className="card-body">

              <div className="d-flex justify-content-between">

                <h5 className="mb-1">
                  {notification.title}
                </h5>

                {!notification.read && (
                  <span className="badge bg-primary">
                    NEW
                  </span>
                )}
              </div>

              <p className="mb-2">
                {notification.message}
              </p>

              <small className="text-muted">
                {new Date(
                  notification.createdAt
                ).toLocaleString()}
              </small>

            </div>
          </div>
        ))
      )}

    </div>
  );
}