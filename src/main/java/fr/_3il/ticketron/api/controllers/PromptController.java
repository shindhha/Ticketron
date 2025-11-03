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

/**
 * Contrôleur REST pour la gestion des requêtes utilisateur vers l'agent Ticketron.
 * Expose un endpoint permettant l'upload d'images de tickets avec des instructions
 * de traitement personnalisées. Gère la réception des fichiers, leur sauvegarde
 * temporaire et la délégation du traitement à l'agent IA.
 */
@RequestMapping("/api")
@RestController
public class PromptController {



  private TicketronAgent ticketron;

  private ExpenseService expenseService;
  /**
   * Constructeur avec injection de l'agent Ticketron.
   * @param expenseService instance du service bd
   * @param ticketron instance de l'agent IA pour le traitement des tickets
   */

  public PromptController(@Autowired TicketronAgent ticketron,
                          @Autowired ExpenseService expenseService) {
    this.ticketron = ticketron;
    this.expenseService = expenseService;
  }
  /**
   * Endpoint POST pour soumettre des images de tickets avec des instructions de traitement.
   * Accepte un ou plusieurs fichiers (images de tickets) accompagnés d'instructions optionnelles.
   * Les fichiers sont sauvegardés temporairement dans le répertoire système temporaire
   * avec un préfixe timestamp pour éviter les collisions de noms.
   *
   * <p>Chaque fichier est ensuite traité par l'agent Ticketron selon les instructions fournies.
   * Le traitement inclut l'OCR, l'extraction des informations et l'enregistrement des dépenses.</p>
   * @param userMessage instructions utilisateur
   * @param file objet contenant les fichiers uploadés et les instructions de traitement
   * @return message de confirmation indiquant que les tickets sont en cours d'analyse
   * @throws IllegalArgumentException si aucun fichier n'est fourni dans la requête
   */
  @PostMapping(
          value = "/chat",
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> handleChat(@RequestPart("userMessage") String userMessage,
                                      @RequestPart(value = "file", required = false) MultipartFile file) {
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
      System.out.println(iaResponse);
      return ResponseEntity.ok(Map.of("text", iaResponse));

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("text", "Erreur serveur : " + e.getMessage()));
    }
  }

  /**
   *
   * @return la liste des expenses avec un code de retour 200
   */
  @GetMapping("/expenses")
  public ResponseEntity<List<Expense>> expenses() {
    return ResponseEntity.ok(expenseService.expenses());
  }
}
