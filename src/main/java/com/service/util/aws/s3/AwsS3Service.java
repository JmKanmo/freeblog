package com.service.util.aws.s3;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.FileHandleException;
import com.service.util.BlogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AwsS3Service {
    private final AwsS3Util awsS3Util;

    public String uploadImageFile(MultipartFile multipartFile) throws Exception {
        if (multipartFile.getOriginalFilename().isEmpty()) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        String imgSrc = awsS3Util.storeImageFile(BlogUtil.getFileUUID(multipartFile), multipartFile.getInputStream(), BlogUtil.initObjectMetaData(multipartFile));
        return imgSrc;
    }
}
