package kr.co.cleaning.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.cleaning.core.config.KFException;
import javax.imageio.ImageIO;

public class FileUtil {

	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	@Value("${kframe.filePath}")
	private String filePath;

	public String getFilePath() {
		return filePath;
	}

	public List<HashMap<String,Object>> fileUpload(List<MultipartFile> files,boolean isEdit) throws Exception {

        List<HashMap<String,Object>> rs	= new ArrayList<>();
        HashMap<String,Object> map		= new HashMap<>();
        String[] strArray 				= {"jpg", "jpeg", "png", "bmp", "gif", "webp"};
        List<String> extList			= new ArrayList<>(Arrays.asList(strArray));

	    for(MultipartFile file : files){
	        if (!file.isEmpty()){
	        	String nowMm			= new SimpleDateFormat("yyyyMM").format(System.currentTimeMillis());
	        	String nowTm			= new SimpleDateFormat("hhmmss").format(System.currentTimeMillis());
	        	String uuid 			= UUID.randomUUID().toString().replaceAll("-", "");

	            String fileRealNm		= file.getOriginalFilename();
	            int lastDotIdx			= fileRealNm.lastIndexOf(".");
	            String fileExtsn 		= (lastDotIdx > -1) ? fileRealNm.substring(lastDotIdx + 1).toLowerCase() : "";
            String fileFakeNm		= nowTm+uuid+'.'+fileExtsn;
            String fileDestPath 	= SUtils.normalizePath(this.filePath + "/" + nowMm);
            if(isEdit) fileDestPath = SUtils.normalizePath(this.filePath + "/editor/" + nowMm);
            long fileSize			= file.getSize();

                // 확장자 및 MIME 타입 검증
                if(!extList.contains(fileExtsn)){
                    log.warn("확장자 검증 실패: {} (허용: {})", fileExtsn, extList);
                    throw new KFException("허용되지 않은 이미지 형식입니다. ("+fileRealNm+")");
                }
                String contentType = file.getContentType();
                // MIME Type 검증 완화: image/* 또는 application/octet-stream 허용 (확장자로 2차 검증됨)
                boolean validMimeType = contentType != null && (
                    contentType.toLowerCase().startsWith("image/") ||
                    contentType.toLowerCase().equals("application/octet-stream")
                );
                if(!validMimeType){
                    log.warn("MIME Type 검증 실패: {} (파일명: {})", contentType, fileRealNm);
                    throw new KFException("이미지 파일만 업로드 가능합니다. ("+fileRealNm+")");
                }

	            File destDir			= new File(fileDestPath);

	            if(!destDir.isDirectory()) {
	            	destDir.mkdirs();
	            }

	            File destFile			= new File(fileDestPath,fileFakeNm);

	            file.transferTo(destFile);

	            map		= new HashMap<>();
	            map.put("fileRealNm"	,fileRealNm);
	            map.put("fileFakeNm"	,fileFakeNm);
	            map.put("filePath"		,fileDestPath);
	            map.put("fileExtsn"		,fileExtsn);
	            map.put("fileSize"		,fileSize);
	            map.put("filePathEdit"	,nowMm);

	        	rs.add(map);
	        }
	    }

	    return rs;
	}

	public void deleteFile(File targetFile) throws Exception {
		if(!targetFile.exists()) return;
		if(targetFile.isDirectory()){
			for(File tFile : targetFile.listFiles()){
				deleteFile(tFile);
				tFile.delete();
			}
		}else{
			targetFile.delete();
		}
	}

    public void fileCopy(File sourceFile, File targetFile) throws Exception {
		// try-with-resources를 사용하여 리소스 자동 해제 (Java 7+)
		try (FileInputStream is = new FileInputStream(sourceFile);
			 FileOutputStream os = new FileOutputStream(targetFile);
			 FileChannel fcIn = is.getChannel();
			 FileChannel fcOut = os.getChannel()) {

			long size = fcIn.size();
			fcIn.transferTo(0, size, fcOut);

		} catch (Exception e) {
			throw new KFException("파일 복사 실패: " + e.getMessage());
		}
    }

    private static String getExt(String name){
        int idx = name.lastIndexOf('.') ;
        return idx > -1 ? name.substring(idx+1).toLowerCase() : "";
    }

    /**
     * Generate thumbnail for an image file if not exists.
     * Save under {filePath}/thumb/{fileFakeNm} and return full path.
     */
    public String generateThumbnailIfNotExists(String filePath, String fileFakeNm, int maxWidth) throws Exception {
        String safeBase = SUtils.normalizePath(filePath);
        File src = new File(safeBase, fileFakeNm);
        File thumbDir = new File(safeBase, "thumb");
        if (!thumbDir.isDirectory()) thumbDir.mkdirs();
        File dest = new File(thumbDir, fileFakeNm);

        if (dest.exists()) return dest.getAbsolutePath();
        if (!src.exists()) throw new KFException("원본 파일이 존재하지 않습니다.");

        try {
            BufferedImage input = ImageIO.read(src);
            if (input == null) {
                // Unsupported format by ImageIO: fallback copy
                fileCopy(src, dest);
                return dest.getAbsolutePath();
            }
            int ow = input.getWidth();
            int oh = input.getHeight();
            if (ow <= 0 || oh <= 0) {
                fileCopy(src, dest);
                return dest.getAbsolutePath();
            }
            int nw = Math.min(maxWidth, ow);
            int nh = (int)Math.round((double)oh * ((double)nw / (double)ow));

            int type = input.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
            BufferedImage output = new BufferedImage(nw, nh, type);
            Graphics2D g2 = output.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(input.getScaledInstance(nw, nh, Image.SCALE_SMOOTH), 0, 0, nw, nh, null);
            g2.dispose();

            String ext = getExt(fileFakeNm);
            String format = ("png".equals(ext) ? "png" : ("gif".equals(ext) ? "gif" : "jpg"));
            ImageIO.write(output, format, dest);
        } catch (Exception e) {
            // Any failure -> copy original to ensure availability
            fileCopy(src, dest);
        }

        return dest.getAbsolutePath();
    }
}
