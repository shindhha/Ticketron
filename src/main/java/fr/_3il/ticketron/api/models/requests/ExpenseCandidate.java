package fr._3il.ticketron.api.models.requests;

import dev.langchain4j.model.output.structured.Description;


@Description("Expense to complete")
public class ExpenseCandidate {
  @Description("Full description of information retrieved from the ticket")
  public String summary;
  public String categoryCode;
  @Description("NAME OF THE CATEGORY")
  public String categoryName;
  @Description("COMPLETE DESCRIPTION OF THE CATEGORY")
  public String categoryDescription;






}
