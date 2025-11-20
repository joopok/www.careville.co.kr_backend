package kr.co.cleaning.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import kr.co.cleaning.core.config.KFException;

public class FileUtil {

	@Value("${kframe.filePath}")
	private String filePath;

	public String getFilePath() {
		return filePath;
	}

	public List<HashMap<String,Object>> fileUpload(List<MultipartFile> files,boolean isEdit) throws Exception {

		List<HashMap<String,Object>> rs	= new ArrayList<>();
		HashMap<String,Object> map		= new HashMap<>();
		String[] strArray 				= {"jpg", "png", "bmp", "gif"};
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

				if(!extList.contains(fileExtsn)){
					throw new KFException("이미지 파일(jpg,gif,png,bmp)만 업로드 하실 수 있습니다. ("+fileRealNm+")");
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

	public void fileCopy(File sourceFile,File targetFile) throws Exception {
		FileInputStream is		= null;
		FileOutputStream os		= null;
		FileChannel fcIn		= null;
		FileChannel fcOut		= null;
		long size				= 0;

		try{
			is		= new FileInputStream(sourceFile);
			os		= new FileOutputStream(targetFile);
			fcIn	= is.getChannel();
			fcOut	= os.getChannel();
			size	= fcIn.size();

			fcIn.transferTo(0, size, fcOut);

		}catch(Exception e){
			throw e;

		}finally {
			try{if(fcIn != null) fcIn.close();}catch(Exception e1){}
			try{if(fcOut != null) fcOut.close();}catch(Exception e2){}
			try{if(os != null) os.close();}catch(Exception e3){}
			try{if(is != null) is.close();}catch(Exception e4){}
		}
	}
}
