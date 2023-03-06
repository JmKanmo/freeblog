package com.service.util.sftp;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.FileHandleException;
import com.service.util.BlogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SftpService {
    private final SftpUtil sftpUtil;

    public String sftpImageFileUpload(MultipartFile multipartFile) throws Exception {
        if (multipartFile.getOriginalFilename().isEmpty()) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        String imgSrc = sftpUtil.fileUpload(BlogUtil.getImageFileUUIDBySftp(multipartFile), multipartFile.getInputStream(), "images");
        return imgSrc;
    }
}
