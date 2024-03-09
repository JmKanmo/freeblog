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
    public String uploadFile(String fileUUID, InputStream inputStream, String type, String hash, String id, String date) throws Exception {
        try {
            StringBuilder dir = new StringBuilder();

            // 대상폴더 경로 이동
            dir.append(sFtpConfig.getDirectory() + "/" + type);
            channelSftp.cd(dir.toString());

            // hash 경로 이동&생성
            if (!checkDir(hash)) {
                channelSftp.mkdir(hash);
            }
            dir.append("/" + hash);
            channelSftp.cd(hash);

            // id 경로 이동&생성
            if (!checkDir(id)) {
                channelSftp.mkdir(id);
            }
            dir.append("/" + id);
            channelSftp.cd(id);

            // date 경로 이동&생성
            if (!checkDir(date)) {
                channelSftp.mkdir(date);
            }
            dir.append("/" + date);
            channelSftp.cd(date);

            // UUID 파일 생성
            dir.append("/" + fileUUID);
            channelSftp.put(inputStream, dir.toString());

            return dir.substring(sFtpConfig.getDirectory().length() + 1);
        } catch (Exception e) {
            throw e;
        } finally {
            close((FileInputStream) inputStream);
        }
    }

    /**
     * 파일 삭제
     *
     * @param type     (images | videos)
     * @param hash     (hash)
     * @param id       (unique key)
     * @param date     (date)
     * @param fileUUID
     * @throws Exception
     */
    /*
     * TODO 추후에 image/video 등과 같은 File CDN 도입, (frontend,backend) 코드 적용 전까지는 synchronized 적용해서 다음 오류를 방지하도록 한다.
     * TODO 이후에 해당 코드는 폐기/미사용 처리 (아마도)
     */
    public synchronized void deleteFile(String type, String hash, String id, String date, String fileUUID) throws Exception {
        connectSFTP();

        String dir = sFtpConfig.getDirectory() + "/" + type + "/" + hash + "/" + id + "/" + date;
        channelSftp.cd(dir);

        try {
            if (channelSftp.ls(dir + "/" + fileUUID).size() > 0) {
                channelSftp.rm(dir + "/" + fileUUID);

                if (channelSftp.ls(dir).size() <= 2) {
                    channelSftp.rmdir(dir);
                }

                dir = sFtpConfig.getDirectory() + "/" + type + "/" + hash + "/" + id;
                channelSftp.cd(dir);
                if (channelSftp.ls(dir).size() <= 2) {
                    channelSftp.rmdir(dir);
                }

                dir = sFtpConfig.getDirectory() + "/" + type + "/" + hash;
                channelSftp.cd(dir);
                if (channelSftp.ls(dir).size() <= 2) {
                    channelSftp.rmdir(dir);
                }

                dir = sFtpConfig.getDirectory() + "/" + type;
                channelSftp.cd(dir);
                if (channelSftp.ls(dir).size() <= 2) {
                    channelSftp.rmdir(dir);
                }
            }
            disconnectSFTP();
        } catch (SftpException sftpException) {
            throw sftpException;
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

    private boolean checkDir(String dir) throws Exception {
        try {
            return channelSftp.stat(dir).isDir();
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * TODO 추후에 image/video 등과 같은 File CDN 도입, (frontend,backend) 코드 적용 전까지는 synchronized 적용해서 다음 오류를 방지하도록 한다.
     * TODO 이후에 해당 코드는 폐기/미사용 처리 (아마도)
     */
    public synchronized String fileUpload(String fileUUID, InputStream fileInputStream, String type, String hash, String id, String date) throws Exception {
        connectSFTP();
        String url = String.format(ConstUtil.SFTP_IMAGE_URL, sFtpConfig.getProtocol(), sFtpConfig.getUrl(), uploadFile(fileUUID, fileInputStream, type, hash, id, date));
        disconnectSFTP();
        return url;
    }
}
