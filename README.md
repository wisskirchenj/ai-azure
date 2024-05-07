# Spring AI on Azure OpenAI Demo Application

Spring AI demo app to connect to ai.exxeta API deployed at Azure OpenAI. Various endpoints will be provided
to demonstrate the capabilities of Chat, RAG, vector store and diverse image use cases.

## Technology / External Libraries

- Spring Boot 3.2.5
- Spring AI 0.8.1 (spring-ai-azure-openai, spring-ai-azure-vector-store)
- Apache Tika Document Reader (spring-ai-tika-document-reader to read and tokenize arbitrary document types)
- Lombok
- Gradle 8.7

## Repository Contents

The sources and docs of main project and testing.

## Usage

Start the backend server by running the main class `AiAzureApplication` in the package `com.exxeta.ai`.
**Important:** Set an environment variable `AI_AZURE_OPENAI_API_KEY` with your personal API key. From IntelliJ,
this is done via Edit Configurations -> Environment Variables.

Choose the deployment model and specify in `application.properties` via `spring.ai.azure.openai.chat.options.deployment-name`
This repo's default is `gpt-35-turbo-16k`.

See IntelliJ's Endpoints-window or below for the available endpoints and their params.

## Program description

[Spring AI](https://docs.spring.io/spring-ai/reference) is an abstraction layer for AI services in the Spring ecosystem. 
It provides a common interface for various AI services, such as OpenAI, Azure OpenAI, Google, Huggingface, and others.
Synchronous and streaming APIs are supported. Pre-configured vector stores support RAG. Spring AI provides Mapping of 
AI Model output to POJOs, image generation, function calling, etc..

This repository contains a demo application - and for testing purpose - that connects to ai.exxeta API deployed at
Azure OpenAI. Currently the usage is only via CLI-tools as http(ie), curl or Postman.
But a simple UI will probably be provided soon.

A future deployment to the cloud *may be possible*, e.g. to provide access via RAG to training material by a
specialised chat bot.

Have fun!

## Project progress

**Project started on 06.05.24**

06.05.24 First commit. Basic Spring Boot application with Spring AI and Tika dependencies.
ChatController contains GET-endpoints for

> /chat?prompt={question}: normal chat with prompt as query parameter

> /chat-data?prompt={question}&data={stuffing}: chat with data stuffing (via additional query parameter "data")

> /image?prompt={question}: image generation with prompt as query parameter (not configured yet - not working)

> /rag?prompt={question}: RAG with prompt as query parameter, documents and path to json vector store need to be set via application.properties

> /quiz/{topic}?questions={number}: POJO-mapping example, where AI response generates `number` (default 3) quiz Q&A objects on the {topic} with diverse difficulty. 

