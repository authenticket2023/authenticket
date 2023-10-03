import React, { useEffect, useState } from 'react';
import { ReactComponent as CTSVG } from "./Capitol Theatre.svg";
import {ReactComponent as StarTheatre} from "./The Star Theatre.svg";
import { ReactComponent as SgStadSVG } from "./Singapore National Stadium.svg";
import './MySVGComponent.css';
import { Grid } from '@mui/material';

export function SGStad() {
  const [selectedSection, setSelectedSection] = useState('');
  const [pregrandParentG, setPregrandParentG]: any = useState(null);

  const handleSectionClick = (event: React.MouseEvent<SVGElement>) => {
    //remove the previous grandParentG filter class
    if (pregrandParentG != null) {
      pregrandParentG.style.filter='';
    }
    const target = event.target as SVGElement;
    const parentG = target.closest('g'); // Find the nearest parent <g> element
    //update to current parentG
    if (parentG) {
      const grandparentG = parentG.parentNode as SVGGElement; // Get the parent of the parent <g>
      setPregrandParentG(grandparentG);
      if (grandparentG) {
        const sectionId = grandparentG.getAttribute('id');
        if (sectionId != null) {
          setSelectedSection(sectionId.substring(6));
          //cannot use fill, must use filter to high the selected section : go to "https://isotropic.co/tool/hex-color-to-css-filter/" if want change filter color
          grandparentG.style.filter='opacity(60%) brightness(20%) contrast(20%)';
          
        }
      }
    }
  };

  return (
    <Grid>
      <h2>Last Selected Section: {selectedSection}</h2>
      <SgStadSVG onClick={handleSectionClick}/>
      <CTSVG onClick={handleSectionClick}/>
      <StarTheatre onClick={handleSectionClick}/>
    </Grid>
  );
}