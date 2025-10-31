package fr._3il.ticketron.api.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;


import java.util.List;

/**
 * Entité JPA représentant un rapport de notes de frais.
 * Un rapport regroupe plusieurs dépenses associées à un employé pour une période
 * ou un projet donné. Il permet de soumettre en une fois plusieurs dépenses pour
 * validation et remboursement.
 *
 * <p>Les rapports suivent un cycle de vie avec différents statuts :
 * DRAFT (brouillon), SUBMITTED (soumis), APPROVED (approuvé), REJECTED (rejeté).</p>
 */
@Entity
@Table(name = "expense_reports")
public class ExpenseReport {

    /**
     * Identifiant unique du rapport de dépenses, généré automatiquement.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titre descriptif du rapport de dépenses.
     * Exemple : "Déplacement Paris - Janvier 2025", "Conférence TechCon 2025".
     */
    public String title;

    /**
     * Identifiant de l'employé propriétaire du rapport.
     */
    public Long employeeId;

    /**
     * Statut actuel du rapport dans son cycle de vie.
     * Valeurs possibles : DRAFT, SUBMITTED, APPROVED, REJECTED.
     * Par défaut : DRAFT.
     */
    public String status = "DRAFT";

    /**
     * Liste des dépenses rattachées à ce rapport.
     * La relation en cascade permet de propager les opérations (suppression, etc.)
     * aux dépenses associées.
     */
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    public List<Expense> expenses;

    /**
     * Date et heure de création du rapport en base de données.
     * Valeur générée automatiquement par la base.
     */
    @Column(name = "created_at", updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.time.LocalDateTime createdAt;

    /**
     * Récupère la date et heure de création du rapport.
     *
     * @return date et heure de création
     */
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Récupère l'identifiant unique du rapport.
     *
     * @return l'identifiant du rapport
     */
    public Long getId() {
        return id;
    }
}
