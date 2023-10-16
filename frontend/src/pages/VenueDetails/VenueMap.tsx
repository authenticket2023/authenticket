import { Box } from "@mui/system";
import CapitolMap from "./CapitolMap.png";
import StarMap from "./StarMap.png";
import StadiumMap from "./StadiumMap.png";

export function InitMap(props: any) {
  if (props.venueId == 1) {
    return (
      <Box sx={{ width: 400, height: 400 }}>
        <img src={StarMap}></img>
      </Box>
    );
  } else if (props.venueId == 2) {
    return (
      <Box sx={{ width: 400, height: 400 }}>
        <img src={CapitolMap}></img>
      </Box>
    );
  } else {
    return (
      <Box sx={{ width: 400, height: 400 }}>
        <img src={StadiumMap}></img>
      </Box>
    );
  }
}
