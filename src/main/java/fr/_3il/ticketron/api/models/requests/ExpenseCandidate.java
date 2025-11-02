package fr._3il.ticketron.api.models.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.output.structured.Description;
import fr._3il.ticketron.exceptions.InvalidExpenseException;

import java.util.Arrays;

@Description("Expense to complete")
public class ExpenseCandidate {
  @Description("Name of the merchant")
  public String merchant;
  @Description("Print date of ticket")
  public String date;
  @Description("Total amount paid")
  public String totalAmount;
  @Description("Currency ")
  public String currency;
  @Description("Full description of information retrieved from the ticket")
  public String description;
  @Description("Print hour of ticket")
  public String hour;
  @Description("SHORT WORD TO IDENTIFY THE CATEGORY")
  public String categoryCode;
  @Description("NAME OF THE CATEGORY")
  public String categoryName;
  @Description("COMPLETE DESCRIPTION OF THE CATEGORY")
  public String categoryDescription;


  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      // Sérialisation JSON pretty-print pour lisibilité (peut être compactée)
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      // En cas d'erreur, on retourne une structure JSON minimale
      return String.format(
              "{\"merchant\":\"%s\",\"date\":\"%s\",\"totalAmount\":\"%s\",\"currency\":\"%s\",\"description\":\"%s\",\"hour\":\"%s\",\"categoryCode\":\"%s\",\"categoryName\":\"%s\",\"categoryDescription\":\"%s\"}",
              safe(merchant), safe(date), safe(totalAmount), safe(currency),
              safe(description), safe(hour), safe(categoryCode),
              safe(categoryName), safe(categoryDescription)
      );
    }
  }

  private String safe(String value) {
    return value == null ? "" : value.replace("\"", "\\\"");
  }

  public String getEmptyField() {
    for (var field : this.getClass().getDeclaredFields()) {
      try {
        String value = (String) field.get(this);
        if (value == null || value.isBlank()) {
          return field.getName();
        }
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return "";
  }




}
