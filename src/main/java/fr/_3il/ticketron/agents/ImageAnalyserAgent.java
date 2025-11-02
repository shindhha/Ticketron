package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ExpenseInterpreter {

  @Agent(value = "Agent that can transform an image to text by using ocr and give the path", outputName = "expenseJson")
  @UserMessage("""
      You receive the path to an image of an expense receipt: {{imgPath}}.
      
      Your task is:
      1. Use the OCR tool to extract the text from the image.
      2. Interpret the extracted text to identify the following fields:
         - merchant
         - date
         - totalAmount
         - currency
         - description (full text or summary)
         - hour (optional, if present)
      3. Produce a JSON object with these exact field names:
      {
        "merchant": "",
        "date": "",
        "totalAmount": "",
        "currency": "",
        "description": "",
        "hour": "",
        "categoryCode": "",
        "categoryName": "",
        "categoryDescription": ""
      }
      
      Rules:
      - DO NOT left any field empty.
      - Do NOT make up values that are not found.
      - Do NOT invent path if not given ask the user
""")
  String imageAnalyser(@V("imgPath") String imgPath);


}
