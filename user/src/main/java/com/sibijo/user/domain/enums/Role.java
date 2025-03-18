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
        public static final String COMPANY = "COMPANY";
        public static final String DELIVERY = "DELIVERY";
        public static final String HUB = "HUB";
        public static final String MASTER = "MASTER";
    }
}