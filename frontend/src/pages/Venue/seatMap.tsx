import React, { useState } from 'react';
import './MySVGComponent.css';

const SeatMap = () => {
    const [selectedRect, setSelectedRect] = useState(null);

    const handleRectClick = (id: any) => {
        if (selectedRect === id) {
            setSelectedRect(null);
            console.log(id)
        } else {
            setSelectedRect(id);
            console.log(id)
        }
    };

    return (
        <div>
            <svg width="800" height="600" xmlns="http://www.w3.org/2000/svg">
                <g>
                    <rect
                        id="svg_3"
                        height="86"
                        width="115"
                        y="97"
                        x="125"
                        stroke="#000"
                        fill="#16e5e5"
                        onClick={() => handleRectClick("svg_3")}
                        className={selectedRect === "svg_3" ? "selected" : ""}
                    />
                    <rect
                        id="svg_4"
                        height="126"
                        width="136"
                        y="208"
                        x="158"
                        stroke="#000"
                        fill="#2b31d8"
                        onClick={() => handleRectClick("svg_4")}
                        className={selectedRect === "svg_4" ? "selected" : ""}
                    />
                    <rect
                        id="svg_5"
                        height="140"
                        width="111"
                        y="120"
                        x="448"
                        stroke="#000"
                        fill="#63ff0f"
                        onClick={() => handleRectClick("svg_5")}
                        className={selectedRect === "svg_5" ? "selected" : ""}
                    />
                    <rect
                        id="svg_6"
                        height="120"
                        width="132"
                        y="304"
                        x="409"
                        stroke="#000"
                        fill="#15c6c6"
                        onClick={() => handleRectClick("svg_6")}
                        className={selectedRect === "svg_6" ? "selected" : ""}
                    />
                </g>
            </svg>

        </div>
    );
};

export default SeatMap;