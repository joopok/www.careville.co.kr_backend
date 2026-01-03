package kr.co.cleaning.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import kr.co.cleaning.core.config.KFException;
import javax.imageio.ImageIO;

public class FileUtil {

	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	@Value("${kframe.filePath}")
	private String filePath;

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	/**
	 * 애플리케이션 시작 시 기본 파일 저장 디렉토리 생성 (755 권한)
	 */
	@PostConstruct
	public void init() {
		try {
			String os = System.getProperty("os.name");
			String user = System.getProperty("user.name");
			String userHome = System.getProperty("user.home");
			String env = "prod".equalsIgnoreCase(activeProfile) ? "운영(Production)" : "개발(Development)";

			log.info("============================================");
			log.info("  파일 저장 설정 정보");
			log.info("  - 활성 프로필: {} ({})", activeProfile, env);
			log.info("  - 설정된 파일 경로: {}", filePath);
			log.info("  - OS: {}", os);
			log.info("  - 실행 사용자: {}", user);
			log.info("  - 사용자 홈: {}", userHome);
			log.info("============================================");

			if (filePath != null && !filePath.isEmpty()) {
				File baseDir = new File(filePath);

				// 경로 상태 확인
				log.info("파일 저장 경로 상태 확인:");
				log.info("  - 절대 경로: {}", baseDir.getAbsolutePath());
				log.info("  - 존재 여부: {}", baseDir.exists());

				if (!baseDir.exists()) {
					// 부모 디렉토리 체인 확인
					File parent = baseDir.getParentFile();
					while (parent != null) {
						log.info("  - 상위 디렉토리 [{}]: 존재={}, 쓰기가능={}",
							parent.getAbsolutePath(), parent.exists(), parent.canWrite());
						if (parent.exists()) break;
						parent = parent.getParentFile();
					}

					boolean created = createDirectoryWithPermissions(baseDir);
					if (created) {
						log.info("파일 저장 디렉토리 생성 완료: {}", filePath);
					} else {
						log.error("!!! 파일 저장 디렉토리 생성 실패 !!!");
						log.error("해결 방법: SSH로 서버 접속 후 다음 명령어 실행:");
						log.error("  mkdir -p {} && chmod 755 {}", filePath, filePath);
					}
				} else {
					log.info("  - 디렉토리 여부: {}", baseDir.isDirectory());
					log.info("  - 쓰기 가능: {}", baseDir.canWrite());
					log.info("  - 읽기 가능: {}", baseDir.canRead());

					if (!baseDir.canWrite()) {
						log.error("!!! 파일 저장 디렉토리 쓰기 권한 없음 !!!");
						log.error("해결 방법: SSH로 서버 접속 후 다음 명령어 실행:");
						log.error("  chmod 755 {}", filePath);
					} else {
						log.info("파일 저장 디렉토리 확인 완료: {}", filePath);
					}
				}
			}
		} catch (Exception e) {
			log.error("파일 저장 디렉토리 초기화 실패: {}", e.getMessage(), e);
		}
	}

	public String getFilePath() {
		return filePath;
	}

