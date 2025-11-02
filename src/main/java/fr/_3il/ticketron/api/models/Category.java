package fr._3il.ticketron.api.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Entité JPA représentant une catégorie de dépense.
 * Permet de classifier les dépenses par type (restaurant, transport, hébergement, etc.).
 * Chaque catégorie possède un code unique, un nom descriptif et une description détaillée.
 *
 * <p>Les catégories sont créées soit manuellement, soit automatiquement par l'agent Ticketron
 * lorsqu'il détecte qu'aucune catégorie existante ne correspond à une dépense.</p>
 */
@Entity
@Table(name = "categories")
public class Category {
    /**
     * Identifiant unique de la catégorie, généré automatiquement.
     */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
    /**
     * Code unique de la catégorie.
     * Généralement composé de 4 lettres majuscules (ex: REST, TRAN, HEBE).
     * Ce champ est obligatoire et doit être unique en base de données.
     */

  @Column(nullable = false, unique = true, length = 100)
  public String code;
    /**
     * Nom descriptif de la catégorie.
     * Exemple : "Restaurant", "Transport", "Hébergement".
     */
  public String name;
    /**
     * Description détaillée du type de dépenses couvertes par cette catégorie.
     * Aide l'agent et les utilisateurs à comprendre quand utiliser cette catégorie.
     */

  public String description;
    /**
     * Récupère l'identifiant unique de la catégorie.
     *
     * @return l'identifiant de la catégorie
     */
  public Long getId() {
    return id;
  }


}
