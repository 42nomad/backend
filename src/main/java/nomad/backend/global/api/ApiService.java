package nomad.backend.global.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nomad.backend.global.Define;
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
        params.add("client_id", "u-s4t2ud-e4da46cee5b6372c0211c39eeac7b3478f15aaec565ef5c9f99e32795e6edc2b");
        params.add("client_secret", secret);
        params.add("code", code);
        params.add("redirect_uri", "https://api.42nomad.kr/admin/callback");
        return new HttpEntity<>(params, headers);
    }

    public HttpEntity<MultiValueMap<String, String>> refreshTokenHeader(String secret, String refreshToken) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "u-s4t2ud-e4da46cee5b6372c0211c39eeac7b3478f15aaec565ef5c9f99e32795e6edc2b");
        params.add("client_secret", secret);
        params.add("refresh_token", refreshToken);
        return new HttpEntity<>(params, headers);
    }

    public HttpEntity<MultiValueMap<String, String>> requestHeader(String token) {
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        return new HttpEntity<>(params, headers);
    }

    public OAuthToken getOAuthToken(String secret, String code) {
        request = tokenHeader(secret, code);
        response = responsePostApi(request, requestTokenUri());
        return oAuthTokenMapping(response.getBody());
    }

    public OAuthToken getNewOAuthToken(String secret, String refreshToken) {
        request = refreshTokenHeader(secret, refreshToken);
        response = responsePostApi(request, requestTokenUri());
        return oAuthTokenMapping(response.getBody());
    }

    public List<Cluster> getAllLoginCadets(String token, int page){
        request = requestHeader(token);
        response = responseGetApi(request, requsetLocationUri(page));
        return clusterMapping(response.getBody());
    }

    public List<Cluster> getRecentlyLogoutCadet(String token, int page) {
        request = requestHeader(token);
        response = responseGetApi(request, requestRecentlyLogoutUri(page));
        return clusterMapping(response.getBody());
    }

    public List<Cluster> getRecentlyLoginCadet(String token, int page) {
        request = requestHeader(token);
        response = responseGetApi(request, requestRecentlyLoginUri(page));
        return clusterMapping(response.getBody());
    }

    public URI requestTokenUri() {
        return UriComponentsBuilder.fromHttpUrl("https://api.intra.42.fr/oauth/token")
                .build()
                .toUri();
    }

    public URI requsetLocationUri(int page) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 100)
                .queryParam("page[number]", page)
                .queryParam("sort", "-end_at") // range null로 줄 수 있는 방법
                .build()
                .toUri();
    }

    public URI requestRecentlyLogoutUri(int page) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -3);
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 50)
                .queryParam("page[number]", page)
                .queryParam("range[end_at]", sdf.format(cal.getTime()) + "," + sdf.format(date))
                .build()
                .toUri();
    }

    public URI requestRecentlyLoginUri(int page) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -3);
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 50)
                .queryParam("page[number]", page)
                .queryParam("range[begin_at]", sdf.format(cal.getTime()) + "," + sdf.format(date))
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
