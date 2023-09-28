import { extend } from 'dayjs';
import React, { useEffect, useState } from 'react';
import {ReactComponent as SectionSVG} from "./testSeatMap.svg";
import {ReactComponent as SingStadSVG} from "./svgviewer-outputsingapore stad.svg";


export const SeatMap = () => {
    const [selectedSection, setSelectedSection] = useState('');

    // const handleSectionClick = (event: any) => {
    //     const sectionId = event.target.getAttribute('id');
    //     console.log("section " + sectionId)
    // }
    const handleSectionClick = (event: React.MouseEvent<SVGElement>) => {
        const target = event.target as SVGElement;
        const parentG = target.closest('g'); // Find the nearest parent <g> element
        if (parentG) {
          const grandparentG = parentG.parentNode as SVGGElement; // Get the parent of the parent <g>
          if (grandparentG) {
            const sectionId = grandparentG.getAttribute('id');
            console.log("section " + sectionId);
            if(sectionId!=null){
                setSelectedSection(sectionId.substring(6,));


            }
          }
        }
      };
      

    // const handleSectionMouseOver = (event:any)=>{
    //     console.log("ONMOUSEOVER")
    // }

    // const handleSectionMouseOut = (event:any)=>{
    // }


    return (
        <body>
            <style>
                {`
/* Define hover styles for the SVG elements */
g[id^="field_"]:hover{
    opacity: 80%;
    //   fill: blue; /* Change fill color to blue on hover */
  cursor: pointer; /* Change cursor to pointer on hover */
}
// .pack{
//   border: 2 px
// }

// [sect_id]:hover{
//     opacity: 90%;
//     //   fill: blue; /* Change fill color to blue on hover */
//   cursor: pointer; /* Change cursor to pointer on hover */
// }

`}
            </style>
            {/* <svg width="800" height="600" xmlns="http://www.w3.org/2000/svg" id="seatMap" onClick={handleSectionClick}>
                <g>
                    <title>Layer 1</title>
                    <rect id="1" height="120" width="233" y="106.5" x="47" stroke="#000" fill="red" />
                    <ellipse ry="62.5" rx="127.5" id="2" cy="192" cx="467.5" stroke="#000" fill="#000" />
                    <rect id="3" height="123" width="162" y="354.5" x="95" stroke="#000" fill="green" />
                    <path id="4" d="m359.20003,453.82747l0,0c0,-66.27417 69.39588,-120 155.00001,-120l0,0c41.10854,0 80.53342,12.64281 109.60154,35.14718c29.06815,22.50437 45.39847,53.02683 45.39847,84.85282l0,0c0,66.27416 -69.39588,120 -155.00001,120l0,0c-85.60413,0 -155.00001,-53.72585 -155.00001,-120zm77.50001,0l0,0c0,33.13708 34.69796,60 77.50001,60c42.80208,0 77.50001,-26.86293 77.50001,-60c0,-33.13708 -34.69795,-60 -77.50001,-60l0,0c-42.80205,0 -77.50001,26.86294 -77.50001,60z" stroke="#000" fill="#fff" />
                    <ellipse ry="28.5" rx="91" id="5" cy="56" cx="637" stroke="#000" fill="#fff" />
                </g>
            </svg> */}
{/* <SectionSVG onClick={handleSectionClick}/>      */}
<h2>Last Selected Section: {selectedSection}</h2>
<SingStadSVG onClick={handleSectionClick} width={"50%"}/>       
            </body>
    );
}