import React, { useEffect, useState } from 'react';
import { Box } from "@mui/system";

export function InitMap(props: any){
    if (props.venueId == 1){
        return (
                <Box sx={{backgroundImage: `./StarMap.png`}}>
            </Box>
        )
    } else if (props.venueId == 2) {
        return (
            <Box sx={{backgroundImage: `./CapitolMap.png`, width: 400, height: 400} }>
            </Box>
        )
    } else {
        return (
            <Box sx={{backgroundImage: `./StadiumMap.png`}}>
            </Box>
        )
    }
}