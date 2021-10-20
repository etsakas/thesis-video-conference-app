import { OpenVidu } from "openvidu-browser";
import React, { useState, useEffect, useCallback } from "react";
import UserVideoComponent from "./UserVideoComponent";
import classes from "./VideoPanel.module.css";

const VideoPanel = (props) => {
  const [OV, setOV] = useState(undefined);
  const [session, setSession] = useState(undefined);
  const [mainStreamManager, setMainStreamManager] = useState(undefined);
  const [publisher, setPublisher] = useState(undefined);
  const [subscribers, setSubscribers] = useState([]);

  const setIsConnected = props.setIsConnected;
  const setSessionData = props.setSessionData;

  const leaveSession = useCallback(() => {
    if (session) {
      session.disconnect();
    }

    setSessionData(null);
    setIsConnected(false);
    setOV(undefined);
    setSession(undefined);
    setSubscribers([]);
    setMainStreamManager(undefined);
    setPublisher(undefined);
  }, [session, setIsConnected, setSessionData]);

  useEffect(() => {
    window.addEventListener("beforeunload", leaveSession);

    if (OV === undefined) {
      let OV = new OpenVidu();
      setOV(OV);
      setSession(OV.initSession());
    }

    return () => {
      window.removeEventListener("beforeunload", leaveSession);
    };
  }, [leaveSession, OV]);

  // leaveSession if VideoPanel component gets unmount
  useEffect(() => {
    return () => {
      if (session) {
        leaveSession();
      }
    };
  }, [leaveSession, session]);

  const handleMainVideoStream = (stream) => {
    if (mainStreamManager !== stream) {
      setMainStreamManager(stream);
    }
  };
  
  useEffect(() => {
    if (!session) return;

    session.on("streamCreated", async (event) => {
      let subscriber = await session.subscribeAsync(event.stream, undefined);
      let subscribersUpdatedList = [...subscribers, subscriber];
      setSubscribers(subscribersUpdatedList);
    });

    session.on("streamDestroyed", (event) => {
      // in case we kick the user from the server we redirect
      // the user back to the join session form.
      if (publisher && event.stream.connection.connectionId === publisher.stream.connection.connectionId) {
        setSessionData(null);
      }

      let deleteSubscriber = (streamManager) => {
          let subscribersList = subscribers;
          let index = subscribersList.indexOf(streamManager, 0);

          if (index > -1) {
            let removedSubscriberId = subscribersList.splice(index, 1)[0].stream.connection.connectionId;
            setSubscribers(subscribersList);
            console.log("A SUBSCRIBER WAS REMOVED: ", removedSubscriberId);
            // check if the user that leaved was the mainStreamManager
            // If so, pick another user
            if (subscribersList.length === 0) {
              setMainStreamManager(publisher);
            } else if (mainStreamManager && removedSubscriberId === mainStreamManager.stream.connection.connectionId) {
              setMainStreamManager(subscribersList[0]);
            }
          }
        };

      deleteSubscriber(event.stream.streamManager);
    });

    // Produces error. Could this be an openvidu issue???
    // https://docs.openvidu.io/en/2.19.0/advanced-features/speech-detection/
    // session.on('publisherStartSpeaking', (event) => {
    //   for (var i = 0; i < subscribers.length; i++) {
    //     if (event.connection.connectionId === subscribers[i].stream.connection.connectionId) {
    //       setMainStreamManager(subscribers[i]);
    //       break;
    //     }
    //   }
    // });

    session.on("exception", (exception) => {
      console.warn(exception);
    });

    if (publisher) return; // WARNING! The condition if (props.isConnected) return; doesn't work!

    session
      .connect(props.token)
      .then(() => {
        // let publisher = OV.initPublisher(undefined);
        let publisher = OV.initPublisher(undefined, {
          audioSource: undefined, // The source of audio. If undefined default microphone
          videoSource: undefined, // The source of video. If undefined default webcam
          publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
          publishVideo: true, // Whether you want to start publishing with your video enabled or not
          resolution: "640x480", // The resolution of your video
          frameRate: 30, // The frame rate of your video
          insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
          mirror: false, // Whether to mirror your local video or not
        });
        setPublisher(publisher);
        setMainStreamManager(publisher);
        session.publish(publisher);
      })
      .then(() => setIsConnected(true))
      .catch((error) => {
        console.log(
          "There was an error connecting to the session:",
          error.code,
          error.message
        );
      });
  }, [session, OV, props.token, subscribers, mainStreamManager, setIsConnected, props.isConnected, setSessionData, publisher]);

  const videoPanel = (
    <div className={classes.videoPanel}>
      <div className={classes.sessionHeader}>
        <h1 className={classes.sessionTitle}>{props.sessionName}</h1>
        <button onClick={() => setSessionData(null)}>Leave session</button>
      </div>

      <div className={classes.videoContainer}>
        {mainStreamManager && (
          <div className={classes.mainVideo}>
            <UserVideoComponent streamManager={mainStreamManager} />
          </div>
        )}
        <div className={classes.miniVideoContainer}>
          {publisher && (
            <div
              className={classes.streamContainer}
              onClick={() => handleMainVideoStream(publisher)}
            >
              <UserVideoComponent streamManager={publisher} />
            </div>
          )}
          {subscribers.map((sub, i) => (
            <div
              key={i}
              className={classes.streamContainer}
              onClick={() => handleMainVideoStream(sub)}
            >
              <UserVideoComponent streamManager={sub} />
            </div>
          ))}
        </div>
        <div className={classes.chatContainer}></div>
      </div>
    </div>
  );

  return (
    <React.Fragment>
      {session && props.isConnected && videoPanel}
    </React.Fragment>
  );
};

export default VideoPanel;

// NOTE: We want to execute the leaveSession function on component unmount
// in case the user visit another page inside our application. For this reason
// we use useEffect. However, if leave button had the property onClick={leaveSession}
// this would cause 2 leaveSession executions: one from the button and one from the
// useEffect. So when the button is click we only want to unmount the component.
// Since the VideoPanel component is render from JoinSessionPanel only in case
// sessionData is non-null, we set the sessionData to null and this will cause
// VideoPanel's unmount. Then the useEffect will execute the leaveSession function.
