package com.insoft.minio.controller;

import com.insoft.minio.service.MinIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * minIO Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2021.07.02
 **/
@RestController
public class MinIOController {
    private final String folderPath = "2021";

    private final MinIOService minIOService;

    @Autowired
    public MinIOController(MinIOService minIOService) {
        this.minIOService = minIOService;
    }


    /**
     * 테스트용 - minIO의 해당 bucket 아래 폴더 생성
     *
     */
    @PostMapping("/bucket")
    public void createFolder() {
        minIOService.createFolder(folderPath);
    }

    /**
     * 테스트용 - minIO의 해당 bucket 아래 폴더 삭제
     *
     */
    @DeleteMapping("/bucket")
    public void deleteFolder() {
        minIOService.deleteFolder("/" + folderPath);
    }

    /**
     * 테스트용 - minIO에 파일 존재하는 지 조회
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    @GetMapping("/existedFile")
    public boolean existFile(@RequestParam String fileName) throws Exception {
        return minIOService.existFile("/2021", fileName);
    }

}
