import { Outlet } from "react-router";

import Header from "@/components/header";
import { ThemeProvider } from "@/components/theme-provider";
import { Toaster } from "@/components/ui/sonner";

function RoutedLayout() {
  return (
    <>
      <div>Hello</div>
    </>
  );
}

export default function AppShell() {
  return <RoutedLayout />;
}
