package org;

import org.analyzing.analyzerStrategy.AnalyzerProvider;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.indexing.InMemoryIndexWriter;
import org.quering.Query;
import org.quering.QueryProcessor;
import org.quering.QueryType;
import org.reading.InMemoryIndexReader;
import org.scoring.ScoreResult;
import org.scoring.calculation.TfIdfScorer;
import org.storage.Field;
import org.storage.IndexStorage;
import org.storage.invertedIndex.InMemoryInvertedIndex;
import org.storage.invertedIndex.InvertedIndex;
import org.utils.ConfigLoader;
import org.utils.config.AppConfig;

import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        try {
            AppConfig appConfig = ConfigLoader.load();


            /*
             * ============================
             * 1. Build Analyzer
             * ============================
             */
            AnalyzerProvider analyzerProvider = new AnalyzerProvider(appConfig);
            AnalyzerStrategy analyzerStrategy = analyzerProvider.provide();

            /*
             * ============================
             * 2. Build Storage
             * ============================
             */
            InvertedIndex invertedIndex = new InMemoryInvertedIndex();
            IndexStorage indexStorage = new IndexStorage(invertedIndex);

            /*
             * ============================
             * 3. Build Writer
             * ============================
             */

            InMemoryIndexWriter writer =
                    new InMemoryIndexWriter(
                            analyzerStrategy,
                            indexStorage
                    );

            /*
             * ============================
             * 4. Index Document
             * ============================
             */

            String content1 = "Java Search Engine";
            String content2 = "Java is a powerful language for building search engine";
            List<Field> document = List.of(
                    new Field(
                            "title",
                            String.class,
                            content1.length(),
                            false,
                            true,
                            content1
                    ),
                    new Field(
                            "body",
                            String.class,
                            content2.length(),
                            true,
                            true,
                            content2
                    )
            );

            writer.addDocument(document);

            System.out.println("Document indexed successfully.");

            /*
             * ============================
             * 5. Build Reader
             * ============================
             */

            InMemoryIndexReader reader =
                    new InMemoryIndexReader(indexStorage);
            System.out.println( reader.getPosting("body"));
            System.out.println(reader.getPosting("title"));

            /*
             * ============================
             * 6. Build Query Processor
             * ============================
             */

            QueryProcessor processor =
                    new QueryProcessor(
                            analyzerStrategy,
                            reader,
                            new TfIdfScorer()
                    );

            /*
             * ============================
             * 7. Build Query
             * ============================
             */

            Query query = new Query(
                    UUID.randomUUID().toString(),
                    java.util.Map.of(
                            "title", "java",
                            "body", "search engine"
                    ),
                    10,
                    0,
                    QueryType.OR
            );

            /*
             * ============================
             * 8. Execute Query
             * ============================
             */

            List<ScoreResult> results =
                    processor.process(query);

            /*
             * ============================
             * 9. Print Results
             * ============================
             */

            System.out.println("\nSearch Results:");

            for (ScoreResult result : results) {
                System.out.println(
                        "DocId: " + result.docId()
                                + ", Score: " + result.totalScore()
                );
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}