package fr._3il.ticketron.api.repositories;

import fr._3il.ticketron.api.models.ExpenseReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseReportRepository extends JpaRepository<ExpenseReport, Long> {
}
