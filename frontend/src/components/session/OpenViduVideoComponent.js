import { useRef, useEffect } from "react";
import classes from "./OpenViduVideoComponent.module.css";

const OpenViduVideoComponent = (props) => {
  const videoRef = useRef();

  useEffect(() => {
    if (props.streamManager && !!videoRef) {
      props.streamManager.addVideoElement(videoRef.current);
    }
  }, [props.streamManager]);

  return <video className={classes.OvVideo} autoPlay={true} ref={videoRef} />;
};

export default OpenViduVideoComponent;
