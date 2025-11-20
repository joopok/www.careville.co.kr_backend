package kr.co.cleaning.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Careville 청소 서비스 API")
                .version("v1.0.0")
                .description("Careville 청소 서비스 관리 시스템 REST API 문서")
                .contact(new Contact()
                        .name("Careville 개발팀")
                        .email("dev@careville.co.kr"));

        Server localServer = new Server()
                .url("http://localhost:8081")
                .description("로컬 개발 서버");

        Server productionServer = new Server()
                .url("https://api.careville.co.kr")
                .description("운영 서버");

        // 비밀번호 인증 스키마 정의
        SecurityScheme passwordScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.QUERY)
                .name("password")
                .description("리뷰 비밀번호 (비밀번호가 설정된 리뷰 접근 시 필요)");

        // 세션 인증 스키마 정의 (관리자용)
        SecurityScheme sessionScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("LCMS_SESSION")
                .description("관리자 세션 ID");

        Components components = new Components()
                .addSecuritySchemes("reviewPassword", passwordScheme)
                .addSecuritySchemes("sessionAuth", sessionScheme);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer))
                .components(components);
    }
}