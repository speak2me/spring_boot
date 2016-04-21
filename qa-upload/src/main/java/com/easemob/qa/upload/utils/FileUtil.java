package com.easemob.qa.upload.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {

    public static boolean compress(String dataPath, String tenantId, int thread_index,
            String content) {
        try {
            long zipTime = System.currentTimeMillis();
            String orginFile = thread_index + "_" + zipTime;
            File parentFile = new File(dataPath + "/" + tenantId);
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            String zipFile = dataPath + "/" + tenantId + "/" + thread_index + "_" + zipTime
                    + ".zip";
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());
            ZipOutputStream outputStream = new ZipOutputStream(
                    new FileOutputStream(new File(zipFile)));
            ZipEntry zipEntry = new ZipEntry(orginFile);
            outputStream.putNextEntry(zipEntry);
            int oneByte = 0;
            while ((oneByte = inputStream.read()) != -1) {
                outputStream.write(oneByte);
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            log.error("failed in compressing the data!" + e.getMessage());
            return false;
        }
        return true;
    }

}
