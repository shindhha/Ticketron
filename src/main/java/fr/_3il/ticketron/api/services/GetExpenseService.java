package fr._3il.ticketron.api.services;

import dev.langchain4j.agent.tool.Tool;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import dev.langchain4j.agent.tool.Tool;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.ExpenseCandidate;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.List;
@Service
public class GetExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;

    private final ExpenseParser expenseCandidateParser;
    public GetExpenseService(@Autowired ExpenseRepository er,
                          @Autowired CategoryService cs,
                          @Autowired ExpenseParser ecp) {
        this.expenseRepository = er;
        this.categoryService = cs;
        this.expenseCandidateParser = ecp;
    }
    @Tool("""
    Retrieve all saved expenses from the database.
    You can use this to analyze or summarize receipts that were previously saved.
    Each expense contains its category code and summary.
""")
    public List<Expense> expenses() {
        return expenseRepository.findAll();
    }
}
