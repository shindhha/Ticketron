package fr._3il.ticketron;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.observability.api.event.AiServiceStartedEvent;
import dev.langchain4j.service.AiServices;
import fr._3il.ticketron.api.services.ExpenseService;
import fr._3il.ticketron.ocr.ImagePreprocessor;
import fr._3il.ticketron.ocr.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.URISyntaxException;

@SpringBootApplication
public class TicketronApplication {

  @Bean
  public ChatModel chatModel(@Autowired EnvGetter envGetter) {
    ChatModel chatModel = OllamaChatModel.builder()
            .baseUrl(envGetter.getModelUrl())
            .modelName(envGetter.getModelName())
            .logRequests(true)
            .logResponses(true)
            .build();
    return chatModel;

  }

  @Bean
  public Ticketron ticketron(@Autowired ChatModel chatModel,
                             @Autowired OcrService ocrService,
                             @Autowired ExpenseService es) throws URISyntaxException {
    return AiServices.builder(Ticketron.class)
            .chatModel(chatModel)
            .tools(ocrService, es)
            .build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }


  public static void main(String[] args) {
    SpringApplication.run(TicketronApplication.class, args);
  }

}
