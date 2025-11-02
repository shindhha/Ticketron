import BasicTable, { Expense } from "../components/BasicTable";

export async function updateExpenses(): Promise<Expense[]> {
  const res = await fetch("http://localhost:8080/api/expenses", {
    method: "GET",
  });
  const data = await res.json();
  return data;
}

export default async function AssistantPage() {
  const rows = await updateExpenses();
  return <BasicTable rows={rows}></BasicTable>;
}
