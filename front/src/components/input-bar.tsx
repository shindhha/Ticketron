import { IconButton, InputBase, Paper } from "@mui/material";
import SendIcon from "@mui/icons-material/Send";
import AddIcon from "@mui/icons-material/Add";
interface InputBarProps {
  addHandler?: () => void;
  sendHandler?: () => void;
}
export function InputBar({ addHandler, sendHandler }: InputBarProps) {
  return (
    <Paper
      component="form"
      sx={{ p: "2px 4px", display: "flex", alignItems: "center", width: 400 }}
    >
      <IconButton onClick={addHandler} sx={{ p: "10px" }} aria-label="menu">
        <AddIcon />
      </IconButton>
      <InputBase
        sx={{ ml: 1, flex: 1 }}
        placeholder="Put your instructions here..."
        inputProps={{ "aria-label": "Put your instructions here..." }}
      />

      <IconButton
        onClick={sendHandler}
        sx={{ p: "10px" }}
        aria-label="directions"
      >
        <SendIcon />
      </IconButton>
    </Paper>
  );
}
