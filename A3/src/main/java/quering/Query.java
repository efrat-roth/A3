package quering;

import lombok.Data;

import java.util.Map;


@Data
public class Query {
    private final String queryId;
    private final Map<String, String> conditions;
    private final int limit;
    private final int start;
    private final QueryType queryType;
}
