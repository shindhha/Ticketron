package fr._3il.ticketron.api.controllers;

import fr._3il.ticketron.Ticketron;
import fr._3il.ticketron.ollama.services.OllamaInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class PromptController {

  private OllamaInvoiceService olis;

  private Ticketron ticketron;
  public PromptController(@Autowired OllamaInvoiceService olis, @Autowired Ticketron ticketron) {
    this.olis = olis;
    this.ticketron = ticketron;
  }
  @PostMapping("/chat")
  public String processPrompt(@ModelAttribute Prompt prompt) throws IOException {
    if (prompt.files == null || prompt.files.length == 0) {
      throw new IllegalArgumentException("Aucun fichier envoyé.");
    }

    Path uploadDir = Paths.get(System.getProperty("java.io.tmpdir"), "ticketron_uploads");
    Files.createDirectories(uploadDir);

    for (var file : prompt.files) {
      String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
      Path filePath = uploadDir.resolve(fileName);
      file.transferTo(filePath.toFile());

      ticketron.processReceipt(filePath.toString(), prompt.instructions);
    }
    return "Tickets reçus et en cours d'analyse.";
  }
}
