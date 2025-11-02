package fr._3il.ticketron.api.services;

import dev.langchain4j.agent.tool.Tool;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.ExpenseCandidate;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final CategoryService categoryService;

  private final ExpenseParser expenseCandidateParser;



  public ExpenseService(@Autowired ExpenseRepository er,
                        @Autowired CategoryService cs,
                        @Autowired ExpenseParser ecp) {
    this.expenseRepository = er;
    this.categoryService = cs;
    this.expenseCandidateParser = ecp;
  }

  @Tool("""
    Save the expense in database.
    
    Usage:
    - Provide a valid JSON string representing the expense in the parameter `expenseToSave`.
    - The JSON MUST contain: merchant, date, totalAmount, currency, categoryCode.
    - Optional fields will be stored if present.
    
    Behavior:
    - Parses the JSON into a ExpenseCandidate.
    - Persists it to the database (creating category if needed).
    - Returns the persisted Expense object, including its generated ID.
""")
  public Expense saveExpense(String expenseToSave) {
    ExpenseCandidate ec = expenseCandidateParser.fromJson(expenseToSave);
    Expense expense = expenseCandidateParser.toExpense(ec);
    Category category = new Category();
    category.code = ec.categoryCode;
    category.name = ec.categoryName;
    category.description = ec.categoryDescription;
    categoryService.saveIfNotExist(category);
    System.out.println("Trying to save with code : " + expense.categoryCode);
    Expense saved = expenseRepository.save(expense);
    return saved;
  }


  public List<Expense> expenses() {
    return expenseRepository.findAll();
  }






}
