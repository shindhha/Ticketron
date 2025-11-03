package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ImageAnalyserAgent {

  @UserMessage("""
      You receive the path to an image of an expense receipt: {{imgPath}}.
      
      Your task is:
      1. Use the OCR tool to extract the text from the image.
      2. Produce the most complete summary to stock in database
ALWAYS ESCAPE ALL SENSIBLE CHARACTER
""")
  @Agent("Agent that can analyse an image")

  String imageAnalyser(@V("imgPath") String imgPath);


}
