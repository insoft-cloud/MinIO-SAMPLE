<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
    <head>
    </head>
    <body>
    <h1>MinIO에 파일 업로드 및 다운로드</h1>
        <hr>
        <form action="/upload" method="post" enctype="multipart/form-data">
            1) 파일 업로드
            <br>
            <input type="file" name="myFile">
            <input type="submit" value="업로드" />

            <hr>
            <br>
            2) 업로드된 파일 다운받기 : <a href="/download?fileName=${fileName}">${fileName}</a>
        </form>
    </body>
</html>
