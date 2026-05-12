package org;

import org.analyzing.analyzerStrategy.AnalyzerStrategyFactory;
import org.indexing.InMemoryIndexWriter;
import org.indexing.IndexFile;
import org.quering.DocQueryResult;
import org.quering.Query;
import org.quering.QueryProcessor;
import org.quering.QueryType;
import org.reading.InMemoryIndexReader;
import org.scoring.calculation.ScoreRegistry;
import org.storage.FieldDefinition;
import org.storage.FieldValue;
import org.storage.IndexStorage;
import org.storage.invertedIndex.InvertedIndexProvider;
import org.utils.ConfigLoader;
import org.utils.config.AppConfig;

import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        try {
            AppConfig appConfig = ConfigLoader.load();


            // 1. Build Analyzer
            AnalyzerStrategyFactory analyzerFactory = new AnalyzerStrategyFactory(appConfig);

            // 2. Build Storage
            InvertedIndexProvider invertedIndexProvider = new InvertedIndexProvider(appConfig);
            IndexStorage indexStorage = new IndexStorage(invertedIndexProvider.provide());

            // 3. Build Writer
            InMemoryIndexWriter writer =
                    new InMemoryIndexWriter(
                            analyzerFactory.getAnalyzerStrategy(),
                            indexStorage
                    );


            // 4. Index Document

            String content1 = "Java, Search Engine.";
            String content2 = "Java is a @powerful language for building search engine";
            List<FieldValue> document = List.of(
                    new FieldValue(
                            new FieldDefinition("title", false, true),
                            content1,
                            content1.length()
                    ),
                    new FieldValue(
                            new FieldDefinition("body", true, true),
                            content2,
                            content2.length()
                    )
            );

            writer.addDocument(document);

            String content3 = "All all or Nothing";
            String content4 = "Is knowing the all details good?";
            List<FieldValue> document2 = List.of(
                    new FieldValue(
                            new FieldDefinition("title", true, true),
                            content3,
                            content3.length()
                    ),
                    new FieldValue(
                            new FieldDefinition("body", true, true),
                            content4,
                            content4.length()
                    )
            );

            writer.addDocument(document2);
            IndexFile indexFile = new IndexFile(writer);
            indexFile.indexFile("src/main/data/document2.txt");
            indexFile.indexFile("src/main/data/document1.txt");

            // 5. Build Reader
            InMemoryIndexReader reader =
                    new InMemoryIndexReader(indexStorage);

            // 6. Build Query Processor
            ScoreRegistry scoreRegistry = new ScoreRegistry(appConfig);
            QueryProcessor processor =
                    new QueryProcessor(
                            analyzerFactory.getAnalyzerStrategy(),
                            reader,
                            scoreRegistry.get()
                    );


            // 7. Build Query
            Query query = new Query(
                    UUID.randomUUID().toString(),
                    java.util.Map.of(
                            "name", "gaming",
                            "price", "89.99",
                            "category","electronics"
                    ),
                    10,
                    0,
                    QueryType.OR
            );


            // 8. Execute Query
            List<DocQueryResult> results = processor.process(query);

            // 9. Print Results
            System.out.println("\nSearch Results:");

            for (DocQueryResult docQueryResult : results) {

                System.out.println("Doc id: " + docQueryResult.docId());
                System.out.println("Score: " + docQueryResult.score());
                System.out.println("Doc fields:");
                for (FieldValue field : docQueryResult.fields()) {
                    System.out.println("\t" + field.getDefinition().fieldName() + ": " + field.getContent());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}