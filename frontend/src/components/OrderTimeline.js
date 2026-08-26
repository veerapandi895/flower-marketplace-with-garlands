import React from "react";

const HAPPY_PATH = [
  "PLACED",
  "ACCEPTED",
  "PREPARING",
  "PACKED",
  "READY",
  "COMPLETED",
];

const STEP_LABELS = {
  PLACED: "Placed",
  ACCEPTED: "Accepted",
  PREPARING: "Preparing",
  PACKED: "Packed",
  READY: "Ready",
  COMPLETED: "Delivered",
};

export default function OrderTimeline({ order }) {
  const isCancelled =
    order.status === "CANCELLED" || order.status === "REJECTED";

  const history = {};
  (order.statusHistory || []).forEach((h) => {
    history[h.status] = h.changedAt;
  });

  const currentStep = HAPPY_PATH.indexOf(order.status);

  return (
    <div className="my-3">
      {isCancelled ? (
        <div className="alert alert-danger">
          <h6 className="mb-1">
            ❌ Order {order.status.toLowerCase()}
          </h6>

          {history[order.status] && (
            <small>
              {new Date(history[order.status]).toLocaleString()}
            </small>
          )}
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-start flex-wrap">

            {HAPPY_PATH.map((step, index) => {
              const completed = index <= currentStep;
              const active = index === currentStep;
              const time = history[step];

              return (
                <React.Fragment key={step}>
                  <div
                    className="text-center"
                    style={{
                      flex: 1,
                      minWidth: "90px",
                    }}
                  >
                    <div
                      className={`rounded-circle mx-auto d-flex align-items-center justify-content-center fw-bold
                      ${
                        completed
                          ? "bg-success text-white"
                          : "bg-light border text-secondary"
                      }`}
                      style={{
                        width: 40,
                        height: 40,
                        fontSize: "16px",
                      }}
                    >
                      {completed ? "✓" : index + 1}
                    </div>

                    <div
                      className={`mt-2 small fw-semibold ${
                        active ? "text-success" : ""
                      }`}
                    >
                      {STEP_LABELS[step]}
                    </div>

                    {time && (
                      <div
                        className="text-muted"
                        style={{ fontSize: "11px" }}
                      >
                        {new Date(time).toLocaleString()}
                      </div>
                    )}
                  </div>

                  {index !== HAPPY_PATH.length - 1 && (
                    <div
                      className={`d-none d-md-block mt-3 ${
                        completed ? "bg-success" : "bg-secondary-subtle"
                      }`}
                      style={{
                        flex: 1,
                        height: "4px",
                        borderRadius: "10px",
                      }}
                    />
                  )}
                </React.Fragment>
              );
            })}
          </div>

          {order.estimatedDeliveryAt &&
            order.status !== "COMPLETED" && (
              <div className="alert alert-info mt-3 mb-0 py-2">
                🚚 <strong>Estimated Delivery :</strong>{" "}
                {new Date(
                  order.estimatedDeliveryAt
                ).toLocaleString()}
              </div>
            )}

          {order.status === "COMPLETED" && (
            <div className="alert alert-success mt-3 mb-0 py-2">
              🎉 Your order has been delivered successfully.
            </div>
          )}
        </>
      )}
    </div>
  );
}