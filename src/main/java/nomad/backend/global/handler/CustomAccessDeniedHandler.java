package nomad.backend.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final HttpRequestEndpointChecker endpointChecker;
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = null;
        if (endpointChecker.isEndpointExist(request)) {
            jsonResponse = "{\"statusCode\": " + StatusCode.NOT_FOUND + ",\"responseMsg\": \"" + ResponseMsg.API_NOT_FOUND +"\"}";
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {
            jsonResponse = "{\"statusCode\": " + StatusCode.FORBIDDEN + ",\"responseMsg\": \"" + ResponseMsg.FORBIDDEN + "\"}";
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
            response.getWriter().write(jsonResponse);
    }
}
