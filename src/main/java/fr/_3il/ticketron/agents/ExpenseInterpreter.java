package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.FlexibleExpense;

public interface ExpenseInterpreter {

  @UserMessage("""
    Analyse ce texte extrait d'un ticket grace à un OCR et renvoie un obje JSON Expense complet.
    Remplit bien tous les champs possibles (merchant, date, totalAmount, vatAmount, currency, description).
    Voici le texte extrait : {{ocrText}}
    
    Respond only with valid JSON. Do not write an introduction or summary.
  """)
  @Agent
  FlexibleExpense extractExpense(@V("ocrText") String ocrText);


}
