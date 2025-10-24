package fr._3il.ticketron.api.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Entity
@Table(name = "expense_reports")
public class ExpenseReport {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public String title;
  public Long employeeId;
  public String status = "DRAFT";

  @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
  public List<Expense> expenses;

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
