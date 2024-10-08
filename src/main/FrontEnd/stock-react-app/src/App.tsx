import React, { useState } from "react";
import DashboardContent from "./components/DashboardContent.tsx";
import DashboardTitle from "./components/DashboardTitle.tsx";

function App() {

  // State to track the list of added stocks
  const [watchlist, setWatchlist] = useState<string[]>([]);

  return (
    <div className="d-flex flex-column min-vh-100">
      <header className="bg-light py-3 text-center">
        <DashboardTitle watchlist={watchlist} setWatchlist={setWatchlist} />
      </header>
      <main className="d-flex flex-grow-1 justify-content-center align-items-center">
        <DashboardContent watchlist={watchlist} setWatchlist={setWatchlist}/>
      </main>
    </div>
  );
}

export default App;
