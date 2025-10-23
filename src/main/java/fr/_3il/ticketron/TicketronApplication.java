package fr._3il.ticketron;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.observability.api.event.AiServiceStartedEvent;
import dev.langchain4j.service.AiServices;
import fr._3il.ticketron.ocr.ImagePreprocessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TicketronApplication {

  @Bean
  public ChatModel chatModel(@Autowired EnvGetter envGetter) {
    return OllamaChatModel.builder()
            .baseUrl(envGetter.getModelUrl())
            .modelName(envGetter.getModelName())
            .build();

  }

  @Bean
  public Ticketron ticketron(@Autowired ChatModel chatModelr) {
    return AiServices.builder(Ticketron.class)
            .chatModel(chatModelr)
            .build();


  }



  public static void main(String[] args) {
    SpringApplication.run(TicketronApplication.class, args);
  }

}
