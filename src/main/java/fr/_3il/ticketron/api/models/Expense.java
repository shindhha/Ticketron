package fr._3il.ticketron.api.models;

import dev.langchain4j.agent.tool.Tool;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entité JPA représentant une dépense extraite d'un ticket de caisse.
 * Contient toutes les informations pertinentes d'une dépense : montant, commerçant,
 * date, TVA, catégorie, méthode de paiement, etc.
 *
 * <p>Les dépenses sont créées par l'agent Ticketron via le pattern Builder,
 * qui permet de construire progressivement les objets Expense en utilisant
 * les outils exposés à l'agent LLM.</p>
 */
@Entity
@Table(name = "expenses")
public class Expense {

  /**
   * Identifiant unique de la dépense, généré automatiquement.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
  * Code de la catégorie associée à cette dépense.
  * Référence le code d'une catégorie existante en base.
  */
  @Column(name = "category_code" , columnDefinition = "VARCHAR(100)", unique = true, nullable = false)
  public String categoryCode;

  /**
   * Description ou notes supplémentaires sur la dépense.
   */
  @Lob
  public String summary;
  /**
   * Date et heure de création de l'enregistrement en base de données.
   * Valeur générée automatiquement par la base.
   */
  @Column(name = "created_at", updatable = false, insertable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private java.time.LocalDateTime createdAt;
  /**
   * Récupère la date et heure de création de l'enregistrement.
   *
   * @return date et heure de création
   */
  public java.time.LocalDateTime getCreatedAt() {
  return createdAt;
  }
  /**
   * Récupère l'identifiant unique de la dépense.
   *
   * @return l'identifiant de la dépense
   */
  public Long getId() {
  return id;
  }
}
