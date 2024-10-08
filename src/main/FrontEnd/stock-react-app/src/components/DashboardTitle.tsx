import React from "react";
import SearchBar from "./searchBar.tsx";

interface DashboardTitleProps {
  watchlist: string[];
  setWatchlist: React.Dispatch<React.SetStateAction<string[]>>;
}

function DashboardTitle({ watchlist, setWatchlist }: DashboardTitleProps) {
  return (
    <>
      <h1>Dashboard</h1>
      <SearchBar watchlist={watchlist} setWatchlist={setWatchlist} />
    </>
  );
}

export default DashboardTitle;
