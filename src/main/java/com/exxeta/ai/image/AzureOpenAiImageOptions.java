package com.exxeta.ai.image;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.image.ImageOptions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The configuration information for a image generation request.
 *
 * @author Benoit Moussaud
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AzureOpenAiImageOptions implements ImageOptions {

    public static final String DEFAULT_IMAGE_MODEL = ImageModel.DALL_E_3.getValue();

    /**
     * The number of images to generate. Must be between 1 and 10. For dall-e-3, only n=1
     * is supported.
     */
    @Setter
    @Getter
    @JsonProperty("n")
    private Integer n;

    /**
     * The model dall-e-3 or dall-e-2 By default dall-e-3
     */
    @Setter
    @JsonProperty(value = "com/exxeta/ai/model")
    private String model = ImageModel.DALL_E_3.value;

    /**
     * The deployment name as defined in Azure Open AI Studio when creating a deployment
     * backed by an Azure OpenAI base model.
     */
    @Setter
    @Getter
    @JsonProperty(value = "deployment_name")
    private String deploymentName;

    /**
     * The width of the generated images. Must be one of 256, 512, or 1024 for dall-e-2.
     */
    @Getter
    @JsonProperty("size_width")
    private Integer width;

    /**
     * The height of the generated images. Must be one of 256, 512, or 1024 for dall-e-2.
     */
    @Getter
    @JsonProperty("size_height")
    private Integer height;

    /**
     * The quality of the image that will be generated. hd creates images with finer
     * details and greater consistency across the image. This param is only supported for
     * dall-e-3. standard or hd
     */
    @Setter
    @Getter
    @JsonProperty("quality")
    private String quality;

    /**
     * The format in which the generated images are returned. Must be one of url or
     * b64_json.
     */
    @Setter
    @Getter
    @JsonProperty("response_format")
    private String responseFormat;

    /**
     * The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024 for
     * dall-e-2. Must be one of 1024x1024, 1792x1024, or 1024x1792 for dall-e-3 models.
     */
    @Setter
    @JsonProperty("size")
    private String size;

    /**
     * The style of the generated images. Must be one of vivid or natural. Vivid causes
     * the model to lean towards generating hyper-real and dramatic images. Natural causes
     * the model to produce more natural, less hyper-real looking images. This param is
     * only supported for dall-e-3. natural or vivid
     */
    @Setter
    @Getter
    @JsonProperty("style")
    private String style;

    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor
     * and detect abuse.
     */
    @Setter
    @Getter
    @JsonProperty("user")
    private String user;

    @Override
    public String getModel() {
        return model;
    }

    public void setWidth(Integer width) {
        this.width = width;
        this.size = this.width + "x" + this.height;
    }

    public void setHeight(Integer height) {
        this.height = height;
        this.size = this.width + "x" + this.height;
    }

    public String getSize() {
        if (this.size != null) {
            return this.size;
        }
        return (this.width != null && this.height != null) ? this.width + "x" + this.height : null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AzureOpenAiImageOptions that))
            return false;
        return Objects.equals(n, that.n) && Objects.equals(model, that.model)
               && Objects.equals(deploymentName, that.deploymentName) && Objects.equals(width, that.width)
               && Objects.equals(height, that.height) && Objects.equals(quality, that.quality)
               && Objects.equals(responseFormat, that.responseFormat) && Objects.equals(size, that.size)
               && Objects.equals(style, that.style) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, model, deploymentName, width, height, quality, responseFormat, size, style, user);
    }

    @Override
    public String toString() {
        return "AzureOpenAiImageOptions{" + "n=" + n + ", model='" + model + '\'' + ", deploymentName='"
               + deploymentName + '\'' + ", width=" + width + ", height=" + height + ", quality='" + quality + '\''
               + ", responseFormat='" + responseFormat + '\'' + ", size='" + size + '\'' + ", style='" + style + '\''
               + ", user='" + user + '\'' + '}';
    }

    public static class Builder {

        private final AzureOpenAiImageOptions options;

        private Builder() {
            this.options = new AzureOpenAiImageOptions();
        }

        public Builder withN(Integer n) {
            options.setN(n);
            return this;
        }

        public Builder withModel(String model) {
            options.setModel(model);
            return this;
        }

        public Builder withDeploymentName(String deploymentName) {
            options.setDeploymentName(deploymentName);
            return this;
        }

        public Builder withResponseFormat(String responseFormat) {
            options.setResponseFormat(responseFormat);
            return this;
        }

        public Builder withWidth(Integer width) {
            options.setWidth(width);
            return this;
        }

        public Builder withHeight(Integer height) {
            options.setHeight(height);
            return this;
        }

        public Builder withUser(String user) {
            options.setUser(user);
            return this;
        }

        public AzureOpenAiImageOptions build() {
            return options;
        }

        public Builder withStyle(String style) {
            options.setStyle(style);
            return this;
        }

    }

    @Getter
    public enum ImageModel {

        /**
         * The latest DALL·E model released in Nov 2023.
         */
        DALL_E_3("dall-e-3"),

        /**
         * The previous DALL·E model released in Nov 2022. The 2nd iteration of DALL·E
         * with more realistic, accurate, and 4x greater resolution images than the
         * original model.
         */
        DALL_E_2("dall-e-2");

        private final String value;

        ImageModel(String model) {
            this.value = model;
        }

    }

}