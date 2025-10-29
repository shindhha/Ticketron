import { IconButton, InputBase, Paper } from "@mui/material";
import SendIcon from "@mui/icons-material/Send";
import AddIcon from "@mui/icons-material/Add";
import { useState, useRef } from "react";
interface InputBarProps {
  onFilesSelected?: () => void;
  onSend?: () => void;
}
export function InputBar({ onFilesSelected, onSend }: InputBarProps) {
  return (
    <Paper
      component="form"
      sx={{ p: "2px 4px", display: "flex", alignItems: "center", width: 400 }}
    >
      <IconButton
        onClick={onFilesSelected}
        sx={{ p: "10px" }}
        aria-label="menu"
      >
        <AddIcon />
      </IconButton>
      <InputBase
        sx={{ ml: 1, flex: 1 }}
        placeholder="Put your instructions here..."
        inputProps={{ "aria-label": "Put your instructions here..." }}
      />

      <IconButton onClick={onSend} sx={{ p: "10px" }} aria-label="directions">
        <SendIcon />
      </IconButton>
    </Paper>
  );
}
