import React, { useState } from "react";
import stockData from "./data/supportedStocks.json";

// Define a TypeScript interface for the stock object
interface Stock {
  currency: string;
  description: string;
  displaySymbol: string;
  figi: string | null;
  isin: string | null;
  mic: string;
  shareClassFIGI: string;
  symbol: string;
  symbol2: string;
  type: string;
}

interface SearchBarProps {
  watchlist: string[];
  setWatchlist: React.Dispatch<React.SetStateAction<string[]>>;
}

const StockSearchBar: React.FC<SearchBarProps> = ({ watchlist, setWatchlist }) => {
  const [searchInput, setSearchInput] = useState<string>("");

  const [stocks] = useState<Stock[]>(stockData as Stock[]);

  // Add stock to watchlist
  function addToWatchlist(symbol: string) {
    if (!watchlist.includes(symbol)) {
      setWatchlist([...watchlist, symbol]);
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchInput(e.target.value);
  };

  const filteredStocks = stocks
    .filter((stock) =>
      stock.symbol.toLowerCase().includes(searchInput.toLowerCase())
    )
    .slice(0, 5);

  return (
    <div>
      <input
        type="search"
        placeholder="Search stocks here"
        onChange={handleChange}
        value={searchInput}
      />
      {searchInput.length > 0 && (
        <div className="stock-preview">
          {filteredStocks.length > 0 ? (
            filteredStocks.map((stock, index) => (
              <div key={index} className="stock-item" style={{ marginBottom: "5px" }}>
                <h4
                  style={{
                    fontSize: "0.9rem",
                    fontWeight: "bold",
                    textDecoration: "underline",
                    cursor: "pointer",
                    color: watchlist.includes(stock.symbol) ? "green" : "black",
                  }}
                  onClick={() => addToWatchlist(stock.symbol)}
                >
                  {stock.symbol}
                </h4>
                <p style={{ fontSize: "0.8rem", margin: "0" }}>
                  {stock.description}
                </p>
              </div>
            ))
          ) : (
            <p>No matching stocks found.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default StockSearchBar;
