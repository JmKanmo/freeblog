package com.service.util.sftp;

import com.jcraft.jsch.*;
import com.service.config.sftp.SFtpConfig;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;


@Component
@RequiredArgsConstructor
public class SftpUtil {
    private Session jschSession;
    private Channel channel;
    private ChannelSftp channelSftp;
    private final SFtpConfig sFtpConfig;

    /**
     * 파일 업로드
     *
     * @param fileName
     * @throws Exception
     */
//    public void uploadFile(String fileName) throws Exception {
//        FileInputStream fis = null;
//
//        try {
//            // 대상폴더 이동
//            channelSftp.cd(sFtpConfig.getDirectory());
//
//            File file = new File(fileName);
//            fis = new FileInputStream(file);
//
//            // 파일 업로드
//            channelSftp.put(fis, file.getName());
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            close(fis);
//        }
//    }

    /**
     * 파일 업로드
     *
     * @param fileUUID
     * @param inputStream
     * @throws Exception
     */
    public String uploadFile(String fileUUID, InputStream inputStream, String type) throws Exception {
        try {
            // 대상폴더 이동
            channelSftp.cd(sFtpConfig.getDirectory() + "/" + type);
            String dir = BlogUtil.formatLocalDateTimeToStrByPattern(BlogUtil.nowByZoneId(), "yyyy-MM-dd");

            try {
                if (channelSftp.stat(dir).isDir()) {
                    channelSftp.put(inputStream, dir + "/" + fileUUID);
                }
            } catch (Exception e) {
                channelSftp.mkdir(dir);
                channelSftp.put(inputStream, dir + "/" + fileUUID);
            }
            return dir + "/" + fileUUID;
        } catch (Exception e) {
            throw e;
        } finally {
            close((FileInputStream) inputStream);
        }
    }

    /**
     * SFTP 접속하기
     *
     * @return
     * @throws JSchException
     * @throws Exception
     */
    public void connectSFTP() throws Exception {
        // JSch 객체를 생성
        JSch jsch = new JSch();

        // JSch 세션 객체를 생성 (사용자 이름, 접속할 호스트, 포트 전달)
        jschSession = jsch.getSession(sFtpConfig.getId(), sFtpConfig.getIp(), sFtpConfig.getPort());

        // 패스워드 설정
        jschSession.setPassword(sFtpConfig.getPassword());

        // 기타설정 적용
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);

        // 접속
        jschSession.connect();

        // sftp 채널 열기
        channel = jschSession.openChannel("sftp");

        // sftp 채널 연결
        channelSftp = (ChannelSftp) channel;
        channelSftp.connect();
    }

    /**
     * SFTP 접속해제
     */
    public void disconnectSFTP() {
        try {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            channelSftp = null;
        }

        try {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            channel = null;
        }

        try {
            if (jschSession != null && jschSession.isConnected()) {
                jschSession.disconnect();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            jschSession = null;
        }
    }

    /**
     * FileInputStream 객체 닫기
     *
     * @param fis
     */
    private void close(FileInputStream fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * BufferedInputStream 객체 닫기
     *
     * @param bis
     */
    private void close(BufferedInputStream bis) {
        if (bis != null) {
            try {
                bis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * FileOutputStream 객체 닫기
     *
     * @param fos
     */
    private void close(FileOutputStream fos) {
        if (fos != null) {
            try {
                fos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * BufferedOutputStream 객체 닫기
     *
     * @param bos
     */
    private void close(BufferedOutputStream bos) {
        if (bos != null) {
            try {
                bos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String fileUpload(String fileUUID, InputStream fileInputStream, String type) throws Exception {
        connectSFTP();
        return String.format(ConstUtil.SFTP_IMAGE_URL, sFtpConfig.getIp(), uploadFile(fileUUID, fileInputStream, type));
    }
}
