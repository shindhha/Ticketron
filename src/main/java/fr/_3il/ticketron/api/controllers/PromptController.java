package fr._3il.ticketron.api.controllers;

import fr._3il.ticketron.agents.TicketronAgent;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class PromptController {


  private TicketronAgent ticketron;

  private ExpenseService expenseService;
  public PromptController(@Autowired TicketronAgent ticketron,
                          @Autowired ExpenseService expenseService) {
    this.ticketron = ticketron;
    this.expenseService = expenseService;
  }
  /**
   * Endpoint unique qui reçoit :
   * - un JSON "messages" (liste des échanges du chat)
   * - un fichier JPG optionnel
   */
  @PostMapping(
          value = "/chat",
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> handleChat(@RequestPart("userMessage") String userMessage,
                                      @RequestPart("file") MultipartFile file) {
    try {
      String prompt = "User asked : " + userMessage;
      if (file != null && !file.isEmpty()) {
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
        Files.createDirectories(uploadDir);

        String filename = Paths.get(file.getOriginalFilename()).getFileName().toString();
        Path destination = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        prompt += " image is located at : " + destination.toString();
      }


      String iaResponse = ticketron.analyseReceiptImage(prompt);

      return ResponseEntity.ok(Map.of("text", iaResponse));

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("text", "Erreur serveur : " + e.getMessage()));
    }
  }

  @GetMapping("/expenses")
  public ResponseEntity<List<Expense>> expenses() {
    return ResponseEntity.ok(expenseService.expenses());
  }
}
