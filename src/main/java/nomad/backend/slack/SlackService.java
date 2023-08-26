package nomad.backend.slack;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class SlackService {
    @Value(value = "${slack.token}")
    String slackToken;
    String slackChannel = "#자리_알림";

    public void sendSlackMessage(String message) {

        try {
            MethodsClient methods = Slack.getInstance().methods(slackToken);
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(slackChannel)
                    .text(message)
                    .build();

            methods.chatPostMessage(request);

        } catch (SlackApiException | IOException e) {
            System.out.println("error");
        }
    }

    public String getSlackIdByEmail(String intrald) {
        String url = "https://slack.com/api/users. lookupByEmail";
        String email = intrald + "@gmail.com";
        url += "?email=" + email;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-type", "application/x-www-form-urlencoded");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                String.class
        );
        System.out.println(responseEntity);
        return responseEntity.getBody();
    }
}
