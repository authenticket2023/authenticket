import { Box } from "@mui/system";
import CapitolMap from "./CapitolMap.png";
import StarMap from "./StarMap.png";
import StadiumMap from "./StadiumMap.png";

export function InitMap(props: any) {
  if (props.venueId == 1) {
    return (
      <Box>
        <img src={StarMap} height={600}></img>
      </Box>
    );
  } else if (props.venueId == 2) {
    return (
      <Box>
        <img src={CapitolMap} height={600}></img>
      </Box>
    );
  } else {
    return (
      <Box>
        <img src={StadiumMap} height={600}></img>
      </Box>
    );
  }
}
