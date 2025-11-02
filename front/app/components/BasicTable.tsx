import * as React from "react";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";

export type Expense = {
  id: number;
  merchant: string | null;
  date: string | null;
  total_amount: number | null;
  currency: string | null;
  categoryCode: string;
  description: string | null;
  created_at: string | null;
};

export default function ExpensesTable({ rows }: { rows: Expense[] }) {
  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 900 }} aria-label="expenses table">
        <TableHead>
          <TableRow>
            <TableCell>ID</TableCell>
            <TableCell>Merchant</TableCell>
            <TableCell>Date</TableCell>
            <TableCell align="right">Total</TableCell>
            <TableCell>Currency</TableCell>
            <TableCell>Category</TableCell>
            <TableCell>Description</TableCell>
            <TableCell>Created At</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <TableRow
              key={row.id}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <TableCell>{row.id}</TableCell>
              <TableCell>{row.merchant || "-"}</TableCell>
              <TableCell>{row.date || "-"}</TableCell>
              <TableCell align="right">
                {row.total_amount?.toFixed(2) ?? "-"}
              </TableCell>
              <TableCell>{row.currency || "-"}</TableCell>
              <TableCell>{row.categoryCode}</TableCell>
              <TableCell>{row.description || "-"}</TableCell>
              <TableCell>{row.created_at || "-"}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
