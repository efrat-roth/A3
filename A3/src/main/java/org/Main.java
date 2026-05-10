package org;

import org.analyzing.analyzerStrategy.AnalyzerStrategyFactory;
import org.indexing.InMemoryIndexWriter;
import org.indexing.IndexFile;
import org.quering.Query;
import org.quering.QueryProcessor;
import org.quering.QueryType;
import org.reading.InMemoryIndexReader;
import org.scoring.calculation.ScoreProvider;
import org.storage.FieldType;
import org.storage.IndexStorage;
import org.storage.invertedIndex.InvertedIndexProvider;
import org.utils.ConfigLoader;
import org.utils.config.AppConfig;

import java.util.List;
import java.util.Map;
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
            List<FieldType> document = List.of(
                    new FieldType(
                            "title",
                            String.class,
                            content1.length(),
                            false,
                            true,
                            content1
                    ),
                    new FieldType(
                            "body",
                            String.class,
                            content2.length(),
                            true,
                            true,
                            content2
                    )
            );

            writer.addDocument(document);

            String content3 = "All all or Nothing";
            String content4 = "Is knowing the all details good?";
            List<FieldType> document2 = List.of(
                    new FieldType(
                            "title",
                            String.class,
                            content3.length(),
                            true,
                            true,
                            content3
                    ),
                    new FieldType(
                            "body",
                            String.class,
                            content4.length(),
                            true,
                            true,
                            content4
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
            ScoreProvider scoreProvider = new ScoreProvider(appConfig);
            QueryProcessor processor =
                    new QueryProcessor(
                            analyzerFactory.getAnalyzerStrategy(),
                            reader,
                            scoreProvider.provide()
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
            Map<String, Map<List<FieldType>, Double>> results = processor.process(query);

            // 9. Print Results
            System.out.println("\nSearch Results:");

            for (Map.Entry<String, Map<List<FieldType>, Double>> entry : results.entrySet()) {

                System.out.println("Doc id: " + entry.getKey());

                for (Map.Entry<List<FieldType>, Double> docDetails : entry.getValue().entrySet()) {

                    System.out.println("Score: " + docDetails.getValue());
                    System.out.println("Doc fields:");

                    for (FieldType field : docDetails.getKey()) {
                        System.out.println("\t" + field.getFieldName() + ": " + field.getContent());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}