package kr.co.cleaning.ctrl;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.config.KFException;
import kr.co.cleaning.svc.CmmnSvc;

@Controller
public class CmmnCtrl {

	private static final Logger log	= LoggerFactory.getLogger(CmmnCtrl.class);

	@Autowired
	CmmnSvc svc;

	@Autowired
	SessionCmn sessionCmn;

	@GetMapping(value = {"/apage", "/apage/", "/apage/signIn.do"})
	public String signIn(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
        if(sessionCmn.isLogon()) return "redirect:/apage/home.do";  // index 페이지로 이동

		return "apage/signIn";
	}

	@PostMapping("/apage/signInChk.do")
	public String signInChk(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
		modelMap.addAllAttributes(svc.getSignIn(paramMap));
		return "jsonView";
	}

	@GetMapping("/apage/signout.do")
	public void signOut(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
		sessionCmn.invalLogon();
		res.sendRedirect("/apage/");
	}

    @PostMapping("/fileUpload.do")
    public String fileUpload(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap,@RequestParam("files") List<MultipartFile> files, ModelMap modelMap) throws Exception {
        if (!sessionCmn.isLogon()) {
            throw new KFException("잘못된 접근 입니다. 로그인 정보가 없습니다.", 901);
        }
        modelMap.addAllAttributes(svc.setFileUpload(paramMap,files));
        return "jsonView";
    }

    @RequestMapping("/fileView.do")
    public String fileView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
        modelMap.addAllAttributes(svc.getFileView(paramMap));
        return "downloadView";
    }

    @RequestMapping("/fileThumbView.do")
    public String fileThumbView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
        modelMap.addAllAttributes(svc.getFileThumbView(paramMap));
        return "downloadView";
    }

    @PostMapping("/fileDel.do")
    public String fileDel(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
        if (!sessionCmn.isLogon()) {
            throw new KFException("잘못된 접근 입니다. 로그인 정보가 없습니다.", 901);
        }
        modelMap.addAllAttributes(svc.setFileDel(paramMap));
        return "jsonView";
    }

    @PostMapping("/editorImgUploader.do")
    public String editorImgUploader(HttpServletRequest req ,HttpServletResponse res,@RequestParam("files") List<MultipartFile> files, ModelMap modelMap) throws Exception {
        if (!sessionCmn.isLogon()) {
            throw new KFException("잘못된 접근 입니다. 로그인 정보가 없습니다.", 901);
        }
        modelMap.addAllAttributes(svc.setEditorImgUploader(files));
        return "jsonView";
    }

	@GetMapping("/editorFileView.do")
	public String editorFileView(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
		modelMap.addAllAttributes(svc.getEditorFileView(paramMap));
		return "downloadView";
	}

	@PostMapping("/serviceCdList.do")
	public String serviceCdList(HttpServletRequest req ,HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {
		modelMap.addAttribute("svcCdList", svc.getServiceCdList());
		return "jsonView";
	}

}
