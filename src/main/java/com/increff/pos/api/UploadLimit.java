package com.increff.pos.api;

public class UploadLimit {
    public static final Integer MAX_UPLOAD_SIZE = 5000;

    public static void checkSize(Integer size) throws ApiException {
        if (size > MAX_UPLOAD_SIZE) {
            throw new ApiException("upload size cannot be more than " + MAX_UPLOAD_SIZE);
        }
    }
}
