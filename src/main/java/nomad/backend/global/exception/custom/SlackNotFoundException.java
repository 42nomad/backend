package nomad.backend.global.exception.custom;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class SlackNotFoundException extends Exception {
    private int errorCode;
    private Long notificationId;

    public SlackNotFoundException(Long notificationId) {
        super(ResponseMsg.SLACK_NOT_FOUND);
        this.errorCode = StatusCode.NOT_FOUND;
        this.notificationId = notificationId;
    }
}
