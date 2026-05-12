package indexing;

import org.analyzing.Analyzer;
import org.analyzing.Token;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.indexing.InMemoryIndexWriter;
import org.junit.jupiter.api.Test;
import org.storage.IndexStorage;
import org.storage.invertedIndex.InMemoryInvertedIndex;
import org.storage.invertedIndex.PostingList;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InMemoryIndexWriterTest {

    @Test
    void addDocumentShouldStoreDocumentAndIndexIndexedFields() throws Exception {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());
        InMemoryIndexWriter writer = createWriter(storage, field -> analyzerReturning(
                new Token("hello", 1),
                new Token("world", 2)
        ));
        FieldType title = new FieldType("title", String.class, 11, true, true, "hello world");

        writer.addDocument(List.of(title));

        assertThat(storage.getDocuments()).hasSize(1);
        String docId = storage.getDocuments().keySet().iterator().next();
        assertThat(storage.getDocument(docId)).containsExactly(title);
        assertThat(storage.getInvertedIndex().getPostings("title")).containsOnlyKeys("hello", "world");
        assertThat(posting(storage, "title", "hello").getPostings().get(docId).getTf()).isEqualTo(1.0 / 11);
        assertThat(posting(storage, "title", "hello").getPostings().get(docId).getPositions()).containsExactly(1);
        assertThat(posting(storage, "title", "world").getPostings().get(docId).getPositions()).containsExactly(2);
    }

    @Test
    void addDocumentShouldNotAnalyzeOrIndexUnindexedFields() throws Exception {
        InMemoryInvertedIndex invertedIndex = new InMemoryInvertedIndex();
        IndexStorage storage = new IndexStorage(invertedIndex);
        InMemoryIndexWriter writer = createWriter(storage, field -> {
            throw new AssertionError("Unindexed fields should not be analyzed");
        });
        FieldType field = new FieldType("raw", String.class, 6, true, false, "secret");

        writer.addDocument(List.of(field));

        assertThat(storage.getDocuments()).hasSize(1);
        assertThat(invertedIndex.getIndex()).isEmpty();
    }

    @Test
    void addDocumentShouldUseTotalDocumentLengthForTermFrequency() throws Exception {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());
        InMemoryIndexWriter writer = createWriter(storage, field -> analyzerReturning(new Token(field, 1)));
        FieldType title = new FieldType("title", String.class, 5, true, true, "hello");
        FieldType body = new FieldType("body", String.class, 15, true, true, "content");

        writer.addDocument(List.of(title, body));

        String docId = storage.getDocuments().keySet().iterator().next();
        assertThat(posting(storage, "title", "title").getPostings().get(docId).getTf()).isEqualTo(1.0 / 20);
        assertThat(posting(storage, "body", "body").getPostings().get(docId).getTf()).isEqualTo(1.0 / 20);
    }

    @Test
    void addDocumentShouldWrapAnalyzerIOExceptionInRuntimeException() throws Exception {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());
        InMemoryIndexWriter writer = createWriter(storage, field -> Analyzer.builder()
                .charFilters(List.of())
                .tokenizer(input -> List.of(new Token(input, 1)))
                .tokenFilters(List.of(tokens -> {
                    throw new IOException("analyzer failed");
                }))
                .build());
        FieldType field = new FieldType("title", String.class, 5, true, true, "hello");

        assertThatThrownBy(() -> writer.addDocument(List.of(field)))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    private static InMemoryIndexWriter createWriter(
            IndexStorage storage,
            AnalyzerStrategy analyzerStrategy
    ) throws Exception {
        InMemoryIndexWriter writer = new InMemoryIndexWriter();
        setPrivateField(writer, "indexStorage", storage);
        setPrivateField(writer, "analyzerStrategy", analyzerStrategy);
        return writer;
    }

    private static Analyzer analyzerReturning(Token... tokens) {
        return Analyzer.builder()
                .charFilters(List.of())
                .tokenizer(input -> List.of(tokens))
                .tokenFilters(List.of())
                .build();
    }

    private static PostingList posting(IndexStorage storage, String fieldName, String term) {
        return storage.getInvertedIndex().getPostingListByTerm(fieldName, term);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
