import React, { useState } from "react";
import api from "../api/axios";
import { resolveImageUrl } from "../utils/imageUrl";

/**
 * Phase 2: Upload Your Gallery.
 * Real multipart image upload with preview thumbnails - replaces the old
 * "paste an image URL" text input. Supports jpg, jpeg, png, webp.
 *
 * Props:
 *  - images: string[]            current list of image URLs
 *  - onChange: (urls) => void    called with the updated list
 *  - uploadEndpoint: string      e.g. "/seller/uploads/flower-images"
 *  - multiple: boolean           allow selecting several files at once (default true)
 *  - fieldName: string           form field name expected by the backend ("files" or "file")
 */
export default function ImageUploader({
  images = [],
  onChange,
  uploadEndpoint,
  multiple = true,
  fieldName = "files",
}) {
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState("");

  const ALLOWED = ["image/jpeg", "image/jpg", "image/png", "image/webp"];

  const handleFileSelect = async (e) => {
    const files = Array.from(e.target.files || []);
    if (files.length === 0) return;

    const invalid = files.find((f) => !ALLOWED.includes(f.type));
    if (invalid) {
      setError("Only JPG, JPEG, PNG and WEBP images are allowed.");
      e.target.value = "";
      return;
    }

    setError("");
    setUploading(true);
    try {
      const formData = new FormData();
      if (multiple) {
        files.forEach((f) => formData.append(fieldName, f));
      } else {
        formData.append(fieldName, files[0]);
      }

      const { data } = await api.post(uploadEndpoint, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      const newUrls = data.urls || (data.url ? [data.url] : []);
      onChange(multiple ? [...images, ...newUrls] : newUrls);
    } catch (err) {
      setError(err.response?.data?.message || "Upload failed. Please try again.");
    } finally {
      setUploading(false);
      e.target.value = "";
    }
  };

  const removeImage = (url) => {
    onChange(images.filter((img) => img !== url));
  };

  return (
    <div>
      <div className="d-flex gap-2 flex-wrap mb-2">
        {images.map((url) => (
          <div key={url} className="position-relative">
            <img
              src={resolveImageUrl(url)}
              alt=""
              style={{ width: 80, height: 80, objectFit: "cover" }}
              className="rounded border"
            />
            <button
              type="button"
              className="btn btn-sm btn-danger position-absolute top-0 end-0 p-0"
              style={{ width: 20, height: 20, lineHeight: "10px" }}
              onClick={() => removeImage(url)}
            >
              ×
            </button>
          </div>
        ))}
      </div>

      <input
        type="file"
        className="form-control"
        accept="image/jpeg,image/jpg,image/png,image/webp"
        multiple={multiple}
        onChange={handleFileSelect}
        disabled={uploading}
      />
      {uploading && <small className="text-muted">Uploading...</small>}
      {error && <div className="text-danger small mt-1">{error}</div>}
    </div>
  );
}
