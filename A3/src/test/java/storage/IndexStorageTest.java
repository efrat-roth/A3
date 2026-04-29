package storage;

import org.junit.jupiter.api.Test;
import org.storage.Field;
import org.storage.IndexStorage;
import org.storage.invertedIndex.InMemoryInvertedIndex;
import org.storage.invertedIndex.InvertedIndex;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class IndexStorageTest {
    @Test
    void shouldAddAndRetrieveDocument() {
        InvertedIndex invertedIndex = new InMemoryInvertedIndex();
        IndexStorage storage = new IndexStorage(invertedIndex);
        Field field = new Field("title", String.class, ("hello").length(), true, true, "hello");

        storage.addDocument("1", List.of(field));

        assertThat(storage.getDocument("1")).isEqualTo(List.of(field));
    }

    @Test
    void addDocumentShouldStoreOnlyFieldsMarkedAsStored() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());
        Field storedField = new Field("title", String.class, 5, true, true, "hello");
        Field unstoredField = new Field("internal", String.class, 6, false, true, "secret");

        storage.addDocument("doc-1", List.of(storedField, unstoredField));

        assertThat(storage.getDocument("doc-1")).containsExactly(storedField);
    }

    @Test
    void addDocumentShouldIgnoreNullDocument() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());

        storage.addDocument("doc-1", null);

        assertThat(storage.getDocument("doc-1")).isNull();
        assertThat(storage.getDocuments()).isEmpty();
    }

    @Test
    void getDocumentShouldReturnNullWhenDocumentDoesNotExist() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());

        assertThat(storage.getDocument("missing-doc")).isNull();
    }

    @Test
    void addDocumentShouldReplaceExistingDocumentWithSameId() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());
        Field originalField = new Field("title", String.class, 5, true, true, "hello");
        Field replacementField = new Field("title", String.class, 7, true, true, "updated");

        storage.addDocument("doc-1", List.of(originalField));
        storage.addDocument("doc-1", List.of(replacementField));

        assertThat(storage.getDocument("doc-1")).containsExactly(replacementField);
        assertThat(storage.getDocuments()).hasSize(1);
    }

    @Test
    void shouldExposeInjectedInvertedIndex() {
        InvertedIndex invertedIndex = new InMemoryInvertedIndex();
        IndexStorage storage = new IndexStorage(invertedIndex);

        assertThat(storage.getInvertedIndex()).isSameAs(invertedIndex);
    }
}
