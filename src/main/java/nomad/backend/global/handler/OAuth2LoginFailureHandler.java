package nomad.backend.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException, IOException {
        String jsonResponse = null;
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2Error oauth2Error = ((OAuth2AuthenticationException) exception).getError();
            String errorCode = oauth2Error.getErrorCode();
            // 여기를 확인할 방법이..
            if ("invalid_token".equals(errorCode) || "access_denied".equals(errorCode)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                jsonResponse = "{\"statusCode\": " + StatusCode.UNAUTHORIZED + ",\"responseMsg\": \"" + ResponseMsg.UNAUTHORIZED +"\"}";
            } else {
                if ("too_many_requests".equals(errorCode)) {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    jsonResponse = "{\"statusCode\": " + StatusCode.TOO_MANY_REQUEST + ",\"responseMsg\": \"" + ResponseMsg.TOO_MANY_REQUEST +"\"}";
                } else {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    jsonResponse = "{\"statusCode\": " + StatusCode.INTERNAL_SERVER_ERROR + ",\"responseMsg\": \"" + oauth2Error.getDescription() +"\"}";
                }
            }
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            jsonResponse = "{\"statusCode\": " + StatusCode.INTERNAL_SERVER_ERROR + ",\"responseMsg\": \"" + exception.getMessage() +"\"}";
        }
        response.getWriter().write(jsonResponse);
    }
}
