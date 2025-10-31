package fr._3il.ticketron.api.models;

import dev.langchain4j.agent.tool.Tool;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
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
     * Nom du commerçant ou de l'établissement où la dépense a été effectuée.
     */
    public String merchant;

    /**
     * Date de la transaction.
     */
    public LocalDate date;

    /**
     * Montant total TTC de la dépense.
     */
    @Column(name = "total_amount")
    public BigDecimal totalAmount;

    /**
     * Montant de la TVA.
     */
    @Column(name = "vat_amount")
    public BigDecimal vatAmount;

    /**
     * Code de la devise (par défaut EUR).
     */
    public String currency = "EUR";

    /**
     * Code de la catégorie associée à cette dépense.
     * Référence le code d'une catégorie existante en base.
     */
    @Column(name = "category_code")
    public String categoryCode;

    /**
     * Description ou notes supplémentaires sur la dépense.
     */
    public String description;

    /**
     * Méthode de paiement utilisée (carte bancaire, espèces, etc.).
     */
    public String paymentMethod;

    /**
     * Chemin vers l'image du ticket original sur le système de fichiers.
     */
    public String imagePath;

    /**
     * Niveau de confiance de l'extraction OCR (0.0 à 1.0).
     * Indique la fiabilité des informations extraites.
     */
    public Float confidence;

    /**
     * Statut de validation de la dépense (PENDING, APPROVED, REJECTED).
     */
    public String status = "PENDING";

    /**
     * Date et heure de création de l'enregistrement en base de données.
     * Valeur générée automatiquement par la base.
     */
    @Column(name = "created_at", updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.time.LocalDateTime createdAt;

    /**
     * Rapport de dépenses auquel cette dépense est rattachée.
     */
    @ManyToOne
    @JoinColumn(name = "report_id")
    public ExpenseReport report;

    /**
     * Builder pour construire progressivement des objets Expense.
     * Exposé comme service Spring et utilisé par l'agent Ticketron via ses outils.
     * Permet à l'agent de définir chaque champ de la dépense un par un avant
     * de construire l'objet final et de l'enregistrer.
     */
    @Service
    public static class ExpenseBuilder {

        private ExpenseBuilder() {}
        private Long id;
        private String merchant;
        private LocalDate date;
        private BigDecimal totalAmount;
        private BigDecimal vatAmount;
        private String currency = "EUR";
        private String categoryCode;
        private String description;
        private String paymentMethod;
        private String imagePath;
        private Float confidence;
        private String status = "PENDING";

        /**
         * Définit l'identifiant de la dépense (usage rare, généralement auto-généré).
         *
         * @param id identifiant de la dépense
         * @return le builder pour chaînage
         */
        public ExpenseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Définit le nom du commerçant pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param merchant nom du commerçant ou établissement
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit le nom du commerçant pour la dépense en cours de construction.")
        public ExpenseBuilder merchant(String merchant) {
            this.merchant = merchant;
            return this;
        }

        /**
         * Définit la date de la transaction pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param date date de la transaction
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit la date de la dépense en cours de construction.")
        public ExpenseBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        /**
         * Définit le montant total TTC pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param totalAmount montant total de la dépense
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit le montant total de la dépense en cours de construction.")
        public ExpenseBuilder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        /**
         * Définit le montant de la TVA pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param vatAmount montant de la TVA
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit le montant de la TVA de la dépense en cours de construction.")
        public ExpenseBuilder vatAmount(BigDecimal vatAmount) {
            this.vatAmount = vatAmount;
            return this;
        }

        /**
         * Définit la devise pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param currency code de la devise (ex: EUR, USD, GBP)
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit la devise de la dépense en cours de construction.")
        public ExpenseBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Définit le code de la catégorie pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param categoryCode code de la catégorie (doit exister en base)
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit la catégorie de la dépense en cours de construction.")
        public ExpenseBuilder category(String categoryCode) {
            this.categoryCode = categoryCode;
            return this;
        }

        /**
         * Définit la description ou notes pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param description description textuelle de la dépense
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit la description de la dépense en cours de construction.")
        public ExpenseBuilder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Définit le mode de paiement pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param paymentMethod méthode de paiement utilisée
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit le mode de paiement de la dépense en cours de construction.")
        public ExpenseBuilder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        /**
         * Définit le chemin de l'image du ticket pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param imagePath chemin vers l'image du ticket
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit le chemin de l'image associée à la dépense en cours de construction.")
        public ExpenseBuilder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        /**
         * Définit le niveau de confiance de l'extraction OCR pour la dépense.
         * Outil exposé à l'agent LLM.
         *
         * @param confidence score de confiance entre 0.0 et 1.0
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit le niveau de confiance de la dépense en cours de construction.")
        public ExpenseBuilder confidence(Float confidence) {
            this.confidence = confidence;
            return this;
        }

        /**
         * Définit le statut de validation pour la dépense en cours de construction.
         * Outil exposé à l'agent LLM.
         *
         * @param status statut de la dépense (PENDING, APPROVED, REJECTED)
         * @return le builder pour chaînage
         */
        @Tool(value = "Definit le statut de la dépense en cours de construction.")
        public ExpenseBuilder status(String status) {
            this.status = status;
            return this;
        }

        /**
         * Construit l'objet Expense final à partir des valeurs définies.
         * Cette méthode est appelée après que tous les champs ont été configurés.
         *
         * @return nouvelle instance d'Expense avec tous les champs renseignés
         */
        public Expense build() {
            Expense expense = new Expense();
            expense.id = this.id;
            expense.merchant = this.merchant;
            expense.date = this.date;
            expense.totalAmount = this.totalAmount;
            expense.vatAmount = this.vatAmount;
            expense.currency = this.currency;
            expense.categoryCode = this.categoryCode;
            expense.description = this.description;
            expense.paymentMethod = this.paymentMethod;
            expense.imagePath = this.imagePath;
            expense.confidence = this.confidence;
            expense.status = this.status;
            return expense;
        }

        /**
         * Réinitialise tous les champs du builder aux valeurs par défaut.
         * Permet de réutiliser le même builder pour construire plusieurs dépenses
         * sans contamination entre les objets.
         *
         * @return le builder réinitialisé pour chaînage
         */
        public ExpenseBuilder reset() {
            this.id = null;
            this.merchant = null;
            this.date = null;
            this.totalAmount = null;
            this.vatAmount = null;
            this.currency = "EUR";
            this.categoryCode = null;
            this.description = null;
            this.paymentMethod = null;
            this.imagePath = null;
            this.confidence = null;
            this.status = "PENDING";
            return this;
        }
    }

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
