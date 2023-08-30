package nomad.backend.admin;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.Define;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.OAuthToken;
import nomad.backend.global.exception.custom.UnauthorizedException;
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

    // 매 access 필요시마다 체크
    public String getAccessToken() {
        checkAndReissueAccessToken();
        return credentialsRepository.findByCredentialType(Define.ACCESS_TOKEN).getData();
    }

    public void checkAndReissueAccessToken() {
        try {
            Credentials accessToken = credentialsRepository.findByCredentialType(Define.ACCESS_TOKEN);
            Long diff = (new Date().getTime() - accessToken.getCreatedAt().getTime()) / (1000 * 60);
            System.out.println("토큰을 발급받은지 " + diff + "분 지났습니다.");
            // 안지났으면 그대로 어세스 사용할 수 있도록
            if (diff > 110) {// 1시간 50분이 지났으면 리프레시로 새로 갱신한다
                String secret = credentialsRepository.findByCredentialType(Define.SECRET_ID).getData();
                String refreshToken = credentialsRepository.findByCredentialType(Define.REFRESH_TOKEN).getData();
                OAuthToken oAuthToken = apiService.getNewOAuthToken(secret, refreshToken);
                credentialsRepository.insertCredential(Define.ACCESS_TOKEN, oAuthToken.getAccess_token());
                credentialsRepository.insertCredential(Define.REFRESH_TOKEN, oAuthToken.getRefresh_token());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("아마도 401 error, refresh도 만료된 것으로 admin 페이지에서 새로 발급 받아야 함");
            throw new UnauthorizedException();
        }
    }
}
