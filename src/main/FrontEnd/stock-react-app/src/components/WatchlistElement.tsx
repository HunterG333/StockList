import React from "react";
import { LineGraph } from "./graph/Line.tsx"; // Assuming LineGraph is reusable

interface WatchlistElementProps {
    stockLabel: string;
    stockData: number[];
    lineColor: string;
}

function WatchlistElement({ stockLabel, stockData, lineColor }: WatchlistElementProps) {
    return (
        <div style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            padding: "10px",
            border: "1px solid #ddd",
            borderRadius: "8px",
            marginBottom: "10px",
            width: "100%"
        }}>
            {/* Stock Label */}
            <div style={{ flex: 1 }}>
                <h3>{stockLabel}</h3>
            </div>

            {/* Stock Graph */}
            <div style={{ flex: 2 }}>
                {stockData.length > 0 ? (
                    <LineGraph stockLabel={stockLabel} stockData={stockData} lineColor={lineColor} />
                ) : (
                    <p>No data available</p>
                )}
            </div>

            {/* Stock Performance Indicator (color-coded) */}
            <div style={{ flex: 1, textAlign: "right" }}>
                <span style={{ color: lineColor }}>
                    {lineColor === 'rgb(0, 200, 0)' ? 'Up' : 'Down'}
                </span>
            </div>
        </div>
    );
}

export default WatchlistElement;
