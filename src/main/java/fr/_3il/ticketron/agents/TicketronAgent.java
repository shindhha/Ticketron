package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.agent.ErrorRecoveryResult;
import dev.langchain4j.agentic.agent.MissingArgumentException;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.output.OutputParsingException;
import fr._3il.ticketron.exceptions.InvalidExpenseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketronAgent {

  private final SupervisorAgent agent;

  /**
   * Agent principal agissant comme un orchestrateur entre les différents sous-agents.
   *
   * Ce wrapper permet d’éviter un comportement problématique lorsque Spring détruit et recrée
   * certains beans, ce qui provoque des crashs lorsqu’on combine Spring avec LangChain4j.
   *
   * @param model Modèle de conversation utilisé pour générer les réponses
   * @param cat   Sous-agent chargé de la catégorisation des dépenses
   * @param exp   Sous-agent chargé de l’analyse d’image du reçu
   * @param per   Sous-agent chargé de l’enregistrement de la dépense
   */
  public TicketronAgent(@Autowired ChatModel model,
                        @Autowired CategoriserAgent cat,
                        @Autowired ImageAnalyserAgent exp,
                        @Autowired SaveExpenseAgent per) {
    this.agent = AgenticServices.supervisorBuilder(SupervisorAgent.class)
            .chatModel(model)
            .subAgents(exp,cat, per)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
            .errorHandler(errorContext -> {
              Class<?> exceptionClass = errorContext.exception().getClass();
              if (exceptionClass.equals(InvalidExpenseException.class)) {
                return ErrorRecoveryResult.retry();
              }
              if (exceptionClass.equals(MissingArgumentException.class)
              || exceptionClass.equals(OutputParsingException.class))
                return ErrorRecoveryResult.retry();
              return ErrorRecoveryResult.result("");
            })
            .build();
  }
  /**
   * Analyse une image de reçu pour extraire les informations de dépense,
   * catégoriser la dépense et l’enregistrer via les sous-agents spécialisés.
   *
   * @param input L'image ou l'URL/Base64 du reçu à analyser
   * @return Résultat textuel de l’analyse et de l’opération réalisée
   */
  public String analyseReceiptImage(String input) {
    String context = "You goal is to categorise and save the expense on an image, yours agent have all the necessary abilities";
    return agent.invoke(input, context);
  }
}
