package org.quering;

import org.storage.FieldValue;

import java.util.List;

public record DocQueryResult(String docId, List<FieldValue> fields, double score) {
}
