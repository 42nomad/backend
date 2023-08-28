package nomad.backend.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlackService {
    @Value(value = "${slack.token}")
    String slackToken;
    String slackChannel = "#자리_알림";

    private final NotificationRepository notificationRepository;

    private final ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    public void sendSlackMessage(String message) {
    //채널에 메세지를 올리는 함수
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
        System.out.println("getSlackIdByEmail");
        String url = "https://slack.com/api/users.lookupByEmail";
        String email = intrald + "@student.42seoul.kr";
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
        SlackDto slackDto = slackDtoMapping(responseEntity.getBody());
        String id = (String)slackDto.getUser().get("id");
        return id;
    }

    public SlackDto slackDtoMapping(String body) {
        SlackDto slackDto = null;
        try {
            slackDto = om.readValue(body, SlackDto.class);
        } catch (JsonProcessingException e) {
            System.out.println(e);
            return null;
        }
        return slackDto;
    }
    public void sendMessageToUser(String intraId, String message) {
        System.out.println("sendMessageToUser " + intraId + " " + message);

        String url = "https://slack.com/api/chat.postMessage";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-type", "application/json; charset=utf-8");

        String slackId = getSlackIdByEmail(intraId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channel", slackId);
        jsonObject.put("text", intraId + " 님께:  " + message);
        String body = jsonObject.toString();

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        HttpStatusCode httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        System.out.println("status = " + status);
        System.out.println(response);
    }

    public void findNotificationAndSendMessage(String cadet, String location, String msg) {
        List<Notification> notifications = notificationRepository.findByLocation(location);
        if (notifications == null)
            return ;
        for (Notification noti : notifications) {
            if (!noti.getBooker().getIntra().equalsIgnoreCase(cadet))
                sendMessageToUser(noti.getBooker().getIntra(), location + msg);
        }
    }
}

