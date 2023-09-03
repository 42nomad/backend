package nomad.backend.global.exception;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;
@Getter
public class ConflictException extends RuntimeException {
    private int errorCode;

    public ConflictException() {
        super(ResponseMsg.CONFLICT);
        this.errorCode = StatusCode.CONFLICT;
    }
}
