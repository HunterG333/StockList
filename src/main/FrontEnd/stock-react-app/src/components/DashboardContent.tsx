import React, { useState, useEffect } from "react";
import { LineGraph } from "./graph/Line.tsx";
import WatchlistElement from "./WatchlistElement.tsx";

// Function to fetch market data for a given stock
async function getMarketData(stock: string, size: number) {
    try {
        const response = await fetch(`http://localhost:8080/api/marketdata?stock=${stock}&days=${size}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`Error fetching market data: ${response.statusText}`);
        }

        const data = await response.json();
        return data; // Assuming server returns array of numbers
    } catch (error) {
        console.error('Error fetching market data:', error);
        return null;
    }
}

interface DashboardContentProps {
    watchlist: string[];
    setWatchlist: React.Dispatch<React.SetStateAction<string[]>>;
}

function DashboardContent({ watchlist, setWatchlist }: DashboardContentProps) {
    // State to store market data for predefined stocks (Dow, S&P, Nasdaq)
    const [dowData, setDowData] = useState<number[]>([]);
    const [spData, setSpData] = useState<number[]>([]);
    const [nasData, setNasData] = useState<number[]>([]);
    
    // State to store market data for the stocks in the watchlist
    const [stockDataList, setStockDataList] = useState<{ [key: string]: number[] }>({});

    // Fetch market data for Dow, S&P, and Nasdaq when the component mounts
    useEffect(() => {
        async function fetchData() {
            const dow = await getMarketData("DIA", 5);
            const sp = await getMarketData("SPY", 5);
            const nas = await getMarketData("QQQ", 5);

            if (dow) setDowData(dow);
            if (sp) setSpData(sp);
            if (nas) setNasData(nas);
        }

        fetchData();
    }, []);

    // Fetch market data for each stock in the watchlist
    useEffect(() => {
        async function fetchWatchlistData() {
            const newStockData: { [key: string]: number[] } = {};

            for (const stock of watchlist) {
                const marketData = await getMarketData(stock, 5);
                if (marketData) {
                    newStockData[stock] = marketData; // Store stock data by symbol
                }
            }

            setStockDataList((prevData) => ({ ...prevData, ...newStockData }));
        }

        if (watchlist.length > 0) {
            fetchWatchlistData();
        }
    }, [watchlist]); // Fetch new data when the watchlist changes

    // Function to determine the color based on data
    const getLineColor = (data: number[]) => {
        if (data.length >= 2) {
            const first = data[0];
            const last = data[data.length - 1];
            return last > first ? 'rgb(0, 200, 0)' : 'rgb(200, 0, 0)'; // Green if last > first, Red otherwise
        }
        return 'rgb(128, 128, 128)'; // Default color in case of insufficient data
    };

    return (
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
            {/* Predefined stocks: Dow, S&P, Nasdaq */}
            <div style={{ display: "flex", justifyContent: "space-around", gap: "5%", width: "100%" }}>
                <LineGraph stockLabel="Dow Jones" stockData={dowData} lineColor={getLineColor(dowData)} />
                <LineGraph stockLabel="S&P 500" stockData={spData} lineColor={getLineColor(spData)} />
                <LineGraph stockLabel="Nasdaq" stockData={nasData} lineColor={getLineColor(nasData)} />
            </div>
    
            {/* Add some padding or margin here */}
            <div style={{ marginBottom: "40px" }}></div> {/* Add some space between predefined stocks and watchlist */}
    
            {/* Watchlist stocks: Map through watchlist and render WatchlistElement */}
            <div style={{ marginTop: "20px", width: "100%" }}>
                {watchlist.map((stock) => {
                    const stockData = stockDataList[stock] || []; // Get the data from stockDataList
                    return (
                        <div style={{ marginBottom: "40px" }}> {/* Add margin-bottom for padding between elements */}
                            <WatchlistElement
                                key={stock}
                                stockLabel={stock}
                                stockData={stockData}
                                lineColor={getLineColor(stockData)}
                            />
                        </div>
                    );
                })}
            </div>
        </div>
    );
    
}

export default DashboardContent;
