package com.exxeta.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.aot.DisabledInAotMode;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisabledInAotMode // MockBean not supported in AOT mode
class AiAzureApplicationTests {

    @MockBean
    VectorStore vectorStore;

    @Autowired
    ApplicationContext context;

    @Test
    void contextLoads() {
        assertTrue(context.containsBean("chatController"));
    }

}
