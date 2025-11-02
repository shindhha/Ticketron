package fr._3il.ticketron.api.services;

import dev.langchain4j.agent.tool.Tool;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

  private CategoryRepository categoryRepository;
  public CategoryService(@Autowired CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }


  @Tool("Retrieve all existing categories from the database")
  public List<Category> getCategories() {
    return categoryRepository.findAll();
  }

  public boolean saveIfNotExist(Category category) {
    if (!categoryRepository.existsByCode(category.code)) {
      categoryRepository.save(category);
      return true;
    }
    return false;
  }



}
