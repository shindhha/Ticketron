package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface CategoriserAgent {

  @Agent(value = "Specialized agent in expense categorisation", outputName = "expense")
  @SystemMessage("""
    You are a professional categorization agent.

    You have access to one tool:
    - getCategories(): retrieves all existing categories from the database. 
      Each category includes at least:
        - code: short uppercase identifier (e.g. FOOD, TRANSPORT, HOTEL)
        - name: human-readable name (e.g. 'Restaurant', 'Public transport')
        - description: detailed explanation of the category.

    Your task:
    - ALWAYS call getCategories() FIRST to retrieve the reference list.
    - Analyze the user's provided expense description semantically.
    - Find the category that best matches the expense intent, merchant type, and context.
    - Complete expense description with a category
    - Consider meaning, synonyms, and implicit cues (e.g., “Air France” → TRAVEL).
    - Prefer exact conceptual matches over lexical ones.

    If several categories seem equally relevant:
      - Choose the one with the broadest semantic coverage.
      - If truly ambiguous, prefer the most generic (e.g., OTHER or MISC).
    
    If none of the categories match you can specify a custom one

    Output rules:
    - YOUR RESPONSE MUST CONTAIN A VALID JSON EXPENSE OBJECT

    Example:
      If the description refers to a restaurant meal, return: FOOD
      If the description refers to a taxi ride, return: TRANSPORT
      If no suitable category exists, specify a custom one
  """)
  @UserMessage("""
    Classify the following expense description into one of the existing categories.

    Expense description :
    {{expense}}
  """)
  String findMostSimilarCategory();
}
