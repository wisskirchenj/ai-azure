package com.exxeta.ai.vectorstore;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@Getter
@Setter
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "app.vectorstore")
public class VectorStoreConfiguration {

    private String vectorStorePath;

    private Resource path;

    private Resource resource;

    @Bean
    VectorStore simpleVectorStore(EmbeddingClient embeddingClient) throws IOException {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingClient);
        if (path.exists()) { // load existing vector store if exists
            log.info("Vector store found - Loading from {}", path.getFilename());
            simpleVectorStore.load(path.getFile());
        } else { // otherwise load the documents and save the vector store
            log.info("No Vector store found - creating embeddings into {}", path.getFilename());
            TikaDocumentReader documentReader = new TikaDocumentReader(resource);
            List<Document> documents = documentReader.get();
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);
            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(path.getFile());
        }
        return simpleVectorStore;
    }
}
