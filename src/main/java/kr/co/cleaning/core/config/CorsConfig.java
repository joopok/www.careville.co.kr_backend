package kr.co.cleaning.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // allowedOriginPatterns 사용 (credentials와 함께 사용 가능)
        // 개발 환경
        config.addAllowedOriginPattern("http://localhost:*"); // 모든 localhost 포트 허용
        config.addAllowedOriginPattern("http://127.0.0.1:*"); // 127.0.0.1 허용

        // 프로덕션 환경에서는 아래와 같이 특정 도메인만 허용
        config.addAllowedOriginPattern("https://*.careville.co.kr");
        config.addAllowedOriginPattern("https://careville.co.kr");
        config.addAllowedOriginPattern("http://careville.co.kr");
        config.addAllowedOriginPattern("http://www.careville.co.kr");
        config.addAllowedOriginPattern("https://www.careville.co.kr");

        // Cafe24 호스팅 도메인 추가
        config.addAllowedOriginPattern("http://ksm1779.cafe24.com");
        config.addAllowedOriginPattern("https://ksm1779.cafe24.com");
        config.addAllowedOriginPattern("http://*.cafe24.com");
        config.addAllowedOriginPattern("https://*.cafe24.com");

        // 허용할 HTTP 메서드
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");

        // 허용할 헤더
        config.addAllowedHeader("*");

        // 자격 증명(쿠키, 인증 정보) 포함 허용
        config.setAllowCredentials(true);

        // 노출할 응답 헤더
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("Authorization");
        config.addExposedHeader("X-Total-Count");

        // preflight 캐시 시간 (초단위)
        config.setMaxAge(3600L);

        // API 경로에 대해 CORS 설정 적용
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/reviewList.do", config);
        source.registerCorsConfiguration("/reviewView.do", config);
        source.registerCorsConfiguration("/reviewReg.do", config);
        source.registerCorsConfiguration("/reviewUpd.do", config);
        source.registerCorsConfiguration("/reviewDel.do", config);

        // 모든 경로에 CORS 적용 (디버깅 및 안정성 강화)
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}