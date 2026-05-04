package org.utils;

public final class Exceptions {
    private Exceptions() {
    }

    public static class InvalidDocumentException extends RuntimeException {
        public InvalidDocumentException(String message) {
            super(message);
        }
    }

    public static class DuplicateDocumentException extends RuntimeException {
        public DuplicateDocumentException(String message) {
            super(message);
        }
    }

    public static class DocumentNotFoundException extends RuntimeException {
        public DocumentNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidFieldException extends RuntimeException {
        public InvalidFieldException(String message) {
            super(message);
        }
    }

    public static class InvalidTermStatsException extends RuntimeException {
        public InvalidTermStatsException(String message) {
            super(message);
        }
    }

    public static class InvalidIndexEntryException extends RuntimeException {
        public InvalidIndexEntryException(String message) {
            super(message);
        }
    }

    public static class FieldNotFoundException extends RuntimeException {
        public FieldNotFoundException(String message) {
            super(message);
        }
    }

    public static class TermNotFoundException extends RuntimeException {
        public TermNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnsupportedScoringAlgorithmException extends RuntimeException {
        public UnsupportedScoringAlgorithmException(String message) {
            super(message);
        }
    }

    public static class InvalidQueryException extends RuntimeException {
        public InvalidQueryException(String message) {
            super(message);
        }
    }

    public static class UnsupportedQueryTypeException extends RuntimeException {
        public UnsupportedQueryTypeException(String message) {
            super(message);
        }
    }

    public static class QueryProcessingException extends RuntimeException {
        public QueryProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class IndexingException extends RuntimeException {
        public IndexingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class AnalyzerNotFoundException extends RuntimeException {
        public AnalyzerNotFoundException(String message) {
            super(message);
        }
    }
    public static class AnalyzerConfigurationException extends RuntimeException {
        public AnalyzerConfigurationException(String message) {
            super(message);
        }
    }

    public static class UnsupportedAnalyzerStrategyException extends RuntimeException {
        public UnsupportedAnalyzerStrategyException(String message) {
            super(message);
        }
    }

    public static class UnsupprtedFieldTypeException extends RuntimeException {
        public UnsupprtedFieldTypeException(String message) {}
    }

}
