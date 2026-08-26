import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";

const POLL_INTERVAL_MS = 15000;

export default function NotificationBell() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [unreadCount, setUnreadCount] = useState(0);
  const [notifications, setNotifications] = useState([]);
  const [open, setOpen] = useState(false);

  const boxRef = useRef(null);

  useEffect(() => {
    if (!user) return;

    fetchUnreadCount();

    const timer = setInterval(fetchUnreadCount, POLL_INTERVAL_MS);

    return () => clearInterval(timer);
    // eslint-disable-next-line
  }, [user]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (boxRef.current && !boxRef.current.contains(e.target)) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const fetchUnreadCount = () => {
    api
      .get("/notifications/unread-count")
      .then((res) => {
        setUnreadCount(res.data.count || 0);
      })
      .catch(() => {
        setUnreadCount(0);
      });
  };

  const openDropdown = () => {
    const nextState = !open;
    setOpen(nextState);

    if (nextState) {
      api
        .get("/notifications/recent")
        .then((res) => {
          setNotifications(res.data || []);
        })
        .catch(() => {
          setNotifications([]);
        });
    }
  };

  const handleNotificationClick = async (notification) => {
    try {
      if (!notification.read) {
        await api.patch(`/notifications/${notification.id}/read`);

        setNotifications((prev) =>
          prev.map((item) =>
            item.id === notification.id
              ? { ...item, read: true }
              : item
          )
        );

        fetchUnreadCount();
      }

      setOpen(false);

      if (notification.url) {
        navigate(notification.url);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const markAllRead = async (e) => {
    e.stopPropagation();

    try {
      await api.patch("/notifications/read-all");

      setNotifications((prev) =>
        prev.map((item) => ({
          ...item,
          read: true,
        }))
      );

      setUnreadCount(0);
    } catch (err) {
      console.error(err);
    }
  };

  if (!user) return null;

  return (
    <div ref={boxRef} className="position-relative me-3">

      <button
        className="btn btn-outline-light position-relative"
        onClick={openDropdown}
        title="Notifications"
      >
        🔔

        {unreadCount > 0 && (
          <span
            className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
          >
            {unreadCount > 9 ? "9+" : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div
          className="dropdown-menu show p-0"
          style={{
            right: 0,
            left: "auto",
            width: "340px",
            maxHeight: "400px",
            overflowY: "auto",
          }}
        >
          <div className="d-flex justify-content-between align-items-center px-3 py-2 border-bottom">
            <strong>Notifications</strong>

            {unreadCount > 0 && (
              <button
                className="btn btn-sm btn-link p-0"
                onClick={markAllRead}
              >
                Mark all read
              </button>
            )}
          </div>

          {notifications.length === 0 ? (
            <div className="px-3 py-3 text-center text-muted">
              No notifications available.
            </div>
          ) : (
            notifications.map((notification) => (
              <div
                key={notification.id}
                className={`px-3 py-2 border-bottom ${
                  notification.read ? "" : "bg-light"
                }`}
                style={{ cursor: "pointer" }}
                onClick={() =>
                  handleNotificationClick(notification)
                }
              >
                <div className="d-flex justify-content-between">

                  <strong className="small">
                    {notification.title}
                  </strong>

                  {!notification.read && (
                    <span
                      className="badge bg-primary"
                      style={{ fontSize: "0.6rem" }}
                    >
                      NEW
                    </span>
                  )}

                </div>

                <p className="small text-muted mb-1">
                  {notification.message}
                </p>

                {notification.createdAt && (
                  <small className="text-muted">
                    {new Date(
                      notification.createdAt
                    ).toLocaleString()}
                  </small>
                )}
              </div>
            ))
          )}

          <div className="text-center py-2 border-top">
            <button
              className="btn btn-sm btn-link"
              onClick={() => {
                setOpen(false);
                navigate("/notifications");
              }}
            >
              View all notifications
            </button>
          </div>
        </div>
      )}
    </div>
  );
}