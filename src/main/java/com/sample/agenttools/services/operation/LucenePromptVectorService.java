package com.sample.agenttools.services.operation;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LucenePromptVectorService {
    private static final String INDEX_DIR = "data/lucene-index";
    private static final String FIELD_ID = "id";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_VECTOR = "embedding";

    private final FSDirectory directory;
    private final StandardAnalyzer analyzer;

    public LucenePromptVectorService() throws IOException {
        this.directory = FSDirectory.open(Paths.get(INDEX_DIR));
        this.analyzer = new StandardAnalyzer();
    }

    public void addPrompt(String id, String description, String content, float[] embedding) throws IOException {
        try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            Document doc = new Document();
            doc.add(new StringField(FIELD_ID, id, Field.Store.YES));
            doc.add(new TextField(FIELD_DESCRIPTION, description, Field.Store.YES));
            doc.add(new TextField(FIELD_CONTENT, content, Field.Store.YES));
            doc.add(new KnnFloatVectorField(FIELD_VECTOR, embedding));
            writer.updateDocument(new Term(FIELD_ID, id), doc);
        }
    }

    public List<Document> searchSimilarPrompts(float[] queryEmbedding, int k) throws IOException {
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            var query = new KnnFloatVectorQuery(FIELD_VECTOR, queryEmbedding, k);
            TopDocs topDocs = searcher.search(query, k);
            List<Document> results = new ArrayList<>();
            StoredFields storedFields = searcher.storedFields();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                results.add(storedFields.document(scoreDoc.doc));
            }
            return results;
        }
    }

    public void deletePrompt(String id) throws IOException {
        try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            writer.deleteDocuments(new Term(FIELD_ID, id));
        }
    }
}
