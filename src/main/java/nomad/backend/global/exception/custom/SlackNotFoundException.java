package nomad.backend.global.exception.custom;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class SlackNotFoundException extends Exception {
    private int errorCode;

    public SlackNotFoundException() {
        super(ResponseMsg.SLACK_NOT_FOUND);
        this.errorCode = StatusCode.NOT_FOUND;
    }
}
