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

@Entity
@Table(name = "expenses")
public class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public String merchant;

  public LocalDate date;

  @Column(name = "total_amount")
  public BigDecimal totalAmount;

  @Column(name = "vat_amount")
  public BigDecimal vatAmount;

  public String currency = "EUR";
  @Column(name = "category_code")
  public String categoryCode;

  public String description;
  public String paymentMethod;
  public String imagePath;
  public Float confidence;
  public String status = "PENDING";

  @Column(name = "created_at", updatable = false, insertable = false,
          columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private java.time.LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "report_id")
  public ExpenseReport report;

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

    public ExpenseBuilder id(Long id) {
      this.id = id;
      return this;
    }
    @Tool(value = "Definit le nom du commerçant pour la dépense en cours de construction.")
    public ExpenseBuilder merchant(String merchant) {
      this.merchant = merchant;
      return this;
    }
    @Tool(value = "Definit la date de la dépense en cours de construction.")
    public ExpenseBuilder date(LocalDate date) {
      this.date = date;
      return this;
    }

    @Tool(value = "Definit le montant total de la dépense en cours de construction.")
    public ExpenseBuilder totalAmount(BigDecimal totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    @Tool(value = "Definit le montant de la TVA de la dépense en cours de construction.")
    public ExpenseBuilder vatAmount(BigDecimal vatAmount) {
      this.vatAmount = vatAmount;
      return this;
    }

    @Tool(value = "Definit la devise de la dépense en cours de construction.")
    public ExpenseBuilder currency(String currency) {
      this.currency = currency;
      return this;
    }

    @Tool(value = "Definit la catégorie de la dépense en cours de construction.")
    public ExpenseBuilder category(String categoryCode) {
      this.categoryCode = categoryCode;
      return this;
    }

    @Tool(value = "Definit la description de la dépense en cours de construction.")
    public ExpenseBuilder description(String description) {
      this.description = description;
      return this;
    }

    @Tool(value = "Definit le mode de paiement de la dépense en cours de construction.")
    public ExpenseBuilder paymentMethod(String paymentMethod) {
      this.paymentMethod = paymentMethod;
      return this;
    }

    @Tool(value = "Definit le chemin de l'image associée à la dépense en cours de construction.")
    public ExpenseBuilder imagePath(String imagePath) {
      this.imagePath = imagePath;
      return this;
    }

    @Tool(value = "Definit le niveau de confiance de la dépense en cours de construction.")
    public ExpenseBuilder confidence(Float confidence) {
      this.confidence = confidence;
      return this;
    }

    @Tool(value = "Definit le statut de la dépense en cours de construction.")
    public ExpenseBuilder status(String status) {
      this.status = status;
      return this;
    }

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



  public java.time.LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public Long getId() {
    return id;
  }
}
