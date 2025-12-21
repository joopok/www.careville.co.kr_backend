package kr.co.cleaning.core.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.core.utils.SUtils;

public class DownloadView extends AbstractView {

	private static final Logger log	= LoggerFactory.getLogger(DownloadView.class);

    @Override
	public void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String filePath = SUtils.nvl(model.get("filePath"));
        String fileName = SUtils.nvl(model.get("fileName"));

        // 파일 경로가 비어있으면 404 반환 (DB에 파일 정보가 없는 경우)
        if (SUtils.isNvl(filePath)) {
            log.debug("[DownloadView] Empty file path requested");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("file not found");
            return;
        }

        File file = new File(filePath);

        if(!file.exists() || !file.isFile()){
            log.warn("[DownloadView] File not found. path={}, name={}", file.getAbsolutePath(), fileName);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("file not found");
            return;
        }

        String mimeType = request.getServletContext().getMimeType(file.getAbsolutePath());

        if (mimeType == null){
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setContentLengthLong(file.length());
        setDisposition(fileName, request, response);

        try(
            BufferedInputStream in		= new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream out	= new BufferedOutputStream(response.getOutputStream())
        ){
            FileCopyUtils.copy(in, out);
            out.flush();
        }
	}

    private void setDisposition(String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String browser				= request.getHeader("User-Agent");
        String dispositionPrefix	= "attachment; filename=";
        String encodedFilename		= null;

        if(browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Edge")) {
            encodedFilename	= URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        } else {
            encodedFilename	= new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }

        response.setHeader("Content-Disposition"		,dispositionPrefix + encodedFilename);
        response.setHeader("Content-Transfer-Encoding"	,"binary");
    }
}
