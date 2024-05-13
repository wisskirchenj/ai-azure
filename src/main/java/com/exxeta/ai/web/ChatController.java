package com.exxeta.ai.web;

import com.exxeta.ai.image.AzureOpenAiImageClient;
import com.exxeta.ai.model.QuizQuestions;
import com.exxeta.ai.vectorstore.VectorStoreConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("java:S6833")
@Slf4j
@RequiredArgsConstructor
@Controller
class ChatController {

    private static final String HEADING = "heading";
    private final ChatClient azureOpenAiChatClient;
    private final ImageClient azureOpenAiImageClient;
    private final VectorStore vectorStore;
    private final VectorStoreConfiguration vectorStoreConfiguration;

    @Value("classpath:/prompts/chat-with-data.st")
    private Resource chatDataTemplate;
    @Value("classpath:/prompts/rag-template.st")
    private Resource ragTemplate;
    @Value("${spring.ai.azure.openai.chat.options.deployment-name}")
    private String deploymentModel;

    @GetMapping("/todos")
    String list(final Model model) {
        model.addAttribute("todos",
                List.of(Map.of("id", 1, "name", "Buy milk"),
                        Map.of("id", 2, "name", "Buy eggs"),
                        Map.of("id", 3, "name", "Buy bread")));
        return "index";
    }

    @GetMapping("/chat")
    String data(final Model model) {
        model.addAttribute(HEADING, "Chat with Context Data (%s)".formatted(deploymentModel));
        return "chat";
    }

    @GetMapping("/chat-rag")
    String chatRag(final Model model) {
        model.addAttribute(HEADING, "Chat (%s) with RAG on".formatted(deploymentModel));
        model.addAttribute("ragResource", vectorStoreConfiguration.getResource());
        return "rag";
    }

    @GetMapping("/gen-quiz")
    String quizGenerate(final Model model) {
        model.addAttribute(HEADING, "Generate Quiz Questions with AI (%s)".formatted(deploymentModel));
        return "quiz";
    }

    @GetMapping("/quiz-table")
    String quizTable(final Model model, @RequestParam String topic, @RequestParam(defaultValue = "3") int questions) {
        model.addAttribute(HEADING, "Generate Quiz Questions with AI (%s)".formatted(deploymentModel));
        model.addAttribute("questions", quiz(topic, questions));
        return "quiztable";
    }

    @GetMapping("/chat-simple")
    @ResponseBody
    String chat(@RequestParam(defaultValue = "Say: This is a test message") String prompt) {
        return azureOpenAiChatClient.call(prompt);
    }

    @GetMapping("/chat-data")
    @ResponseBody
    String chatWithData(@RequestParam(defaultValue = "John Doe lives in Fantasytown") String data, String prompt) {
        var template = new PromptTemplate(chatDataTemplate);
        Map<String, Object> map = Map.of("Context", data, "Question", prompt);
        return azureOpenAiChatClient.call(template.render(map));
    }

    @GetMapping("/image")
    @ResponseBody
    String image(@RequestParam(defaultValue = "Generate a 100 x 100 Pixel Image with the OpenAI logo") String prompt) {
        var response = ((AzureOpenAiImageClient)azureOpenAiImageClient).call(prompt);
        return response.getResult().getOutput().getUrl();
    }

    @GetMapping("/rag")
    @ResponseBody
    String springBoot(@RequestParam(defaultValue = "What is the emblem of Summer Olympics 2024") String prompt) {
        var similarDocuments = vectorStore.similaritySearch(SearchRequest.query(prompt).withTopK(2));
        var contentList = similarDocuments.stream().map(Document::getContent).toList();
        var promptTemplate = new PromptTemplate(ragTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", prompt);
        promptParameters.put("documents", String.join("\n", contentList));

        return azureOpenAiChatClient.call(promptTemplate.render(promptParameters));
    }

    @GetMapping("/quiz/{topic}")
    @ResponseBody
    QuizQuestions quiz(@PathVariable String topic, @RequestParam(defaultValue = "3") int questions) {
        var outputParser = new BeanOutputParser<>(QuizQuestions.class);
        var userPrompt = """
                Generate {questions} quiz questions with different difficulties on the topic {topic}.
                {format}
                """;
        var promptTemplate = new PromptTemplate(userPrompt,
                Map.of("topic", topic, "questions", questions, "format", outputParser.getFormat()));
        return outputParser.parse(azureOpenAiChatClient.call(promptTemplate.render()));
    }

}
