package com.example.springmcpdemo.config;

import com.example.springmcpdemo.service.ShoppingCartService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider shoppingCartToolCallbackProvider(ShoppingCartService shoppingCartService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(shoppingCartService)
                .build();
    }
}