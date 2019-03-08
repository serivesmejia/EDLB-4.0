package io.github.serivesmejia.EDLB;

import io.github.serivesmejia.EDLB.Arena.GameState;
import static io.github.serivesmejia.EDLB.Arena.GameState.BLOCKED;
import static io.github.serivesmejia.EDLB.Arena.GameState.BUILDING;
import static io.github.serivesmejia.EDLB.Arena.GameState.CLOSED;
import static io.github.serivesmejia.EDLB.Arena.GameState.DISABLED;
import static io.github.serivesmejia.EDLB.Arena.GameState.ENDING;
import static io.github.serivesmejia.EDLB.Arena.GameState.PLAYING;
import static io.github.serivesmejia.EDLB.Arena.GameState.SETUPING;
import static io.github.serivesmejia.EDLB.Arena.GameState.STARTING;
import static io.github.serivesmejia.EDLB.Arena.GameState.WAITING;

public class State {
    
        public String gameStateToString(GameState state){
        if(null != state)
            switch (state){
            case BUILDING:
                return "BUILDING";
            case SETUPING:
                return "SETUPING";
            case WAITING:
                return "WAITING";
            case STARTING:
                return "STARTING";
            case PLAYING:
                return "PLAYING";
            case ENDING:
                return "ENDING";
            case CLOSED:
                return "CLOSED";
            case DISABLED:
                return "DISABLED";
            case BLOCKED:
                return "BLOCKED";
            default:
                break;
        }
        return null;
    }
    
    public GameState stringToGameState(String state){
        if(null != state)
           switch (state) {
            case "BUILDING":
                return BUILDING;
            case "SETUPING":
                return SETUPING;
            case "WAITING":
                return WAITING;
            case "STARTING":
                return STARTING;
            case "PLAYING":
                return PLAYING;
            case "ENDING":
                return ENDING;
            case "CLOSED":
                return CLOSED;
            case "DISABLED":
                return DISABLED;
            case "BLOCKED":
                return BLOCKED;
            default:
                break;
        }
        return null;
    }    
}
