package com.openisle.service;

/**
 * Abstract service for uploading images.
 */
public abstract class ImageUploader {
    /**
     * Upload an image and return its accessible URL.
     * @param data image binary data
     * @param filename name of the file
     * @return accessible URL of the uploaded file
     */
    /**
     * Upload an image asynchronously and return a future of its accessible URL.
     * Implementations should complete the future exceptionally on failures so
     * callers can react accordingly.
     */
    public abstract java.util.concurrent.CompletableFuture<String> upload(byte[] data, String filename);
}
