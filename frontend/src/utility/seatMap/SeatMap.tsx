import React, { useEffect, useState } from 'react';
import { ReactComponent as CTSVG } from "./Capitol Theatre.svg";
import {ReactComponent as StarTheatre} from "./The Star Theatre.svg";
import { ReactComponent as SgStadSVG } from "./Singapore National Stadium.svg";
import './MySVGComponent.css';
import { Grid, Typography } from '@mui/material';


export function SGStad(props: any) {
  const [selectedSection, setSelectedSection] = useState('');
  const [pregrandParentG, setPregrandParentG]: any = useState(null);
  // console.log(props.id);

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
          props.setSelectedSection(sectionId.substring(6));
          //cannot use fill, must use filter to high the selected section : go to "https://isotropic.co/tool/hex-color-to-css-filter/" if want change filter color
          grandparentG.style.filter='opacity(60%) brightness(20%) contrast(20%)';
          
        }
      }
    }
  };

  return (
    <Grid style={{ display:'flex', flexDirection:'row'}}>
      {/* <h2>Last Selected Section: {selectedSection}</h2> */}
      {props.id === 1 ?<StarTheatre onClick={handleSectionClick} style={{ width:'650px'}}/> : props.id === 2 ? <CTSVG onClick={handleSectionClick} style={{ width:'650px'}}/> : props.id === 3 ? <SgStadSVG onClick={handleSectionClick} style={{ width:'650px'}}/> : null}
      {/* <SgStadSVG onClick={handleSectionClick}/>
      <CTSVG onClick={handleSectionClick}/> */}
      <div style={{background:'#F8F8F8', height:'110px', width:'300px', borderRadius:'8px', alignContent:'left', display:'flex', flexDirection:'column', marginTop:125, marginLeft:0}}>
        <Typography style={{font:'roboto', fontWeight:500, fontSize:'18px', marginLeft:25, marginTop:18}}>
          You have selected
        </Typography>
        <Typography style={{font:'roboto', fontWeight:500, fontSize:'16px', color:'#888888', marginLeft:25}}>
          {selectedSection}
        </Typography>
      </div>
    </Grid>
  );
}