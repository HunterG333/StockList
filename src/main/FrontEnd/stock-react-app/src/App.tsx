import React from "react";
import DashboardContent from "./components/DashboardContent.tsx";
import DashboardTitle from "./components/DashboardTitle.tsx";

function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <header className="bg-light py-3 text-center">
        <DashboardTitle />
      </header>
      <main className="d-flex flex-grow-1 justify-content-center align-items-center">
        <DashboardContent />
      </main>
    </div>
  );
}

export default App;
