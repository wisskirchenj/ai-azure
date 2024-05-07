package com.exxeta.ai.image;

import com.azure.ai.openai.OpenAIClient;
import org.springframework.ai.image.ImageClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageConfiguration {

    @Bean
    public ImageClient azureOpenAiImageClient(OpenAIClient openAIClient) {
        return new AzureOpenAiImageClient(openAIClient);
    }
}
