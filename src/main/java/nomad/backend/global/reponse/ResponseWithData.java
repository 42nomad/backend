package nomad.backend.global.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseWithData {
    @Schema(description = "status code")
    private int statusCode;
    @Schema(description = "response message")
    private String responseMsg;
    @Schema(description = "response data")
    private Long responseData;

    public static ResponseWithData res(final int statusCode, final String responseMsg, final  Long responseData) {
        return ResponseWithData.builder()
                .statusCode(statusCode)
                .responseMsg(responseMsg)
                .responseData(responseData)
                .build();
    }
}