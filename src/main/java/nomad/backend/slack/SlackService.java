package nomad.backend.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.event.InviteRequestedEvent;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import nomad.backend.admin.CredentialsService;
import nomad.backend.global.Define;
import nomad.backend.meetingroom.MeetingRoomRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlackService {
    @Value(value = "${slack.token}")
    String slackToken;
    String slackChannel = "#자리_알림";

    private final NotificationRepository notificationRepository;
    private final CredentialsService credentialsService;
    private final JavaMailSender javaMailSender;
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
        if (slackDto.getUser() == null) {
            return null;
        }
        String id = (String) slackDto.getUser().get("id");
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

    public void sendSlackInviteMail(String intraId) {
        SimpleMailMessage message = new SimpleMailMessage();
        SlackInviteMailDto mailDto = slackInviteMailDtoMapping(intraId);
        String invitePath = credentialsService.getSlackPath().replace("\"", "");
        String inviteUrl = Define.SLACK_INVITE_URL + invitePath;
        message.setFrom("2023nomad42@gmail.com");
        message.setTo(mailDto.getAddress());
        message.setSubject(mailDto.getTitle());
        message.setText(inviteUrl);
        System.out.println(message);
        javaMailSender.send(message);
    }

    public SlackInviteMailDto slackInviteMailDtoMapping(String intraId) {
        SlackInviteMailDto slackInviteMailDto = new SlackInviteMailDto(intraId + "@student.42seoul.kr", "42nomad Slack Invite", "");
        return slackInviteMailDto;
    }

    public void sendMessageToUser(String intraId, String message) {

        String url = "https://slack.com/api/chat.postMessage";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-type", "application/json; charset=utf-8");

        String slackId = getSlackIdByEmail(intraId);
        if (slackId == null)
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channel", slackId);
        jsonObject.put("text", message);
        String body = jsonObject.toString();

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        HttpStatusCode httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        System.out.println(response);
    }

    public void findIMacNotificationAndSendMessage(String cadet, String location, String msg) {
        List<Notification> notifications = notificationRepository.findByIMacLocation(location);
        for (Notification noti : notifications) {
            if (!noti.getBooker().getIntra().equalsIgnoreCase(cadet))
                sendMessageToUser(noti.getBooker().getIntra(), location + msg);
        }
    }

    public void findMeetingRoomNotificationAndSendMessage(String cluster, String location, String msg) {
        List<Notification> notifications = notificationRepository.findByClusterAndMeetingRoomLocation(cluster, location);
        for (Notification noti : notifications) {
            sendMessageToUser(noti.getBooker().getIntra(), msg);
        }
    }



}