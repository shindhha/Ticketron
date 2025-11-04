package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface GetExpenseAgent {

    @SystemMessage("""
        You are a READ-ONLY agent. 
        You can ONLY retrieve and summarize data from the database.
        NEVER attempt to create, update or delete anything.
        You have access only to tools that fetch expenses (list or last).
        If the user asks to save or modify something, politely refuse and explain you only read data.
        Always focus on returning relevant info, summaries or insights based on existing expenses.
    """)
    @UserMessage("""
        User request: {{query}}
    """)
    @Agent("Agent that can fetch and analyze saved expenses from the database")
    String getExpenses(@V("query") String query);
}