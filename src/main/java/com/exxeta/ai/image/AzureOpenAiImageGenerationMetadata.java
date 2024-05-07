package com.exxeta.ai.image;

import org.springframework.ai.image.ImageGenerationMetadata;

public record AzureOpenAiImageGenerationMetadata(String revisedPrompt) implements ImageGenerationMetadata {
}
