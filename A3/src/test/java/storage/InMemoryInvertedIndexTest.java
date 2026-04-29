package storage;

import org.junit.jupiter.api.Test;
import org.storage.invertedIndex.InMemoryInvertedIndex;
import org.storage.invertedIndex.PostingList;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryInvertedIndexTest {

    @Test
    void addFieldShouldCreateFieldTokenAndPosting() {
        InMemoryInvertedIndex index = new InMemoryInvertedIndex();

        index.addField("title", "hello", "doc-1", 3, 10);

        PostingList postingList = index.getPostingListByTerm("title", "hello");
        assertThat(postingList).isNotNull();
        assertThat(postingList.getPostings()).containsOnlyKeys("doc-1");
        assertThat(postingList.getPostings().get("doc-1").getTf()).isEqualTo(0.1);
        assertThat(postingList.getPostings().get("doc-1").getPositions()).containsExactly(3);
    }

    @Test
    void addTermShouldAddNewTokenToExistingField() {
        InMemoryInvertedIndex index = new InMemoryInvertedIndex();

        index.addField("body", "search", "doc-1", 0, 5);
        index.addTerm("body", "engine", "doc-1", 1, 5);

        assertThat(index.getPostings("body")).containsOnlyKeys("search", "engine");
        assertThat(index.getPostingListByTerm("body", "engine").getPostings().get("doc-1").getTf())
                .isEqualTo(0.2);
        assertThat(index.getPostingListByTerm("body", "engine").getPostings().get("doc-1").getPositions())
                .containsExactly(1);
    }

    @Test
    void addTermShouldAccumulateTfAndPositionsForExistingDocument() {
        InMemoryInvertedIndex index = new InMemoryInvertedIndex();

        index.addField("body", "java", "doc-1", 2, 4);
        index.addTerm("body", "java", "doc-1", 7, 4);

        assertThat(index.getPostingListByTerm("body", "java").getPostings().get("doc-1").getTf())
                .isEqualTo(0.5);
        assertThat(index.getPostingListByTerm("body", "java").getPostings().get("doc-1").getPositions())
                .containsExactly(2, 7);
    }

    @Test
    void addTermShouldAddNewDocumentToExistingToken() {
        InMemoryInvertedIndex index = new InMemoryInvertedIndex();

        index.addField("body", "java", "doc-1", 2, 4);
        index.addTerm("body", "java", "doc-2", 1, 2);

        PostingList postingList = index.getPostingListByTerm("body", "java");
        assertThat(postingList.getPostings()).containsOnlyKeys("doc-1", "doc-2");
        assertThat(postingList.getPostings().get("doc-1").getTf()).isEqualTo(0.25);
        assertThat(postingList.getPostings().get("doc-2").getTf()).isEqualTo(0.5);
        assertThat(postingList.getPostings().get("doc-2").getPositions()).containsExactly(1);
    }
}
