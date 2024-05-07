package com.exxeta.ai.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(AzureOpenAiImageOptionsProperties.CONFIG_PREFIX)
public class AzureOpenAiImageOptionsProperties {

    public static final String CONFIG_PREFIX = "spring.ai.azure.openai.image";

    /**
     * Enable Azure OpenAI chat client.
     */
    private boolean enabled = true;

    @NestedConfigurationProperty
    private AzureOpenAiImageOptions options = AzureOpenAiImageOptions.builder().build();

}
