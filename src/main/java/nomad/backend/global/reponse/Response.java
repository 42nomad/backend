package nomad.backend.global.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Response {
    @Schema(description = "status code")
    private int statusCode;
    @Schema(description = "response message")
    private String responseMsg;

    public static Response res(final int statusCode, final String responseMsg) {
        return Response.builder()
                .statusCode(statusCode)
                .responseMsg(responseMsg)
                .build();
    }
}
