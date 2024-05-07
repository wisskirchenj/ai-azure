package com.exxeta.ai.image;

import com.azure.ai.openai.models.ImageGenerations;
import org.springframework.ai.image.ImageResponseMetadata;
import org.springframework.util.Assert;

import java.util.Objects;

public class AzureOpenAiImageResponseMetadata implements ImageResponseMetadata {

    private final Long created;

    public static AzureOpenAiImageResponseMetadata from(ImageGenerations openAiImageResponse) {
        Assert.notNull(openAiImageResponse, "OpenAiImageResponse must not be null");
        return new AzureOpenAiImageResponseMetadata(openAiImageResponse.getCreatedAt().toEpochSecond());
    }

    protected AzureOpenAiImageResponseMetadata(Long created) {
        this.created = created;
    }

    @Override
    public Long created() {
        return this.created;
    }

    @Override
    public String toString() {
        return "AzureOpenAiImageResponseMetadata{" + "created=" + created + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AzureOpenAiImageResponseMetadata that))
            return false;
        return Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(created);
    }

}
