package org.quering;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                log.debug("Query type resolved: value {}, queryType {}", value, queryType);
                return queryType;
            }
        }
        log.warn("Unknown query type requested: {}", value);
        throw new IllegalArgumentException("Unknown query type: " + value);
    }
}
