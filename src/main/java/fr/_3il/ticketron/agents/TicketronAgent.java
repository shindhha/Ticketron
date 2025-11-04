package fr._3il.ticketron.agents;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.agent.ErrorRecoveryResult;
import dev.langchain4j.agentic.agent.MissingArgumentException;
import dev.langchain4j.agentic.supervisor.SupervisorContextStrategy;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.output.OutputParsingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketronAgent {

  private static final String SUPERVISOR_CONTEXT = """
      You are an orchestrator. Decide whether to call a tool or to finish.
      
      DECISION RULES (strict):
      1) If the user message is small talk / greeting / unrelated to expenses, RETURN:
         {"agentName":"done","arguments":{"response":"<brief reply in user's language>"}}
      2) Do NOT call imageAnalyser$2 if 'imgPath' is null/empty or not a readable local file.
      3) If any tool returns an error, STOP and return 'done'.
      4) Do NOT call persistExpense$3 when the user asked to VIEW, SHOW, DISPLAY or GET existing expenses.
         persistExpense$3 must only be used when the user explicitly asks to SAVE, REGISTER or ADD a new expense.
      5) When the user asks about existing receipts (e.g., "montre-moi", "affiche", "donne-moi", "quel est"),
         call getExpenses$4 and then RETURN done.
      6) Always answer in the user's language.
      7) Never re-save an expense that was just retrieved reading must not trigger writing.
      
      Output ONLY JSON: {"agentName":"...","arguments":{...}}.
      """;


  private final SupervisorAgent agent;

  public TicketronAgent(@Autowired ChatModel model,
                        @Autowired CategoriserAgent cat,
                        @Autowired ImageAnalyserAgent exp,
                        @Autowired SaveExpenseAgent per,
                        @Autowired GetExpenseAgent getter) {
    this.agent = AgenticServices.supervisorBuilder(SupervisorAgent.class)
            .chatModel(model)
            .subAgents(exp, cat, per, getter)
            .contextGenerationStrategy(SupervisorContextStrategy.CHAT_MEMORY_AND_SUMMARIZATION)
            .responseStrategy(SupervisorResponseStrategy.SCORED)
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

  public String analyseReceiptImage(String input) {
    try {
      return agent.invoke(input, SUPERVISOR_CONTEXT,1);
    } catch (OutputParsingException e) {
      e.printStackTrace();
      return analyseReceiptImage(input);
    }
  }
}
