MinIO FileService 를 이용한 파일 관리
---
## Spec
- SpringBoot 2.5.2
- Gradle 6.8.3
- MinIO 8.0.3


### 1. build.gradle 에 Dependency 추가
- MinIO Client API 구현된 jar 파일을 import (/libs/fileservice-0.3.0-SNAPSHOT.jar)
```
[build.gradle]

// minIO Client library
implementation group: 'io.minio', name: 'minio', version: '8.0.3'
compile files('libs/fileservice-0.3.0-SNAPSHOT.jar')
```

### 2. Bean 등록
- MinIO Client API 사용하기 위해 MinioFileService 를 Bean으로 등록한다.
```
* com.insoft.minio > config > MinIOConfig.java 클래스 참고
```

### 3. MinIO Server 접근 정보 정의
- resources 아래 application.yml 에 MinIO Server 연동을 위한 정보를 설정한다.
- 아래는 예제 이므로 각 시스템에 맞게 설정하여야 한다.
```
minio:
  endPoint: http://[MinIO_Server_URI]:[port]
  accessKey: xxxxx
  secretKey: xxxxx
  bucketName: [bucket 명]
```

- 각각의 프로퍼티 항목은 아래 표에서 설명한다.

<table>
<thead>
<tr><th>Key</th><th>Value</th></tr>
</thead>
<tbody>
<tr>
<td>minio.endPoint</td>
<td>MinIO 서버의 접속 URL</td>
</tr>
<tr>
<td>minio.accessKey</td>
<td>MinIO 서버의 AccessKey. 사용자 ID라고 생각하면 쉽다.</td>
</tr>
<tr>
<td>minio.secretKey</td>
<td>MinIO 서버 접근 비밀번호.</td>
</tr>
<tr>
<td>minio.bucketName</td>
<td>Root directory 이름</td>
</tr>
</tbody>
</table>



### 4. 파일 저장

- MinioFileService는 MultipartFile, File, InputStream 등을 전달 받아 MinIO에 저장한다.

```
    /**
     * 파일을 fileName으로 저장한다.
     * @param folderPath 파일을 저장할 경로
     * @param fileName 저장할 파일 명
     * @param file 저장할 파일
     * @throws Exception 예외 전파
     */
    void saveAsFile(String folderPath, String fileName, MultipartFile file) throws Exception;

    /**
     * 파일을 인수로 전달된 filePath에 포함된 파일명으로 저장한다.
     * @param filePath 파일명을 포함한 전체 경로
     * @param file 저장할 파일
     */
    void saveAsFile(String filePath, MultipartFile file) throws Exception;
    
    /**
     * 파일을 인수로 전달된 fileName으로 저장한다.
     * @param folderPath 파일을 저장할 경로
     * @param fileName 파일명
     * @param file 저장 할 파일
     * @throws Exception 예외 전파
     */
    void saveAsFile(String folderPath, String fileName, File file) throws Exception;
    
    /**
     * 파일을 인수로 전달된 filePath에 포함된 파일명으로 저장한다.
     * @param filePath 파일명을 포함한 파일을 저장할 경로
     * @param file 저장 할 파일
     * @throws Exception 예외 전파
     */
    void saveAsFile(String filePath, File file) throws Exception;
    
    /**
     * 파일의 InputStream을 인수로 받아 인수로 전달된 fileName으로 저장한다.
     * @param folderPath 파일을 저장할 경로
     * @param fileName 저장할 파일명
     * @param inputStream 저장할 파일의 InputStream
     * @throws Exception 예외 전파
     */
    void saveAsFile(String folderPath, String fileName, InputStream inputStream) throws Exception;
    
    /**
     * 파일의 InputStream을 인수로 받아 인수로 전달된 filePath에 포함된 파일명으로 저장한다.
     * @param filePath 파일을 저장할 경로(파일명 포함)
     * @param inputStream 저장할 파일의 InputStream
     * @throws Exception 예외 전파
     */
    void saveAsFile(String filePath, InputStream inputStream) throws Exception;

```

### 5. 파일 추출

* MinioFileService는 byte array, InputStream 등의 형태로 MinIO에서 파일을 추출한다.

```
    /**
     * 특정 파일을 byte 배열로 추출한다.
     * @param folderPath 대상 폴더 경로
     * @param fileName 대상 파일명
     * @return 파일을 byte 배열 반환
     * @throws Exception 예외 전파
     */
    byte[] getBytes(String folderPath, String fileName) throws Exception;

    /**
     * 특정 파일을 byte 배열로 추출한다.
     * @param filePath 파일명을 포함한 전체 경로
     * @return 파일을 byte 배열로 반환
     * @throws Exception 예외 전파
     */
    byte[] getBytes(String filePath) throws Exception;

    /**
     * 특정 파일의 InputStream를 추출한다.
     * @param folderPath 대상 폴더 경로
     * @param fileName 대상 파일명
     * @return 파일의 InputStream을 반환
     * @throws Exception 예외 전파
     */
    InputStream getInputStream(String folderPath, String fileName) throws Exception;

    /**
     * 특정 파일의 InputStream를 추출한다.
     * @param filePath 파일명을 포함한 전체 경로
     * @return 파일의 InputStream을 반환
     * @throws Exception 예외 전파
     */
    InputStream getInputStream(String filePath) throws Exception;
```

### 6. 로컬 테스트
```
1) http://localhost:7777 접속
2) 메인 화면에서 파일 선택 후 오른쪽 '업로드' 버튼 클릭
3) 업로드 후 바로 업로드된 파일 확인
4) 링크된 파일 이름 클릭 시 다운로드 완료
```
