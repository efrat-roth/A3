package reading;

import org.junit.jupiter.api.Test;
import org.reading.InMemoryIndexReader;
import org.reading.QueryContext;
import org.storage.Field;
import org.storage.IndexStorage;
import org.storage.invertedIndex.InMemoryInvertedIndex;
import org.storage.invertedIndex.PostingList;
import org.storage.invertedIndex.TermStats;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryIndexReaderTest {

    @Test
    void getPostingShouldReturnPostingsForField() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());
        storage.getInvertedIndex().addField("title", "java", "doc-1", 1, 4);
        storage.getInvertedIndex().addField("title", "search", "doc-2", 2, 4);
        InMemoryIndexReader reader = new InMemoryIndexReader(storage);

        Map<String, PostingList> postings = reader.getPosting("title");

        assertThat(postings).containsOnlyKeys("java", "search");
        assertThat(postings.get("java").getPostings()).containsOnlyKeys("doc-1");
        assertThat(postings.get("search").getPostings()).containsOnlyKeys("doc-2");
    }

    @Test
    void buildContextShouldIncludeDocIdAndTotalStoredDocumentCount() {
        IndexStorage storage = storageWithDocuments();
        storage.getInvertedIndex().addField("title", "java", "doc-1", 1, 4);
        InMemoryIndexReader reader = new InMemoryIndexReader(storage);

        QueryContext context = reader.buildContext("doc-1", Map.of("title", List.of("java")));

        assertThat(context.getDocId()).isEqualTo("doc-1");
        assertThat(context.getDocsCount()).isEqualTo(2);
    }

    @Test
    void buildContextShouldCollectTermStatsForRequestedDocument() {
        IndexStorage storage = storageWithDocuments();
        storage.getInvertedIndex().addField("title", "java", "doc-1", 1, 4);
        storage.getInvertedIndex().addTerm("title", "java", "doc-1", 3, 4);
        storage.getInvertedIndex().addField("body", "search", "doc-1", 2, 4);
        InMemoryIndexReader reader = new InMemoryIndexReader(storage);

        QueryContext context = reader.buildContext("doc-1", Map.of(
                "title", List.of("java"),
                "body", List.of("search")
        ));

        assertThat(context.getStatsOfDoc()).hasSize(2);
        assertThat(context.getStatsOfDoc()).anySatisfy(termStats -> {
            assertThat(termStats.getTf()).isEqualTo(0.5);
            assertThat(termStats.getPositions()).containsExactly(1, 3);
        });
        assertThat(context.getStatsOfDoc()).anySatisfy(termStats -> {
            assertThat(termStats.getTf()).isEqualTo(0.25);
            assertThat(termStats.getPositions()).containsExactly(2);
        });
    }

    @Test
    void buildContextShouldStoreNullStatsWhenRequestedTermDoesNotAppearInDocument() {
        IndexStorage storage = storageWithDocuments();
        storage.getInvertedIndex().addField("title", "java", "doc-2", 1, 5);
        InMemoryIndexReader reader = new InMemoryIndexReader(storage);

        QueryContext context = reader.buildContext("doc-1", Map.of("title", List.of("java")));

        assertThat(context.getStatsOfDoc()).containsExactly((TermStats) null);
    }

    @Test
    void buildContextShouldCountTermAppearancesAcrossAllDocuments() {
        IndexStorage storage = storageWithDocuments();
        storage.getInvertedIndex().addField("title", "java", "doc-1", 1, 5);
        storage.getInvertedIndex().addTerm("title", "java", "doc-1", 3, 5);
        storage.getInvertedIndex().addTerm("title", "java", "doc-2", 2, 4);
        storage.getInvertedIndex().addField("body", "search", "doc-1", 1, 5);
        storage.getInvertedIndex().addTerm("body", "search", "doc-2", 1, 4);
        storage.getInvertedIndex().addTerm("body", "search", "doc-2", 4, 4);
        InMemoryIndexReader reader = new InMemoryIndexReader(storage);

        QueryContext context = reader.buildContext("doc-1", Map.of(
                "title", List.of("java"),
                "body", List.of("search")
        ));

        assertThat(context.getTermsDf()).containsEntry("java", 3);
        assertThat(context.getTermsDf()).containsEntry("search", 3);
    }

    private static IndexStorage storageWithDocuments() {
        IndexStorage storage = new IndexStorage(new InMemoryInvertedIndex());
        storage.addDocument("doc-1", List.of(new Field("title", String.class, 4, true, true, "java")));
        storage.addDocument("doc-2", List.of(new Field("title", String.class, 6, true, true, "search")));
        return storage;
    }
}
