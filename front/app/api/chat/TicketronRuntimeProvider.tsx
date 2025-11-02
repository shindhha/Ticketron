"use client";

import type { ReactNode } from "react";
import {
  AssistantRuntimeProvider,
  SimpleImageAttachmentAdapter,
  useLocalRuntime,
  type ChatModelAdapter,
} from "@assistant-ui/react";

const TicketronAdapter: ChatModelAdapter = {
  async run({ messages, abortSignal }) {
    const last = messages[messages.length - 1];
    const attachment = last?.attachments?.[0];
    const form = new FormData();

    const attach = attachment?.content;
    if (attach && attach![0].image) {
      const image = await dataUrlToFile(attach![0].image, "test");
      form.append("file", image, image.name);
    }
    const text = last.content
      .filter((c) => c.type === "text")
      .map((c) => c.text)
      .join("\n");
    form.append("userMessage", text);
    const res = await fetch("http://localhost:8080/api/chat", {
      method: "POST",
      body: form,
    });
    const data = await res.json();
    return {
      content: [
        {
          type: "text",
          text: data.text ?? "⚠️ Impossible de convertir l’image en fichier.",
        },
      ],
    };
  },
};

async function dataUrlToFile(dataUrl: string, filename: string): Promise<File> {
  const response = await fetch(dataUrl);
  const blob = await response.blob();
  const ext = blob.type.split("/")[1];
  return new File([blob], `${filename}.${ext}`, { type: blob.type });
}

export function TicketronRuntimeProvider({
  children,
}: Readonly<{
  children: ReactNode;
}>) {
  const runtime = useLocalRuntime(TicketronAdapter, {
    adapters: { attachments: new SimpleImageAttachmentAdapter() },
  });

  return (
    <AssistantRuntimeProvider runtime={runtime}>
      {children}
    </AssistantRuntimeProvider>
  );
}
