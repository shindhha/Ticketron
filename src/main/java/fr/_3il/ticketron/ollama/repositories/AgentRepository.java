package fr._3il.ticketron.ollama.repositories;

import fr._3il.ticketron.ollama.models.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<AgentEntity, Integer> {

  @Override
  @Query("select a from AgentEntity a where a.id = ?1")
  Optional<AgentEntity> findById(Integer integer);
}
