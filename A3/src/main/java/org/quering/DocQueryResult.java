package org.quering;

import org.storage.FieldType;

import java.util.List;

public record DocQueryResult(String docId, List<FieldType> fields, double score) {
}
