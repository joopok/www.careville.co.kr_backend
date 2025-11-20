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

        File file		= new File(SUtils.nvl(model.get("filePath")));
        String fileName = SUtils.nvl(model.get("fileName"));

        if(!file.exists() || !file.isFile()){
            throw new FileNotFoundException("file not found error.");
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
