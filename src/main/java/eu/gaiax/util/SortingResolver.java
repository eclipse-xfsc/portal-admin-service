package eu.gaiax.util;

import java.util.stream.Stream;

public enum SortingResolver {
    NAME("participant_name", "participant_name")
    ;

    private final String uiValue;
    private final String dbValue;

    SortingResolver(String uiValue, String dbValue) {
        this.uiValue = uiValue;
        this.dbValue = dbValue;
    }

    public static String getDbValueByUiValue(String uiValue) {
        return Stream.of(values())
                .filter(x -> x.uiValue.equalsIgnoreCase(uiValue))
                .findFirst()
                .orElse(NAME)
                .dbValue;
    }
}
