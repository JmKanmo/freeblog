package com.service.util.sftp;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.FileHandleException;
import com.service.util.BlogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SftpService {
    private final SftpUtil sftpUtil;

    public String sftpImageFileUpload(MultipartFile multipartFile, Object hash, Object id) throws Exception {
        if (multipartFile.getOriginalFilename().isEmpty()) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        String imgSrc = sftpUtil.fileUpload(BlogUtil.getImageFileUUIDBySftp(multipartFile), multipartFile.getInputStream(), "images", String.valueOf(hash), String.valueOf(id), BlogUtil.formatLocalDateTimeToStrByPattern(LocalDateTime.now(), "yyyy-MM-dd"));
        return imgSrc;
    }

    public void sftpImageFileDelete(String imgSrc) throws Exception {
        if (imgSrc == null || imgSrc.isEmpty()) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        String[] parsed = parsedSftpImgSrc(imgSrc);
        sftpUtil.deleteFile(parsed[1], parsed[2], parsed[3], parsed[4], parsed[5]);
    }

    private String[] parsedSftpImgSrc(String imgSrc) {
        String[] parsed = imgSrc.split("://");

        if (parsed.length <= 1) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        parsed = parsed[1].split("/");

        if (parsed.length < 6) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        return parsed;
    }
}
