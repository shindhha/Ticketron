"use client";
import { Thread } from "@/components/assistant-ui/thread";
import { TicketronRuntimeProvider } from "../api/chat/TicketronRuntimeProvider";
import { SidebarProvider, SidebarInset } from "@/components/ui/sidebar";
export default function AssistantPage() {
  return (
    <TicketronRuntimeProvider>
      <SidebarProvider>
        <div className="flex h-dvh w-full pr-0.5">
          <SidebarInset>
            <div className="flex-1 overflow-hidden"></div> <Thread />
          </SidebarInset>
        </div>
      </SidebarProvider>
    </TicketronRuntimeProvider>
  );
}
