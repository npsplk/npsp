package lk.npsp.domain.enumeration;

/**
 * The ScreenLanguage enumeration.
 */
public enum ScreenLanguage {
    ENGLISH("English", 0),
    SINHALA("Sinhala", 1),
    TAMIL("Tamil", 2);

    private final String key;
    private final Integer value;

    ScreenLanguage(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public Integer getValue() {
        return value;
    }
}


