package org.quering;

import lombok.Getter;

@Getter
public enum QueryType {
    AND("and"),
    OR("or"),
    NOT("not"),
    MUST("must"),
    RANGE("range"),
    INCLUDE("include"),
    EXCLUDE("exclude");

    private final String value;

    QueryType(String value) {
        this.value = value;
    }

    public static QueryType fromValue(String value) {
        for (QueryType queryType : values()) {
            if (queryType.value.equalsIgnoreCase(value)) {
                return queryType;
            }
        }
        throw new IllegalArgumentException("Unknown query type: " + value);
    }
}
