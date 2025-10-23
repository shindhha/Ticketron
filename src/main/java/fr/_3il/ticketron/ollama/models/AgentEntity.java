package fr._3il.ticketron.ollama.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Agent")
public class AgentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  public String name;
  @Lob
  public String prompt;


}
