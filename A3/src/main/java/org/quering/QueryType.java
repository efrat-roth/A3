package org.quering;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

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

        if (value == null || value.isBlank()) {
            throw new Exceptions.InvalidQueryException("Query type cannot be null or blank");
        }

        for (QueryType queryType : values()) {
            if (queryType.value.equalsIgnoreCase(value)) {
                log.debug("Query type resolved: value {}, queryType {}", value, queryType);
                return queryType;
            }
        }

        log.warn("Unknown query type requested: {}", value);

        throw new Exceptions.UnsupportedQueryTypeException("Unknown query type: " + value);
    }
}
