package com.insoft.minio.service;

import kr.go.smes.fileservice.MinioFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * minIO Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2021.07.02
 **/
@Service
public class MinIOService {
    private final Logger logger = LoggerFactory.getLogger(MinIOService.class);
    private final MinioFileService minioFileService;

    private static final String fixMsg = "파일이 존재 하지 않습니다.";

    @Autowired
    public MinIOService(MinioFileService minioFileService) {
        this.minioFileService = minioFileService;
    }


    /**
     * minIO에 파일 존재하는 지 조회
     *
     * @param folderPath the folder path
     * @param fileName the file name
     * @return boolean
     * @throws Exception
     */
    public boolean existFile(String folderPath, String fileName) throws Exception {
        return minioFileService.exists(folderPath, fileName);
    }

    /**
     * minIO의 해당 bucket 아래 파일 삭제
     *
     * @param filePath
     * @param fileName
     */
    public void deleteFile(String filePath, String fileName) {
        try {
            minioFileService.deleteFile(filePath, fileName);
        } catch (Exception e) {
            logger.info("Exception error when delete files : " + e.getMessage());
        }
    }


    /**
     * minIO의 해당 bucket 아래 폴더 생성
     *
     * @param folderPath
     */
    public void createFolder(String folderPath) {
        try {
            minioFileService.createFolder(folderPath);
        } catch (Exception e) {
            logger.info("Exception error when create folder : " + e.getMessage());
        }
    }

    /**
     * minIO의 해당 bucket 아래 폴더 삭제
     *
     * @param folderPath the bucket's folder path
     */
    public void deleteFolder(String folderPath) {
        try {
            minioFileService.deleteFolder(folderPath);
        } catch (Exception e) {
            logger.info("Exception error when delete folder : " + e.getMessage());
        }
    }

    /**
     * minIO에 파일 업로드
     *
     * @param folderPath the folder path
     * @param fileName the file name
     * @param file the file
     */
    public void uploadFile(String folderPath, String fileName, MultipartFile file) {
        try {
            minioFileService.saveAsFile(folderPath, fileName, file);
        } catch (Exception e) {
            logger.info("Exception error when uploading files : " + e.getMessage());
        }
    }

    /**
     * minIO에서 파일 다운로드
     *
     * @param response the httpServletResponse
     * @param folderPath the folder path
     * @param fileName the file name
     */
    public void downloadFile(HttpServletResponse response, String folderPath, String fileName) {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = minioFileService.getInputStream(folderPath, fileName);
            out = response.getOutputStream();
            FileCopyUtils.copy(in, out);

            out.flush();

        } catch (Exception e) {
            logger.info("Exception error when downloading files : " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignore) {
                    logger.error(ignore.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ignore) {
                    logger.error(ignore.getMessage());
                }
            }
        }
    }

    public String downloadFilesFromMinIO(HttpServletRequest request, HttpServletResponse response, String filename, String storedFilePath) {
        String msg = "";

        try {

            String fileName = removeJumpFileName(filename);
            System.out.println("fileName :: " + fileName);

            boolean existFile = existFile(storedFilePath, fileName);

            if (existFile) {
                String mimetype = "application/x-msdownload";
                response.setContentType(mimetype);
                setDisposition(filename, request, response);

                try {
                    downloadFile(response, storedFilePath, fileName);

                } catch (Exception ex) {
                    logger.info(ex.getMessage());
                }

            } else {
                // 파일이 존재 안함
                msg = fixMsg;

            }
        } catch(Exception ex) {
            logger.info(ex.getMessage());
            msg = fixMsg;
        }

        return msg;
    }

    /**
     * 브라우저 구분 얻기
     *
     * @param request
     * @return
     */
    private String getBrowser(HttpServletRequest request) {
        String header = request.getHeader("User-Agent");

        if (header.indexOf("MSIE") > -1 || header.indexOf("Trident") > -1) {
            return "MSIE";
        } else if (header.indexOf("Chrome") > -1) {
            return "Chrome";
        } else if (header.indexOf("Opera") > -1) {
            return "Opera";
        }
        return "Firefox";
    }

    /**
     * Disposition 지정하기
     *
     * @param filename
     * @param request
     * @param response
     * @throws Exception
     */
    private void setDisposition(String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String browser = getBrowser(request);

        String dispositionPrefix = "attachment; filename=";
        String encodedFilename = null;

        if (browser.equals("MSIE")) {
            encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.equals("Firefox")) {
            encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Opera")) {
            encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Chrome")) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < filename.length(); i++) {
                char c = filename.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else {
            throw new IOException("Not supported browser");
        }

        response.setHeader("Content-Disposition", dispositionPrefix + encodedFilename);

        if ("Opera".equals(browser)){
            response.setContentType("application/octet-stream;charset=UTF-8");
        }
    }

    /**
     * 파일이름 문자열에 경로를 벗어나게 하는 코드를 삭제 시킨다.
     * @param fileName
     * @return
     */
    public static String removeJumpFileName(String fileName) {
        String rtnFileName = "";
        rtnFileName = fileName.replace("\\", "");
        rtnFileName = rtnFileName.replace("/", "");
        return rtnFileName;
    }
}
