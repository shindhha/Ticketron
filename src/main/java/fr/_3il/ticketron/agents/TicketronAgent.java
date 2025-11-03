package fr._3il.ticketron.agents;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.agent.ErrorRecoveryResult;
import dev.langchain4j.agentic.agent.MissingArgumentException;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.output.OutputParsingException;
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
              if (exceptionClass.equals(MissingArgumentException.class)
                      || exceptionClass.equals(OutputParsingException.class))
                return ErrorRecoveryResult.retry();
              return ErrorRecoveryResult.result("");
            })
            .build();
  }

  public class SupervisorTools {
    @Tool("done")
    public String done(@P("response") String response) {
      return response; // renvoie tel quel -> sera la valeur retournée par agent.invoke(...)
    }
  }
  /**
   * Analyse une image de reçu pour extraire les informations de dépense,
   * catégoriser la dépense et l’enregistrer via les sous-agents spécialisés.
   *
   * @param input L'image ou l'URL/Base64 du reçu à analyser
   * @return Résultat textuel de l’analyse et de l’opération réalisée
   */
  public String analyseReceiptImage(String input) {
    String context = "You are an orchestrator. Decide whether to call a tool or to finish.\n" +
            "\n" +
            "DECISION RULES (strict):\n" +
            "1) If the user message is small talk / greeting / unrelated to expenses, RETURN:\n" +
            "   {\"agentName\":\"done\",\"arguments\":{\"response\": \"<brief reply in user's language>\"}}\n" +
            "2) Do NOT call imageAnalyser$2 if 'imgPath' is null/empty or not a readable local file.\n" +
            "3) If any tool returns an error (e.g. contains \"ERROR\" or \"Can't read input file!\"), STOP and return 'done' with a short explanation. Do NOT fabricate data.\n" +
            "4) Do NOT call persistExpense$3 unless you have a structured, validated expense (store/date/amount/items or OCR text). Never invent content.\n" +
            "5) Answer in the user's language. Output ONLY JSON: {\"agentName\": \"...\", \"arguments\": {...}}.";
    try {
      return agent.invoke(input, context);
    } catch (OutputParsingException e) {
      e.printStackTrace();
      return analyseReceiptImage(input);
    }

  }

}
