package fr._3il.ticketron;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import fr._3il.ticketron.agents.ExpenseInterpreter;
import fr._3il.ticketron.agents.Ticketron;
import fr._3il.ticketron.ocr.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.URISyntaxException;
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
  public Ticketron ticketron(@Autowired ChatModel chatModel,
                             @Autowired OcrService ocrService) throws URISyntaxException {
    return AgenticServices.agentBuilder(Ticketron.class)
            .chatModel(chatModel)
            .tools(ocrService)
            .build();
  }
  @Bean
  public ExpenseInterpreter expenseExtractor(@Autowired ChatModel chatModel,
                                             @Autowired  OcrService ocr) {
    return AgenticServices.agentBuilder(ExpenseInterpreter.class)
            .chatModel(chatModel)
            .build();
  }



  public static void main(String[] args) {
    SpringApplication.run(TicketronApplication.class, args);
  }

}
