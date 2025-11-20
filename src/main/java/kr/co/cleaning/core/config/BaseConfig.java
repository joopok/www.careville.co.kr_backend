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

import jakarta.servlet.http.HttpSession;
import kr.co.cleaning.core.utils.AESUtil;
import kr.co.cleaning.core.utils.FileUtil;
import kr.co.cleaning.core.utils.PageUtil;

@Configuration
public class BaseConfig  implements WebMvcConfigurer{


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
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
    		public void addCorsMappings(CorsRegistry registry) {
    			registry.addMapping("/**") // 모든 경로 허용
    			.allowedOrigins("http://localhost:3000") // React 개발 서버 주소
    			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    			.allowedHeaders("*")
    			.allowCredentials(true);
    		}
    	};
    }

	// JSP 기반 뷰
    /*
    @Bean
    ViewResolver viewResolver() {
    	InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(2);
        return viewResolver;
    }
    */

    // 컨트롤러 wiew 이름으로 등록된 빈으로 연결
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
	Pointcut pointcut(SessionCmn sessionCmn){
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

		Properties mappings 	= new Properties();	// 예외 클래스에 대해 에러 페이지를 지정합니다.
		Properties statusCodes	= new Properties();	// 에러페이지에 상태코드를 지정합니다.

		mappings.setProperty("java.lang.NullPointerException",	"error/defaultError");
		statusCodes.setProperty("error/nullPointer", "500");

		mappings.setProperty("org.springframework.web.multipart.MultipartException", "error/defaultError");
		statusCodes.setProperty("error/fileError", "400");

		mappings.setProperty("kr.co.kcs.core.base.KFException", "error/defaultError");
		statusCodes.setProperty("error/KFException", "999");

		mappings.setProperty("java.lang.Exception", "error/defaultError");
		statusCodes.setProperty("error/defaultError", "500");

		excpRes.setDefaultErrorView("error/defaultError");	// 지정되지 않은 예외에 대한 기본 에러페이지 입니다.
		excpRes.setExceptionMappings(mappings);
		excpRes.setStatusCodes(statusCodes);

		return excpRes;
	}

}
