package kr.co.cleaning.core.config;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.CacheControl;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import org.springframework.lang.NonNull;

import jakarta.servlet.http.HttpSession;
import kr.co.cleaning.core.utils.AESUtil;
import kr.co.cleaning.core.utils.FileUtil;
import kr.co.cleaning.core.utils.PageUtil;

@Configuration
public class BaseConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
		// CSS, JS, 이미지 등 정적 리소스
		registry.addResourceHandler("/css/**")
				.addResourceLocations("classpath:/static/css/")
				.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));

		registry.addResourceHandler("/js/**")
				.addResourceLocations("classpath:/static/js/")
				.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));

		registry.addResourceHandler("/images/**")
				.addResourceLocations("classpath:/static/images/")
				.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));

		registry.addResourceHandler("/plugins/**")
				.addResourceLocations("classpath:/static/plugins/")
				.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));

		registry.addResourceHandler("/apage/**")
				.addResourceLocations("classpath:/static/apage/")
				.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));

		// HTML 파일들 (테스트용)
		registry.addResourceHandler("/*.html")
				.addResourceLocations("classpath:/static/")
				.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));
	}

	@Bean
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NonNull CorsRegistry registry) {
				registry.addMapping("/**") // 모든 경로 허용
						// 개발 환경
						.allowedOriginPatterns("http://localhost:*")
						.allowedOriginPatterns("http://127.0.0.1:*")
						// 프로덕션 환경
						.allowedOriginPatterns("https://*.careville.co.kr")
						.allowedOriginPatterns("https://careville.co.kr")
						.allowedOriginPatterns("http://careville.co.kr")
						.allowedOriginPatterns("http://www.careville.co.kr")
						.allowedOriginPatterns("https://www.careville.co.kr")
						// 카페24 호스팅 환경
						.allowedOriginPatterns("http://ksm1779.cafe24.com")
						.allowedOriginPatterns("https://ksm1779.cafe24.com")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
						.allowedHeaders("*")
						.exposedHeaders("Content-Type", "Authorization", "X-Total-Count")
						.allowCredentials(true)
						.maxAge(3600L); // preflight 캐시 1시간
			}
		};
	}

	// 컨트롤러 view 이름으로 등록된 빈으로 연결
	@Bean
	BeanNameViewResolver beanNameViewResolver() {
		BeanNameViewResolver resolver = new BeanNameViewResolver();
		resolver.setOrder(0); // Thymeleaf보다 먼저 처리
		return resolver;
	}

	// JSON 응답
	@Bean
	JsonView jsonView() {
		return new JsonView();
	}

	// File DownLoad
	@Bean
	DownloadView downloadView() {
		return new DownloadView();
	}

	// File 처리
	@Bean
	FileUtil fileUtil() {
		return new FileUtil();
	}

	// Page 처리
	@Bean
	PageUtil pageUtil() {
		return new PageUtil();
	}

	// Session 공통
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	SessionCmn sessionCmn(HttpSession session) {
		return new SessionCmn(session);
	}

	// AOP Pointcut
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	Pointcut pointcut(SessionCmn sessionCmn) {
		return new Pointcut(sessionCmn);
	}

	// AES 암복호화 모듈
	@Bean
	AESUtil aesUtil(@Value("${kframe.aes.key}") String aesKey) {
		return new AESUtil(aesKey);
	}

	// 내부 Exception 공통 처리
	@Bean
	SimpleMappingExceptionResolver exceptionResolver() {
		return new KFExceptionResolver();
	}

	// 내부 Exception 공통 처리 정의
	@Bean
	KFExceptionResolver setExceptionResolver(KFExceptionResolver excpRes) {

		Properties mappings = new Properties(); // 예외 클래스에 대해 에러 페이지를 지정합니다.
		Properties statusCodes = new Properties(); // 에러페이지에 상태코드를 지정합니다.

		mappings.setProperty("java.lang.NullPointerException", "error/defaultError");
		statusCodes.setProperty("error/nullPointer", "500");

		mappings.setProperty("org.springframework.web.multipart.MultipartException", "error/defaultError");
		statusCodes.setProperty("error/fileError", "400");

		mappings.setProperty("kr.co.cleaning.core.config.KFException", "error/defaultError");
		statusCodes.setProperty("error/KFException", "999");

		mappings.setProperty("java.lang.Exception", "error/defaultError");
		statusCodes.setProperty("error/defaultError", "500");

		excpRes.setDefaultErrorView("error/defaultError"); // 지정되지 않은 예외에 대한 기본 에러페이지 입니다.
		excpRes.setExceptionMappings(mappings);
		excpRes.setStatusCodes(statusCodes);

		return excpRes;
	}

}
