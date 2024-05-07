package com.exxeta.ai.web;

import com.exxeta.ai.image.AzureOpenAiImageClient;
import com.exxeta.ai.model.QuizQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
class ChatController {

    private final ChatClient azureOpenAiChatClient;
    private final ImageClient azureOpenAiImageClient;
    private final VectorStore vectorStore;
    @Value("classpath:/prompts/chat-with-data.st")
    private Resource chatDataTemplate;
    @Value("classpath:/prompts/rag-template.st")
    private Resource ragTemplate;

    @GetMapping("/chat")
    String chat(@RequestParam(defaultValue = "Say: This is a test message") String prompt) {
        return azureOpenAiChatClient.call(prompt);
    }

    @GetMapping("/image")
    String image(@RequestParam(defaultValue = "Generate a 100 x 100 Pixel Image with the OpenAI logo") String prompt) {
        var response = ((AzureOpenAiImageClient)azureOpenAiImageClient).call(prompt);
        return response.getResult().getOutput().getUrl();
    }

    @GetMapping("/chat-data")
    String chatWithData(@RequestParam(defaultValue = "John Doe lives in Fantasytown") String data, String prompt) {
        var template = new PromptTemplate(chatDataTemplate);
        Map<String, Object> map = Map.of("Context", data, "Question", prompt);
        return azureOpenAiChatClient.call(template.render(map));
    }

    @GetMapping("/rag")
    String springBoot(@RequestParam(defaultValue = "What is the emblem of Summer Olympics 2024") String prompt) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(prompt).withTopK(2));
        List<String> contentList = similarDocuments.stream().map(Document::getContent).toList();
        var promptTemplate = new PromptTemplate(ragTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", prompt);
        promptParameters.put("documents", String.join("\n", contentList));

        ChatResponse response = azureOpenAiChatClient.call(promptTemplate.create(promptParameters));
        return response.getResult().getOutput().getContent();
    }

    @GetMapping("/quiz/{topic}")
    QuizQuestions quiz(@PathVariable String topic, @RequestParam(defaultValue = "3") int questions) {
        var outputParser = new BeanOutputParser<>(QuizQuestions.class);
        var userPrompt = """
                Generate {questions} quiz questions with different difficulties on the topic {topic}.
                {format}
                """;
        var promptTemplate = new PromptTemplate(userPrompt,
                Map.of("topic", topic, "questions", questions, "format", outputParser.getFormat()));
        var chatResponse = azureOpenAiChatClient.call(promptTemplate.create());
        return outputParser.parse(chatResponse.getResult().getOutput().getContent());
    }

    record QuizQuestions(List<QuizQuestion> questions) {
    }

}
