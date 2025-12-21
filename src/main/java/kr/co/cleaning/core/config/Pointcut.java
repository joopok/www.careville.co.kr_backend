package kr.co.cleaning.core.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.ui.ModelMap;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
public class Pointcut {

    private final SessionCmn sessionCmn;

    public Pointcut(SessionCmn sessionCmn) {
        this.sessionCmn = sessionCmn;
    }

    @Around("execution(* kr.co.cleaning.ctrl..*.*(..)) && !within(kr.co.cleaning.ctrl.CmmnCtrl)")
    public Object aroundTransfer(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        ModelMap mm = null;
        HttpServletRequest req = null;

        for (Object arg : args) {
            if (arg instanceof ModelMap) {
                mm = (ModelMap) arg;
            } else if (arg instanceof HttpServletRequest) {
                req = (HttpServletRequest) arg;
            }
        }
        /*
         * /apage/ 로 시작하는 요청만 세션 체크
         */
        if (req != null && req.getRequestURI().startsWith("/apage/")) {
            if (!sessionCmn.isLogon()) {
                throw new KFException("잘못된 접근 입니다. 로그인 정보가 없습니다.", 901);
            }
            if (mm != null) {
                mm.addAttribute("loginSession", sessionCmn.getLogonInfo());
                mm.addAttribute("currentUri", req.getRequestURI());
            }
        }

        return joinPoint.proceed(args); // 실제 메서드 실행
    }
}