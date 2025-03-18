package com.sibijo.user.domain.enums;

public enum Role {
    COMPANY(Authority.COMPANY),
    DELIVERY(Authority.DELIVERY),
    HUB(Authority.HUB),
    MASTER(Authority.MASTER);

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String COMPANY = "ROLE_COMPANY";
        public static final String DELIVERY = "ROLE_DELIVERY";
        public static final String HUB = "ROLE_HUB";
        public static final String MASTER = "ROLE_MASTER";
    }
}