package fr._3il.ticketron.api.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor @AllArgsConstructor @Builder
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

  @ManyToOne
  @JoinColumn(name = "category_id")
  public Category category;

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

  private static ExpenseBuilder builder = null;

  public static class ExpenseBuilder {

    private ExpenseBuilder() {}
    private Long id;
    private String merchant;
    private LocalDate date;
    private BigDecimal totalAmount;
    private BigDecimal vatAmount;
    private String currency = "EUR";
    private Category category;
    private String description;
    private String paymentMethod;
    private String imagePath;
    private Float confidence;
    private String status = "PENDING";

    public ExpenseBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public ExpenseBuilder merchant(String merchant) {
      this.merchant = merchant;
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
      expense.category = this.category;
      expense.description = this.description;
      expense.paymentMethod = this.paymentMethod;
      expense.imagePath = this.imagePath;
      expense.confidence = this.confidence;
      expense.status = this.status;
      return expense;
    }

  }

  public static ExpenseBuilder builder() {
    if (builder == null) {
      builder = new ExpenseBuilder();
    }
    return builder;
  }

  public java.time.LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public Long getId() {
    return id;
  }
}
