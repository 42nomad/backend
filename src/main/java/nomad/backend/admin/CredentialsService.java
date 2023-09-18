package nomad.backend.admin;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.Define;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.OAuthToken;
import nomad.backend.global.exception.UnauthorizedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CredentialsService {
    private final CredentialsRepository credentialsRepository;
    private final ApiService apiService;

    public String getSecret() {
        return checkSecret();
    }

    @Scheduled(cron = "0 0 10 * * *") // 초 분 시 일 월 요일
    public String checkSecret() {
        Credentials secret = credentialsRepository.findByCredentialType(Define.SECRET_ID);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -20);
        Date twentyDaysAgo = calendar.getTime();
        System.out.println("secret 20일 전은 " + calendar.toString());
        if (secret.getCreatedAt().before(twentyDaysAgo)) {
            // 슬랙봇으로 담당자한테 시크릿 갈라고 알려주기~!
            System.out.println("슬랙봇 자리");
        }
        return secret.getData();
    }

    public String getAccessToken() {
        checkAndReissueAccessToken();
        return credentialsRepository.findByCredentialType(Define.ACCESS_TOKEN).getData();
    }

    public String getSlackPath() {
        return credentialsRepository.findByCredentialType(Define.SLACK_PATH).getData();
    }

    public void checkAndReissueAccessToken() {
        try {
            Credentials accessToken = credentialsRepository.findByCredentialType(Define.ACCESS_TOKEN);
            Long diff = (new Date().getTime() - accessToken.getCreatedAt().getTime()) / (1000 * 60);
            System.out.println("토큰을 발급받은지 " + diff + "분 지났습니다.");
            if (diff > 110) {
                String secret = credentialsRepository.findByCredentialType(Define.SECRET_ID).getData();
                String refreshToken = credentialsRepository.findByCredentialType(Define.REFRESH_TOKEN).getData();
                OAuthToken oAuthToken = apiService.getNewOAuthToken(secret, refreshToken);
                credentialsRepository.insertCredential(Define.ACCESS_TOKEN, oAuthToken.getAccess_token());
                credentialsRepository.insertCredential(Define.REFRESH_TOKEN, oAuthToken.getRefresh_token());
            }
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }
}
