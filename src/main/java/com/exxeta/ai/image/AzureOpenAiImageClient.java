package com.exxeta.ai.image;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.*;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.util.Assert;

import java.util.List;

import static java.lang.String.format;

/**
 * {@link ImageClient} implementation for {@literal Microsoft Azure AI} backed by
 * {@link OpenAIClient}.
 *
 * @author Benoit Moussaud
 * @see ImageClient
 * @see com.azure.ai.openai.OpenAIClient
 */
public class AzureOpenAiImageClient implements ImageClient {

    private static final String DEFAULT_DEPLOYMENT_NAME = AzureOpenAiImageOptions.DEFAULT_IMAGE_MODEL;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OpenAIClient openAIClient;

    @Getter
    private final AzureOpenAiImageOptions imageOptions;

    public AzureOpenAiImageClient(OpenAIClient openAIClient) {
        this(openAIClient, AzureOpenAiImageOptions.builder().withDeploymentName(DEFAULT_DEPLOYMENT_NAME).build());
    }

    public AzureOpenAiImageClient(OpenAIClient microsoftOpenAiClient, AzureOpenAiImageOptions options) {
        Assert.notNull(microsoftOpenAiClient, "com.azure.ai.openai.OpenAIClient must not be null");
        Assert.notNull(options, "AzureOpenAiChatOptions must not be null");
        this.openAIClient = microsoftOpenAiClient;
        this.imageOptions = options;
    }

    public ImageResponse call(String prompt) {
        return call(new ImagePrompt(prompt, imageOptions));
    }

    @Override
    public ImageResponse call(ImagePrompt imagePrompt) {
        ImageGenerationOptions imageGenerationOptions = toOpenAiImageOptions(imagePrompt);
        String deploymentOrModelName = getDeploymentName(imagePrompt);
        if (logger.isTraceEnabled()) {
            logger.trace("Azure ImageGenerationOptions call {} with the following options : {} ", deploymentOrModelName,
                    toPrettyJson(imageGenerationOptions));
        }

        var images = openAIClient.getImageGenerations(deploymentOrModelName, imageGenerationOptions);

        if (logger.isTraceEnabled()) {
            logger.trace("Azure ImageGenerations: {}", toPrettyJson(images));
        }

        List<ImageGeneration> imageGenerations = images.getData().stream().map(entry -> {
            var image = new Image(entry.getUrl(), entry.getBase64Data());
            var metadata = new AzureOpenAiImageGenerationMetadata(entry.getRevisedPrompt());
            return new ImageGeneration(image, metadata);
        }).toList();

        ImageResponseMetadata openAiImageResponseMetadata = AzureOpenAiImageResponseMetadata.from(images);
        return new ImageResponse(imageGenerations, openAiImageResponseMetadata);
    }

    private String toPrettyJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            return "JsonProcessingException:" + e + " [" + object.toString() + "]";
        }
    }

    /**
     * Return the deployment-name if provided or use the model name.
     * @param prompt the image prompt
     * @return Return the deployment-name if provided or use the model name.
     */
    private String getDeploymentName(ImagePrompt prompt) {
        var runtimeImageOptions = prompt.getOptions();

        if (this.imageOptions != null) {
            // Merge options fixed in beta7
            // https://github.com/Azure/azure-sdk-for-java/issues/38183
            runtimeImageOptions = ModelOptionsUtils.merge(runtimeImageOptions, this.imageOptions,
                    AzureOpenAiImageOptions.class);
        }

        if (runtimeImageOptions != null) {
            if (runtimeImageOptions instanceof AzureOpenAiImageOptions runtimeAzureOpenAiImageOptions) {
                if (runtimeAzureOpenAiImageOptions.getDeploymentName() != null) {
                    return runtimeAzureOpenAiImageOptions.getDeploymentName();
                }
            }

        }

        // By default the one provided in the image prompt
        return prompt.getOptions().getModel();

    }

    private ImageGenerationOptions toOpenAiImageOptions(ImagePrompt prompt) {

        if (prompt.getInstructions().size() > 1) {
            throw new RuntimeException(format("implementation support 1 image instruction only, found %s",
                    prompt.getInstructions().size()));
        }
        if (prompt.getInstructions().isEmpty()) {
            throw new RuntimeException("please provide image instruction, current is empty");
        }

        var instructions = prompt.getInstructions().get(0).getText();
        var runtimeImageOptions = prompt.getOptions();
        ImageGenerationOptions imageGenerationOptions = new ImageGenerationOptions(instructions);

        if (this.imageOptions != null) {
            // Merge options fixed in beta7
            // https://github.com/Azure/azure-sdk-for-java/issues/38183
            runtimeImageOptions = ModelOptionsUtils.merge(runtimeImageOptions, this.imageOptions,
                    AzureOpenAiImageOptions.class);
        }

        if (runtimeImageOptions != null) {
            // Handle portable image options
            if (runtimeImageOptions.getN() != null) {
                imageGenerationOptions.setN(runtimeImageOptions.getN());
            }
            if (runtimeImageOptions.getModel() != null) {
                imageGenerationOptions.setModel(runtimeImageOptions.getModel());
            }
            if (runtimeImageOptions.getResponseFormat() != null) {
                // b64_json or url
                imageGenerationOptions.setResponseFormat(
                        ImageGenerationResponseFormat.fromString(runtimeImageOptions.getResponseFormat()));
            }
            if (runtimeImageOptions.getWidth() != null && runtimeImageOptions.getHeight() != null) {
                imageGenerationOptions.setSize(
                        ImageSize.fromString(runtimeImageOptions.getWidth() + "x" + runtimeImageOptions.getHeight()));
            }

            // Handle OpenAI specific image options
            if (runtimeImageOptions instanceof AzureOpenAiImageOptions runtimeAzureOpenAiImageOptions) {
                if (runtimeAzureOpenAiImageOptions.getQuality() != null) {
                    imageGenerationOptions
                            .setQuality(ImageGenerationQuality.fromString(runtimeAzureOpenAiImageOptions.getQuality()));
                }
                if (runtimeAzureOpenAiImageOptions.getStyle() != null) {
                    imageGenerationOptions
                            .setStyle(ImageGenerationStyle.fromString(runtimeAzureOpenAiImageOptions.getStyle()));
                }
                if (runtimeAzureOpenAiImageOptions.getUser() != null) {
                    imageGenerationOptions.setUser(runtimeAzureOpenAiImageOptions.getUser());
                }
            }
        }
        return imageGenerationOptions;
    }

}
