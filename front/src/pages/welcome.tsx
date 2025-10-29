import { useRef, useState } from "react";
import { InputBar } from "../components";
import { Box } from "@mui/material";

export function Welcome() {
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files) return;
    const files = Array.from(e.target.files);
    setSelectedFiles(files);
  };
  return (
    <Box>
      <input
        ref={fileInputRef}
        type="file"
        multiple
        style={{ display: "none" }}
        onChange={handleFileChange}
      />
      <InputBar
        onFilesSelected={() => fileInputRef.current?.click()}
        onSend={() => alert("Good bye")}
      ></InputBar>
    </Box>
  );
}
// Removed the custom useRef function as it conflicts with React's useRef.
