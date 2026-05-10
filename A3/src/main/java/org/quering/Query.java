package org.quering;

import lombok.Data;
import org.utils.Exceptions;

import java.util.Map;

@Data
public class Query {

    private final String queryId;
    private final Map<String, String> conditions;
    private final int limit;
    private final int start;
    private final QueryType queryType;

    public Query(String queryId, Map<String, String> conditions, int limit, int start, QueryType queryType) {

        if (queryId == null || queryId.isBlank()) {
            throw new Exceptions.InvalidQueryException("Query id cannot be null or blank");
        }

        if (conditions == null || conditions.isEmpty()) {
            throw new Exceptions.InvalidQueryException("Query conditions cannot be empty");
        }

        if (limit <= 0) {
            throw new Exceptions.InvalidQueryException("Limit must be greater than zero");
        }

        if (start < 0) {
            throw new Exceptions.InvalidQueryException("Start offset cannot be negative");
        }

        this.queryId = queryId;
        this.conditions = conditions;
        this.limit = limit;
        this.start = start;
        this.queryType = queryType;
    }
}