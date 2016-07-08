package org.jerry.frameworks.base.web.upload.exception;

import org.apache.commons.fileupload.FileUploadException;

/**
 * 文件名超长
 *
 * <p>Date : 16/5/27</p>
 * <p>Time : 上午11:58</p>
 *
 * @author jerry
 */
public class FileNameLengthLimitExceededException extends FileUploadException {
    private int length;
    private int maxLength;
    private String fileName;

    public FileNameLengthLimitExceededException(String fileName, int length, int maxLength) {
        super("file name: [" + fileName + "], length: [" + length + "], maxLength: [" + maxLength + "].");
        this.fileName = fileName;
        this.length = length;
        this.maxLength = maxLength;
    }

    public int getLength() {
        return length;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getFileName() {
        return fileName;
    }
}
