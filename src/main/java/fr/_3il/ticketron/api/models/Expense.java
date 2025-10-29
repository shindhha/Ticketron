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

  public String currency = "EUR";
  @Column(name = "category_code")
  public String categoryCode;

  public String description;

  @Column(name = "created_at", updatable = false, insertable = false,
          columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private java.time.LocalDateTime createdAt;

  public java.time.LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public Long getId() {
    return id;
  }
}
