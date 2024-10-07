import React, { useState } from 'react';
import stockData from './data/supportedStocks.json';

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

const StockSearchBar = () => {
  const [searchInput, setSearchInput] = useState<string>("");

  // Explicitly cast the imported JSON data as Stock[]
  const [stocks] = useState<Stock[]>(stockData as Stock[]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchInput(e.target.value);
  };

  // Filter stocks and limit to top 5
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

      {/* Only display results if search input has characters */}
      {searchInput.length > 0 && (
        <div className="stock-preview">
          {filteredStocks.length > 0 ? (
            filteredStocks.map((stock, index) => (
              <div key={index} className="stock-item" style={{ marginBottom: '5px' }}>
                <h4 style={{ fontSize: '0.9rem', fontWeight: 'bold' }}>{stock.symbol}</h4>
                <p style={{ fontSize: '0.8rem', margin: '0' }}>{stock.description}</p>
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
