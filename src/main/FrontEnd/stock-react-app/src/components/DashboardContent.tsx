import React, { useState, useEffect } from "react";
import { LineGraph } from "./graph/Line.tsx";

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
    }, []);  // Empty dependency array means this runs once when the component mounts

    return (
        <div style={{ display: "flex", justifyContent: "space-around", gap: "5%" }}>
            <LineGraph stockLabel="Dow Jones" stockData={dowData} lineColor="rgb(75, 192, 192)" />
            <LineGraph stockLabel="S&P 500" stockData={spData} lineColor="rgb(255, 99, 132)" />
            <LineGraph stockLabel="Nasdaq" stockData={nasData} lineColor="rgb(54, 162, 235)" />
        </div>
    );
}

export default DashboardContent;