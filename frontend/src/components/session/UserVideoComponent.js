import OpenViduVideoComponent from "./OpenViduVideoComponent";
import classes from "./UserVideoComponent.module.css";
import { useState, useEffect } from "react";

const UserVideoComponent = (props) => {
  const [userName, setUserName] = useState("");

  useEffect(() => {
    if (props.streamManager.stream.connection) {
      // if we had both clientData and serverData we would have to do something like this
      // let clientData = JSON.parse(props.streamManager.stream.connection.data.split('%/%')[0]).clientData;
      let userName = JSON.parse(props.streamManager.stream.connection.data).username;
      setUserName(userName);
    }
  }, [props]);

  return (
    <div className={classes.videoWrapper}>
      {props.streamManager && (
        <div className={classes.streamComponent}>
          <OpenViduVideoComponent streamManager={props.streamManager} />
          <div>
            <p>{userName}</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserVideoComponent;
