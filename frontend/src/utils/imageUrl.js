// Resolves an image reference to a displayable URL.
// Full external URLs (http/https) pass through unchanged; local uploads
// (e.g. "/uploads/flowers/xyz.jpg") are resolved against the backend origin.
export function resolveImageUrl(url) {
  if (!url) return null;
  if (url.startsWith("http://") || url.startsWith("https://")) return url;

  const apiBase = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api";
  const origin = apiBase.replace(/\/api\/?$/, "");
  return url.startsWith("/") ? `${origin}${url}` : `${origin}/${url}`;
}
