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

/**
 * Classe principale de l'application Ticketron.
 * Configure et démarre l'application Spring Boot pour l'automatisation
 * du traitement des tickets de caisse via OCR et agent LLM.
 * Définit les beans Spring nécessaires : modèle de chat Ollama et agent Ticketron.
 */
@SpringBootApplication
public class TicketronApplication {

    /**
     * Crée et configure le bean ChatModel basé sur Ollama.
     * Le modèle est configuré avec l'URL et le nom récupérés depuis la configuration,
     * avec logs activés et timeout de 5 minutes.
     *
     * @param envGetter service fournissant les variables d'environnement (URL et nom du modèle)
     * @return instance configurée de ChatModel pour communiquer avec Ollama
     */
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



    /**
     * Point d'entrée principal de l'application.
     * Démarre le contexte Spring Boot et initialise tous les services.
     *
     * @param args arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SpringApplication.run(TicketronApplication.class, args);
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
  /**
   * Agent permettant de déterminer la catégorie d’une dépense
   * (ex : Alimentaire, Transport, Logement, etc.).
   *
   * Utilise la mémoire de conversation sur les 10 derniers messages
   * afin de garder le contexte entre les appels sans surcharge.
   *
   * @param chatModel modèle LLM utilisé pour générer les décisions
   * @param categoryService service métier permettant d'accéder à la liste des catégories
   * @return un agent LangChain4j prêt à catégoriser des données
   */
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
  /**
   * Agent responsable de la sauvegarde d’une dépense après analyse.
   *
   * Il délègue la persistance au service métier ExpenseService.
   * outputName("") indique qu’on ne souhaite pas renommer la sortie du LLM.
   *
   * @param model modèle LLM utilisé
   * @param expenseService service métier enregistrant la dépense en base
   * @return agent spécialisé dans l’enregistrement des dépenses
   */
  @Bean
  public SaveExpenseAgent persister(@Autowired ChatModel model,
                                    @Autowired ExpenseService expenseService) {
    return AgenticServices.agentBuilder(SaveExpenseAgent.class)
            .chatModel(model)
            .tools(expenseService)
            .outputName("")
            .build();
  }


  /**
   * Configuration CORS permettant au front-end d’accéder à l’API Spring.
   * Ici, le front local en http://localhost:3000 est autorisé.
   *
   * Ajustable si le front change d’URL ou pour un environnement de prod.
   */
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




}
