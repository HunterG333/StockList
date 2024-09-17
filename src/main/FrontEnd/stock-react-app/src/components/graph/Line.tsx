import React from "react";
import {Line} from 'react-chartjs-2'
import {Chart as ChartJS, 
    CategoryScale, 
    LinearScale, 
    PointElement, 
    LineElement,
    Title,
    Tooltip,
    Legend,
} from "chart.js"

ChartJS.register(CategoryScale, 
    LinearScale, 
    PointElement, 
    LineElement,
    Title,
    Tooltip,
    Legend
);

function generateTradingDates(numDays: number): string[]{
    const today = new Date();
    let tradingDays: string[] = [];

    while (tradingDays.length < numDays) {
        const dayOfWeek = today.getDay(); // Sunday = 0, Monday = 1, ... Saturday = 6
        
        if (dayOfWeek > 0 && dayOfWeek < 6) {
            tradingDays.push(today.toLocaleDateString('en-US', {
                month: 'numeric', day: 'numeric'
            }));
        }
        
        today.setDate(today.getDate() - 1);
    }

    return tradingDays.reverse();

}

export const LineGraph = ({ stockLabel, stockData, lineColor }) => {

    // Get the last element (most recent stock price)
    const lastPrice = stockData[stockData.length - 1];

    const lineChartData = {
        labels: 
            generateTradingDates(5),
        datasets: [
            {
                label: stockLabel, // Use the label from props
                data: stockData,   // Use the stock data from props
                borderColor: lineColor, // Use the line color from props
            },
        ],
    };

    const options = {
        maintainAspectRatio: false,
        responsive: true,
    };

    return (
        <div style={{ width: "100%", height: "450px", maxWidth: "33vw" }}>
            <h2 style={{ textAlign: "center", color: lineColor }}>{lastPrice}</h2>
            <Line options={options} data={lineChartData} />
        </div>
    );
};


