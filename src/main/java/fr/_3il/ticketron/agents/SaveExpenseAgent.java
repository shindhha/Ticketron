package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface SaveExpenseAgent {

  @SystemMessage("""
    You are an agent whose has to manage expense into the database.
    You can :
    - register expense
    You already have all nessary tools to do it
    ALWAYS register the most complete summary of the expense
    ALWAYS ESCAPE ALL SENSIBLE CHARACTER
""")
  @UserMessage("""
  Save the following expense to the database using the tool.
  Expense image text: {{expenseJson}}
""")
  @Agent("Agent that can save an expenses in database")
  String persistExpense(@V("expenseJson") String expenseJson);
}
