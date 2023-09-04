package nomad.backend.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final HttpRequestEndpointChecker endpointChecker;
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException, ServletException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = null;
        if (!endpointChecker.isEndpointExist(request)) {
            System.out.println("not found");
            jsonResponse = "{\"statusCode\": " + StatusCode.NOT_FOUND + ",\"responseMsg\": \"" + ResponseMsg.API_NOT_FOUND +"\"}";
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {
            System.out.println("unauth");
            jsonResponse = "{\"statusCode\": " + StatusCode.UNAUTHORIZED + ",\"responseMsg\": \"" + ResponseMsg.UNAUTHORIZED +"\"}";
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        response.getWriter().write(jsonResponse);
    }
}
