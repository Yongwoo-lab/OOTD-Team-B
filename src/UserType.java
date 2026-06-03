public enum UserType {
    ADMIN("Admin", "A"),
    MEMBER("Customer", "C"),
    GUEST("Guest", "G");

    private final String storageName;
    private final String idPrefix;

    UserType(String storageName, String idPrefix) {
        this.storageName = storageName;
        this.idPrefix = idPrefix;
    }

    public String getStorageName() {
        return storageName;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public static UserType fromSignup() {
        return MEMBER;
    }

    public static UserType fromDatabaseValue(String value) {
        if ("Admin".equalsIgnoreCase(value)) {
            return ADMIN;
        }
        if ("Guest".equalsIgnoreCase(value)) {
            return GUEST;
        }
        return MEMBER;
    }

    public static boolean isSkyPassDatabaseValue(String value) {
        return "SkyPass".equalsIgnoreCase(value) || "SkyPass Member".equalsIgnoreCase(value);
    }
}
