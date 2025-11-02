package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import fr._3il.ticketron.api.repositories.ExpenseReportRepository;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des dépenses et des catégories.
 * Fournit des outils exposés à l'agent LLM pour créer, enregistrer et consulter
 * les dépenses et leurs catégories. Agit comme intermédiaire entre l'agent IA
 * et la couche de persistance (repositories).
 */
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseReportRepository expenseReportRepository;
    private final CategoryRepository categoryRepository;
    private final Expense.ExpenseBuilder expenseBuilder;

    /**
     * Constructeur du service avec injection des dépendances.
     *
     * @param er repository pour la gestion des dépenses
     * @param ers repository pour la gestion des rapports de dépenses
     * @param cs repository pour la gestion des catégories
     * @param mapper ObjectMapper pour la sérialisation/désérialisation JSON
     * @param eb builder réutilisable pour construire les objets Expense
     */
    public ExpenseService(@Autowired ExpenseRepository er,
                          @Autowired ExpenseReportRepository ers,
                          @Autowired CategoryRepository cs,
                          @Autowired ObjectMapper mapper,
                          @Autowired Expense.ExpenseBuilder eb) {
        this.expenseRepository = er;
        this.expenseReportRepository = ers;
        this.categoryRepository = cs;
        this.expenseBuilder = eb;
    }

    /**
     * Enregistre une dépense en base de données à partir des informations
     * préalablement configurées dans le builder.
     * Cet outil est utilisé par l'agent LLM après avoir renseigné tous les champs
     * de la dépense via le builder.
     *
     * @return la dépense enregistrée avec son identifiant généré
     */
    @Tool(value = "Enregistre une depense a partir des informations fournies dans le builder.")
    public Expense saveExpense() {
        Expense expense = expenseBuilder.build();
        Expense saved = expenseRepository.save(expense);
        return saved;
    }

    /**
     * Récupère la liste complète de toutes les catégories de dépenses disponibles.
     * Permet à l'agent LLM de consulter les catégories existantes avant de classifier
     * une nouvelle dépense ou d'en créer une nouvelle si nécessaire.
     *
     * @return liste de toutes les catégories enregistrées en base de données
     */
    @Tool(value = "Retourne la liste de toutes les categories de depenses disponibles.")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Crée et enregistre une nouvelle catégorie de dépense.
     * Le code est automatiquement normalisé (majuscules, espaces supprimés).
     * Cet outil permet à l'agent LLM de créer dynamiquement de nouvelles catégories
     * lorsque les catégories existantes ne correspondent pas à une dépense.
     *
     * @param code code unique de la catégorie (4 lettres majuscules recommandées)
     * @param name nom descriptif de la catégorie
     * @param description description détaillée du type de dépenses couvertes
     * @return la catégorie créée et enregistrée avec son identifiant généré
     */
    @Tool(value = "Ajoute une nouvelle categorie de depense avec un nom et une description.")
    public Category addCategory(
            @P("Le code de la catégorie, 4 lettres majuscules.")
            String code,
            String name,
            String description) {
        Category category = new Category();
        category.code = code.trim().toUpperCase();
        category.name = name;
        category.description = description;
        Category saved = categoryRepository.save(category);
        return saved;
    }
}
