package nomad.backend.global.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nomad.backend.global.api.mapper.Cluster;
import nomad.backend.global.api.mapper.OAuthToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@Service
public class ApiService {
    private final ObjectMapper om = new ObjectMapper();
    private final RestTemplate rt = new RestTemplate();
    public final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("UTC")));
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    HttpHeaders headers;
    MultiValueMap<String, String> params;
    HttpEntity<MultiValueMap<String, String>> request;
    ResponseEntity<String> response;


    public HttpEntity<MultiValueMap<String, String>> tokenHeader(String secret, String code) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "u-s4t2ud-9e9d9a8349093bbe40ba6f4dcaafa2b4905a0eff3eaa2a380f94b9ebc30c0dd9");
        params.add("client_secret", secret);
        params.add("code", code);
        params.add("redirect_uri","http://localhost:8080/auth/callback");
        return new HttpEntity<>(params, headers);
    }
    public HttpEntity<MultiValueMap<String, String>> requestHeader(String token) {
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        return new HttpEntity<>(params, headers);
    }

    public OAuthToken getOAuthToken(String code) {
        request = tokenHeader("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", code);
        response = responsePostApi(request, requestTokenUri());
        return oAuthTokenMapping(response.getBody());
    }
    public List<Cluster> getLoginCadets(String token, int page){
        request = requestHeader(token);
        response = responseGetApi(request, requsetLocationUri(page));
        return clusterMapping(response.getBody());
    }

    public List<Cluster> getLocationEnd(String token, int i) {
        request = requestHeader(token);
        response = responseGetApi(request, requestLocationEndUri(i));
        return clusterMapping(response.getBody());
    }

    public URI requestTokenUri() {
        return UriComponentsBuilder.fromHttpUrl("https://api.intra.42.fr/oauth/token")
                .build()
                .toUri();
    }
    public URI requsetLocationUri(int i) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path("/v2"+ "/campus/" + "29" + "/locations")
                .queryParam("page[size]", 10)
                .queryParam("page[number]", i)
                .queryParam("sort", "-end_at")
                .build()
                .toUri();
    }

    public URI requestLocationEndUri(int page) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -3);
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path("/v2"+ "/campus/" + "29" + "/locations")
                .queryParam("page[size]", 10)
                .queryParam("page[number]", page)
                .queryParam("range[end_at]", sdf.format(cal.getTime()) + "," + sdf.format(date))
                .build()
                .toUri();
    }

    public OAuthToken oAuthTokenMapping(String body) {
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = om.readValue(body, OAuthToken.class);
        } catch (JsonProcessingException e) {
            return null;
        }
        return oAuthToken;
    }
    //To do: exception class 생성 후 변경 필요
    public List<Cluster> clusterMapping(String body) {
        List<Cluster> clusters = null;
        try {
            clusters = Arrays.asList(om.readValue(body, Cluster[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
        return clusters;
    }

    public ResponseEntity<String> responseGetApi(HttpEntity<MultiValueMap<String, String>> requset, URI url) {
        return rt.exchange(
                url.toString(),
                HttpMethod.GET,
                request,
                String.class);
    }

    public ResponseEntity<String> responsePostApi(HttpEntity<MultiValueMap<String, String>> req, URI url) {
        return rt.exchange(
                url.toString(),
                HttpMethod.POST,
                req,
                String.class);
    }
}
