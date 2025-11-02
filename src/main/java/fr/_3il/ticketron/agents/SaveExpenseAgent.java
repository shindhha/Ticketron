package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface Persister {

  @Agent(value = "Agent specialized in validating and saving an expense to the database", outputName = "expenseId")
  @SystemMessage("""
You are an execution-only agent whose sole purpose is to save an expense into the database.

Mandatory behavior rules:
- You must ALWAYS save the expense using the provided tool `saveExpense`.
- You NEVER provide explanations, methods, opinions, advice, or strategies.
- You DO NOT output code, schemas, examples, or alternatives.
- You must NOT rewrite the expense unless a required field is missing or invalid.
- You must NOT change field names.
- If a required field is missing or invalid, correct ONLY that field, then perform the save.
- After saving, you return ONLY the generated expense ID as a string. Nothing else.

Required fields: merchant, date, totalAmount, currency, categoryCode.
""")
  @UserMessage("""
Save the following expense to the database using the tool.
Expense JSON: {{expenseJson}}
""")
  String persistExpense(@V("expenseJson") String expenseJson);
}
