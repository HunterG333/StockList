import React, { useState, useEffect } from "react";
import { LineGraph } from "./graph/Line.tsx";
import SearchBar from "./searchBar.tsx";

//create a list 
async function getMarketData(stock: string, size: number){
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
        return data;  // Assuming the server returns an array of numbers
    } catch (error) {
        console.error('Error fetching market data:', error);
        return null;
    }
}



function DashboardContent() {
    // State to store market data
    const [dowData, setDowData] = useState<number[]>([]);
    const [spData, setSpData] = useState<number[]>([]);
    const [nasData, setNasData] = useState<number[]>([]);

    // Fetch market data when the component mounts
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
    }, []);  // Change array to depend on dow sp and nas if we migrate to a live socket

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
        <div style={{ display: "flex", justifyContent: "space-around", gap: "5%" }}>
            <LineGraph stockLabel="Dow Jones" stockData={dowData} lineColor={getLineColor(dowData)} />
            <LineGraph stockLabel="S&P 500" stockData={spData} lineColor={getLineColor(spData)} />
            <LineGraph stockLabel="Nasdaq" stockData={nasData} lineColor={getLineColor(nasData)} />
        </div>
    );
}

export default DashboardContent;