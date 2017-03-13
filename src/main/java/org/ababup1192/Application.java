package org.ababup1192;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;


@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        System.out.println("main()");
        try (ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args)) {
            Application app = ctx.getBean(Application.class);
            app.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String... args) {
        log.info("====Start====");
        final RestTemplate restTemplate = new RestTemplate();

        // セッションの取得(通常はPOST)
        ResponseEntity<String> loginEntity = restTemplate.getForEntity(
                "http://localhost:8080/session",
                String.class
        );
        log.info(loginEntity.getBody());

        // クッキーからセッションIDの取得
        String sessionCookie = loginEntity.getHeaders().get("Set-Cookie").stream().collect(Collectors.joining(";"));

        // 認証が必要なAPIを叩く前に、先程のセッションIDをヘッダー(クッキー)にセット
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionCookie);

        // ヘッダーを設定
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 認証が必要なAPIを叩く
        ResponseEntity<String> getPeopleEntity = restTemplate.exchange(
                "http://localhost:8080/people",
                HttpMethod.GET,
                entity,
                String.class);
        log.info(String.valueOf(getPeopleEntity.getBody()));
    }
}
