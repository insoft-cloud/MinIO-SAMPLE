package com.insoft.minio.controller;

import com.insoft.minio.service.MinIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * 파일 업로드 및 다운로드 controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2021.07.02
 **/
@Controller
public class FileUploadController {
    private final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private final MinIOService minIOService;
    private final String folderPath = "/2021";

    @Autowired
    public FileUploadController(MinIOService minIOService) {
        this.minIOService = minIOService;
    }

    /**
     * main 화면 이동
     *
     * @return String
     */
    @GetMapping("/")
    public String mainPage() {
        return "test";
    }


    /**
     * minIO에 파일 업로드
     *
     * @param request the httpServletRequest
     * @return String
     */
    @PostMapping("/upload")
    public String uploadFile(HttpServletRequest request) {
        String url = "";

        try {
            MultipartHttpServletRequest mptRequest = (MultipartHttpServletRequest)request;
            Iterator<String> fileIter = mptRequest.getFileNames();

            while (fileIter.hasNext()) {
                MultipartFile mFile = mptRequest.getFile(fileIter.next());

                String tmp = mFile.getOriginalFilename();
                if (tmp.lastIndexOf("\\") >= 0) {
                    tmp = tmp.substring(tmp.lastIndexOf("\\") + 1);
                }

                //파일생성
                if (mFile.getSize() > 0) {
                    // 업로드 가능 확장자
                    String[] extWhiteNames = {"xls", "xlsx", "doc", "docx","ppt", "pptx", "hwp","pdf","zip", "txt", "tiff","gif","bmp","png", "jpg","jpeg" };
                    String fileExt = tmp.substring(tmp.lastIndexOf(".")).toLowerCase().replaceAll("\\.", ""); //.을 없애고  확장자 소문자로
                    boolean canUploadExt = false;

                    for(int i = 0 ; i < extWhiteNames.length; i++) {
                        if(extWhiteNames[i].equals(fileExt)) {
                            canUploadExt = true;
                        }
                    }

                    if(!canUploadExt)
                        logger.info("Can not save file type Exception : " + fileExt);

                    minIOService.uploadFile(folderPath, tmp, mFile);

                }

                url = "/files?fileName=" + tmp;
            }
        } catch(Exception e) {
            throw e;
        }

        return "redirect:" + url;
    }


    /**
     * 업로드된 파일명 가지고 메인 페이지 이동
     *
     * @param fileName the file name
     * @return ModelAndView
     */
    @GetMapping("/files")
    public ModelAndView getFiles(@RequestParam String fileName) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("fileName", fileName);
        mv.setViewName("test");

        return mv;
    }


    /**
     * 업로드된 파일 다운로드
     *
     * @param request the httpServletRequest
     * @param response the httpServletResponse
     * @param fileName the file name
     */
    @GetMapping("/download")
    public void downloadFiles(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileName) {
        minIOService.downloadFilesFromMinIO(request, response, fileName, folderPath);
    }
}
