package com.collins.api_gateway.constants;

public final class ApplicationConstants {

    public static final String JWT_SECRET_KEY = "jwt.secret";

    public static final String JWT_SECRET_DEFAULT_VALUE =
            "this-is-a-very-long-default-secret-key-for-jwt-2026";

    public static final String JWT_HEADER = "Authorization";

    private ApplicationConstants() {
    }
}
