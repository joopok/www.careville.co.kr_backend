package kr.co.cleaning.ctrl;

import java.util.HashMap;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.utils.AESUtil;
import kr.co.cleaning.svc.CmmnSvc;
import kr.co.cleaning.svc.CnsltSvc;

@Controller
public class TestCtrl {

	private static final Logger log	= LoggerFactory.getLogger(TestCtrl.class);

	@Autowired
	CnsltSvc svc;

	@Autowired
	CmmnSvc cmmnSvc;

	@PostMapping("/test.do")
	public String test(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {


        String rawPassword	= "1234";
        String hashed		= BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        boolean matched		= BCrypt.checkpw(rawPassword, hashed);

//        log.info("----- 단방향 해싱 : {}",hashed);
//        log.info("----- 단방향 매칭 : {}",matched);


        String plain		= "1";
        String encrypted	= AESUtil.encrypt(plain);
        String decrypted	= AESUtil.decrypt(encrypted);

//        log.info("----- 양방향 원문   : {}",plain);
//        log.info("----- 양방향 암호화 : {}",encrypted);
//        log.info("----- 양방향 복호화 : {}",decrypted);


        String a	= "R9czQJ1ySt9RXjiOrLpk7A==";
        String b	= "eyji6KmSO+ZtPgfctWc/tg==";

        log.info("----- 양방향 복호화 a : {}",AESUtil.urlDec(a));
        log.info("----- 양방향 복호화 b : {}",AESUtil.urlDec(b));

		return "jsonView";
	}


}
