package org.storage;

import lombok.NonNull;

public record FieldDefinition(@NonNull String fieldName, boolean stored, boolean indexed){}
