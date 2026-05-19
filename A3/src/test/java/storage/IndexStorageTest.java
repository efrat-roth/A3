package storage;

import org.junit.jupiter.api.Test;
import org.storage.IndexStorage;
import org.storage.invertedIndex.InMemoryInvertedIndex;
import org.storage.invertedIndex.InvertedIndex;
import org.utils.Exceptions.DocumentNotFoundException;
import org.utils.Exceptions.DuplicateDocumentException;
import org.utils.Exceptions.InvalidDocumentException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class IndexStorageTest {
    @Test
    void shouldAddAndRetrieveDocument() {
        InvertedIndex invertedIndex = new InMemoryInvertedIndex();
        IndexStorage storage = new IndexStorage(invertedIndex);
        FieldType field = new FieldType("title", String.class, ("hello").length(), true, true, "hello");

        storage.addDocument("1", List.of(field));

        assertThat(storage.getDocument("1")).isEqualTo(List.of(field));
    }

    @Test
    void addDocumentShouldStoreOnlyFieldsMarkedAsStored() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());
        FieldType storedField = new FieldType("title", String.class, 5, true, true, "hello");
        FieldType unstoredField = new FieldType("internal", String.class, 6, false, true, "secret");

        storage.addDocument("doc-1", List.of(storedField, unstoredField));

        assertThat(storage.getDocument("doc-1")).containsExactly(storedField);
    }

    @Test
    void addDocumentShouldIgnoreNullDocument() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());

        assertThatThrownBy(() -> storage.addDocument("doc-1", null))
                .isInstanceOf(InvalidDocumentException.class)
                .hasMessage("Document cannot be null");

        assertThat(storage.getDocuments()).isEmpty();
    }

    @Test
    void getDocumentShouldReturnNullWhenDocumentDoesNotExist() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());

        assertThatThrownBy(() -> storage.getDocument("missing-doc"))
                .isInstanceOf(DocumentNotFoundException.class)
                .hasMessage("Document not found: missing-doc");
    }

    @Test
    void addDocumentShouldReplaceExistingDocumentWithSameId() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());
        FieldType originalField = new FieldType("title", String.class, 5, true, true, "hello");
        FieldType replacementField = new FieldType("title", String.class, 7, true, true, "updated");

        storage.addDocument("doc-1", List.of(originalField));

        assertThatThrownBy(() -> storage.addDocument("doc-1", List.of(replacementField)))
                .isInstanceOf(DuplicateDocumentException.class)
                .hasMessage("Document already exists: doc-1");
        assertThat(storage.getDocument("doc-1")).containsExactly(originalField);
        assertThat(storage.getDocuments()).hasSize(1);
    }

    @Test
    void shouldExposeInjectedInvertedIndex() {
        InvertedIndex invertedIndex = new InMemoryInvertedIndex();
        IndexStorage storage = new IndexStorage(invertedIndex);

        assertThat(storage.getInvertedIndex()).isSameAs(invertedIndex);
    }
}
