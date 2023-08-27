package nomad.backend.slack;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nomad.backend.member.MemberDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "slack")
@RestController
@RequiredArgsConstructor
public class SlackController {


    private final SlackService slackService;

    @Operation(summary = "Slack 메세지 테스트", description = "Slack 메세지 테스트",  operationId = "slackTest")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
            ),
    })
    @GetMapping("/slack/message")
    public void slack(@RequestParam String intra, @RequestParam String message){
        System.out.println("슬랙 테스트");
//        slackService.sendSlackMessage("test");
        slackService.sendMessageToUser(intra, message);
    }

}