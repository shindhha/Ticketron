package fr._3il.ticketron;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import fr._3il.ticketron.agents.CategoriserAgent;
import fr._3il.ticketron.agents.ImageAnalyserAgent;
import fr._3il.ticketron.agents.SaveExpenseAgent;
import fr._3il.ticketron.api.services.CategoryService;
import fr._3il.ticketron.api.services.ExpenseService;
import fr._3il.ticketron.ocr.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@SpringBootApplication
public class TicketronApplication {

  @Bean
  public ChatModel chatModel(@Autowired EnvGetter envGetter) {
    ChatModel chatModel = OllamaChatModel.builder()
            .baseUrl(envGetter.getModelUrl())
            .modelName(envGetter.getModelName())
            .logRequests(true)
            .logResponses(true)
            .timeout(Duration.ofSeconds(300))
            .build();
    return chatModel;

  }

  @Bean
  public CategoriserAgent categoriser(@Autowired ChatModel chatModel,
                                      @Autowired CategoryService categoryService) {
    return AgenticServices.agentBuilder(CategoriserAgent.class)
            .chatModel(chatModel)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
            .tools(categoryService)
            .build();
  }
  @Bean
  public ImageAnalyserAgent expenseExtractor(@Autowired ChatModel chatModel,
                                             @Autowired OcrService ocrService) {
    return AgenticServices.agentBuilder(ImageAnalyserAgent.class)
            .chatModel(chatModel)
            .beforeAgentInvocation((value) -> {
              value.inputs().forEach((name, obj) -> {
                System.out.println("TEST");
                System.out.println("got : " + name + " : " + obj);
              });
            })
            .tools(ocrService)
            .build();
  }

  @Bean
  public SaveExpenseAgent persister(@Autowired ChatModel model,
                                    @Autowired ExpenseService expenseService) {
    return AgenticServices.agentBuilder(SaveExpenseAgent.class)
            .chatModel(model)
            .tools(expenseService)
            .outputName("")
            .build();
  }



  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000") // ton front Next.js
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
      }
    };
  }



  public static void main(String[] args) {
    SpringApplication.run(TicketronApplication.class, args);
  }

}
