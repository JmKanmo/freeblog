package com.service.util.sftp;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.SftpFileHandleException;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SftpService {
    private final SftpUtil sftpUtil;

    public String sftpFileUpload(MultipartFile multipartFile) throws Exception {
        if (multipartFile.getOriginalFilename().isEmpty()) {
            throw new SftpFileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        String imgSrc = sftpUtil.fileUpload(BlogUtil.getImageFileUUID(multipartFile), multipartFile.getInputStream());
        return imgSrc;
    }
}
