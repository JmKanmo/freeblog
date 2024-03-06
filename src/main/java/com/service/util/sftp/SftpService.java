package com.service.util.sftp;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.FileHandleException;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SftpService {
    private final SftpUtil sftpUtil;

    public String sftpFileUpload(MultipartFile multipartFile, Object hash, Object id) throws Exception {
        if (multipartFile.getOriginalFilename().isEmpty()) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        String fileSrc = sftpUtil.fileUpload(BlogUtil.getFileUUIDBySftp(multipartFile), multipartFile.getInputStream(), getDirectoryType(hash), String.valueOf(hash), String.valueOf(id), BlogUtil.formatLocalDateTimeToStrByPattern(LocalDateTime.now(), "yyyy-MM-dd"));
        return fileSrc;
    }

    public void sftpFileDelete(String fileSrc) throws Exception {
        if (fileSrc == null || fileSrc.isEmpty()) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        String[] parsed = parsedSftpFileSrc(fileSrc);
        sftpUtil.deleteFile(parsed[1], parsed[2], parsed[3], parsed[4], parsed[5]);
    }

    private String[] parsedSftpFileSrc(String fileSrc) {
        String[] parsed = fileSrc.split("://");

        if (parsed.length <= 1) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        parsed = parsed[1].split("/");

        if (parsed.length < 6) {
            throw new FileHandleException(ServiceExceptionMessage.NOT_VALID_FILE_NAME);
        }

        return parsed;
    }

    private String getDirectoryType(Object objHash) {
        try {
            Integer hash = (Integer) objHash;

            if (hash == ConstUtil.POST_VIDEO_HASH) {
                return "videos";
            } else if (hash == ConstUtil.SFTP_POST_THUMBNAIL_HASH
                    || hash == ConstUtil.SFTP_POST_IMAGE_HASH
                    || hash == ConstUtil.SFTP_COMMENT_IMAGE_HASH
                    || hash == ConstUtil.SFTP_PROFILE_THUMBNAIL_HASH) {
                return "images";
            }
            return ConstUtil.UNDEFINED;
        } catch (Exception e) {
            return ConstUtil.UNDEFINED;
        }
    }
}