	/**
	 * 디렉토리 생성 및 755 권한 부여 (Unix/Linux 환경)
	 */
	private boolean createDirectoryWithPermissions(File dir) {
		try {
			Path path = dir.toPath();

			// 부모 디렉토리 확인 및 생성
			File parentDir = dir.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				log.info("부모 디렉토리가 없어서 생성 시도: {}", parentDir.getAbsolutePath());
				if (!parentDir.mkdirs()) {
					// mkdirs 실패 시 Files.createDirectories로 재시도
					try {
						Files.createDirectories(parentDir.toPath());
						log.info("부모 디렉토리 생성 성공: {}", parentDir.getAbsolutePath());
					} catch (Exception pe) {
						log.error("부모 디렉토리 생성 실패: {} - 원인: {}", parentDir.getAbsolutePath(), pe.getMessage());
						// 부모 디렉토리 쓰기 권한 확인
						File grandParent = parentDir.getParentFile();
						if (grandParent != null) {
							log.error("상위 디렉토리 정보: 경로={}, 존재={}, 쓰기가능={}",
								grandParent.getAbsolutePath(), grandParent.exists(), grandParent.canWrite());
						}
						throw pe;
					}
				}
			}

			// 디렉토리 생성 (mkdirs 먼저 시도 후 Files.createDirectories)
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Files.createDirectories(path);
				}
			}
			log.info("디렉토리 생성/확인 완료: {}", dir.getAbsolutePath());

			// Unix/Linux 환경에서만 권한 설정 (Windows는 스킵)
			String os = System.getProperty("os.name").toLowerCase();
			if (!os.contains("win")) {
				try {
					Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
					Files.setPosixFilePermissions(path, perms);
					log.debug("디렉토리 권한 755 설정: {}", path);
				} catch (UnsupportedOperationException e) {
					// Windows 등 POSIX 미지원 시스템
					log.debug("POSIX 권한 미지원 시스템: {}", os);
				}
			}
			return true;
		} catch (Exception e) {
			log.error("====== 디렉토리 생성 실패 상세 ======");
			log.error("대상 경로: {}", dir.getAbsolutePath());
			log.error("오류 유형: {}", e.getClass().getName());
			log.error("오류 메시지: {}", e.getMessage());

			// 부모 디렉토리 상태 확인
			File parent = dir.getParentFile();
			if (parent != null) {
				log.error("부모 디렉토리: {}", parent.getAbsolutePath());
				log.error("  - 존재 여부: {}", parent.exists());
				log.error("  - 디렉토리 여부: {}", parent.isDirectory());
				log.error("  - 쓰기 가능: {}", parent.canWrite());
				log.error("  - 읽기 가능: {}", parent.canRead());
			}
			log.error("=====================================");
			return false;
		}
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

                // ========== 파일 업로드 전 경로 정보 로깅 ==========
                log.info("┌─────────────────────────────────────────────────────────────┐");
                log.info("│               [ 파일 업로드 경로 정보 ]                      │");
                log.info("├─────────────────────────────────────────────────────────────┤");
                log.info("│ 설정된 기본 경로 (filePath): {}", this.filePath);
                log.info("│ 계산된 저장 경로 (fileDestPath): {}", fileDestPath);
                log.info("│ 저장될 파일명: {}", fileFakeNm);
                log.info("│ 원본 파일명: {}", fileRealNm);
                log.info("│ 파일 크기: {} bytes", fileSize);

                File checkDir = new File(fileDestPath);
                File checkBase = new File(this.filePath);
                log.info("├─────────────────────────────────────────────────────────────┤");
                log.info("│ 기본 경로 존재 여부: {}", checkBase.exists());
                log.info("│ 기본 경로 쓰기 가능: {}", checkBase.canWrite());
                log.info("│ 저장 경로 존재 여부: {}", checkDir.exists());
                log.info("│ 저장 경로 쓰기 가능: {}", checkDir.exists() ? checkDir.canWrite() : "N/A");
                log.info("│ 최종 파일 전체 경로: {}/{}", fileDestPath, fileFakeNm);
                log.info("│ DB 저장용 경로: {}", fileDestPath);
                log.info("└─────────────────────────────────────────────────────────────┘");
                // ========================================================

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
	            	log.info("디렉토리가 없어서 생성 시도: {}", fileDestPath);
	            	boolean dirCreated = createDirectoryWithPermissions(destDir);
	            	if (!dirCreated) {
	            		log.error("파일 업로드 실패: 디렉토리 생성 불가 - {}", fileDestPath);
	            		throw new KFException("파일 저장 디렉토리를 생성할 수 없습니다. 서버 관리자에게 문의하세요. (경로: " + fileDestPath + ")");
	            	}
	            	log.info("디렉토리 생성 성공: {}", fileDestPath);
	            }

	            // 디렉토리 쓰기 권한 재확인
	            if (!destDir.canWrite()) {
	            	log.error("파일 업로드 실패: 디렉토리 쓰기 권한 없음 - {}", fileDestPath);
	            	throw new KFException("파일 저장 디렉토리에 쓰기 권한이 없습니다. 서버 관리자에게 문의하세요.");
	            }

	            File destFile			= new File(fileDestPath,fileFakeNm);

	            log.info("파일 저장 시작: {}", destFile.getAbsolutePath());
	            file.transferTo(destFile);
	            log.info("파일 저장 완료: {} (존재: {})", destFile.getAbsolutePath(), destFile.exists());

	            map		= new HashMap<>();
	            map.put("fileRealNm"	,fileRealNm);
	            map.put("fileFakeNm"	,fileFakeNm);
	            map.put("filePath"		,fileDestPath);
	            map.put("fileExtsn"		,fileExtsn);
	            map.put("fileSize"		,fileSize);
	            map.put("filePathEdit"	,nowMm);

	            log.info("DB 저장용 데이터: filePath={}, fileFakeNm={}", fileDestPath, fileFakeNm);
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
        if (!thumbDir.isDirectory()) createDirectoryWithPermissions(thumbDir);
        File dest = new File(thumbDir, fileFakeNm);

        if (dest.exists()) return dest.getAbsolutePath();
        if (!src.exists()) {
            log.warn("원본 파일이 존재하지 않습니다: {}", src.getAbsolutePath());
            return null;
        }

        try {
            BufferedImage input = ImageIO.read(src);
            if (input == null) {
                // Unsupported format by ImageIO: fallback copy
                try {
                    fileCopy(src, dest);
                    return dest.getAbsolutePath();
                } catch (Exception copyEx) {
                    log.warn("썸네일용 파일 복사 실패: {}", copyEx.getMessage());
                    return null;
                }
            }
            int ow = input.getWidth();
            int oh = input.getHeight();
            if (ow <= 0 || oh <= 0) {
                try {
                    fileCopy(src, dest);
                    return dest.getAbsolutePath();
                } catch (Exception copyEx) {
                    log.warn("썸네일용 파일 복사 실패: {}", copyEx.getMessage());
                    return null;
                }
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
            // Any failure -> try to copy original, but handle failure gracefully
            log.warn("썸네일 생성 중 오류 발생: {}", e.getMessage());
            try {
                fileCopy(src, dest);
            } catch (Exception copyEx) {
                log.warn("원본 파일 복사 실패 (파일이 없거나 접근 불가): {}", copyEx.getMessage());
                return null;
            }
        }

        return dest.getAbsolutePath();
    }
}
