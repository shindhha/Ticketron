package fr._3il.ticketron;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.observability.api.event.AiServiceStartedEvent;
import dev.langchain4j.service.AiServices;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.services.ExpenseService;
import fr._3il.ticketron.ocr.ImagePreprocessor;
import fr._3il.ticketron.ocr.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.URISyntaxException;
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
     * Crée et configure le bean Ticketron, l'agent LLM principal de l'application.
     * L'agent est construit avec LangChain4j et dispose d'outils pour :
     * - Exécuter l'OCR sur des images (OcrService)
     * - Gérer les dépenses (ExpenseService)
     * - Construire des objets Expense (ExpenseBuilder)
     *
     * @param chatModel modèle de chat Ollama pour les interactions LLM
     * @param ocrService service OCR pour extraire le texte des images
     * @param es service de gestion des dépenses
     * @param eb builder pour créer des objets Expense
     * @return instance configurée de l'agent Ticketron avec tous ses outils
     * @throws URISyntaxException si la configuration des chemins de ressources échoue
     */
    @Bean
    public Ticketron ticketron(@Autowired ChatModel chatModel,
                               @Autowired OcrService ocrService,
                               @Autowired ExpenseService es,
                               @Autowired Expense.ExpenseBuilder eb) throws URISyntaxException {
        return AiServices.builder(Ticketron.class)
                .chatModel(chatModel)
                .tools(ocrService, es, eb)
                .build();
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

}
