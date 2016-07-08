package org.jerry.frameworks.base.web.upload;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jerry.frameworks.base.utils.LogUtils;
import org.jerry.frameworks.base.utils.security.Coder;
import org.jerry.frameworks.base.web.upload.exception.FileNameLengthLimitExceededException;
import org.jerry.frameworks.base.web.upload.exception.InvalidExtensionException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 文件上传工具
 *
 * <p>Date : 16/5/27</p>
 * <p>Time : 上午11:52</p>
 *
 * @author jerry
 */
public class FileUploadUtils {
    /**
     * 默认上传文件大小50M
     */
    private static long defaultFileSize = 52428800;
    /**
     * 默认上传路径
     */
    private static String defaultBaseDir = "upload";

    /* √CONSTANTS DEFINE START√ */
    /**
     * 文件名最大长度
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 200;

    /**
     * 图片文件扩展名
     */
    public static final String[] IMAGE_EXTENSION = {
            "bmp", "jpeg", "gif", "jpg", "png"
    };
    /**
     * FLASH文件扩展名
     */
    public static final String[] FLASH_EXTENSION = {
            "swf", "flv"
    };
    /**
     * 多媒体文件扩展名
     */
    public static final String[] MEDIA_EXTENSION = {
            "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb"
    };

    /**
     * 默认允许上传的文件扩展名
     */
    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            //图片
            "bmp", "gif", "jpg", "jpeg", "png",
            //word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "html", "htm", "txt",
            //压缩文件
            "rar", "zip", "gz", "bz2",
            //pdf
            "pdf"
    };

    private static int counter = 0;

    public static long getDefaultFileSize() {
        return defaultFileSize;
    }

    public static void setDefaultFileSize(long defaultFileSize) {
        FileUploadUtils.defaultFileSize = defaultFileSize;
    }

    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }

    public static void setDefaultBaseDir(String defaultBaseDir) {
        FileUploadUtils.defaultBaseDir = defaultBaseDir;
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param request 当前请求
     * @param file    上传的文件
     * @param result  添加出错信息
     * @return          上传成功的文件名
     */
    public static String upload(HttpServletRequest request, MultipartFile file, BindingResult result) {
        return upload(request, file, result, DEFAULT_ALLOWED_EXTENSION);
    }


    /**
     * 以默认配置进行文件上传
     *
     * @param request          当前请求
     * @param file             上传的文件
     * @param result           添加出错信息
     * @param allowedExtension 允许上传的文件类型
     * @return                  上传成功的文件名
     */
    public static String upload(HttpServletRequest request, MultipartFile file, BindingResult result, String[] allowedExtension) {
        try {
            return upload(request, getDefaultBaseDir(), file, allowedExtension, defaultFileSize, true);
        } catch (IOException e) {
            LogUtils.logError("file upload error", e);
            result.reject("upload.server.error");
        } catch (InvalidExtensionException.InvalidImageExtensionException e) {
            result.reject("upload.not.allow.image.extension");
        } catch (InvalidExtensionException.InvalidFlashExtensionException e) {
            result.reject("upload.not.allow.flash.extension");
        } catch (InvalidExtensionException.InvalidMediaExtensionException e) {
            result.reject("upload.not.allow.media.extension");
        } catch (InvalidExtensionException e) {
            result.reject("upload.not.allow.extension");
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            result.reject("upload.exceed.maxSize");
        } catch (FileNameLengthLimitExceededException e) {
            result.reject("upload.filename.exceed.length");
        }
        return null;
    }

    /**
     * 文件上传
     *
     * @param request                       当前请求 从请求中提取 应用上下文根
     * @param baseDir                       相对应用的基目录
     * @param file                          上传的文件
     * @param allowedExtension              允许的文件类型 null 表示允许所有
     * @param maxSize                       最大上传的大小 -1 表示不限制
     * @param needDatePathAndRandomName     是否需要日期目录和随机文件名前缀
     * @return                              返回上传成功的文件名
     * @throws InvalidExtensionException
     * @throws FileUploadBase.FileSizeLimitExceededException
     * @throws IOException
     * @throws FileNameLengthLimitExceededException
     */
    public static String upload(
            HttpServletRequest request,
            String baseDir,
            MultipartFile file,
            String[] allowedExtension,
            long maxSize,
            boolean needDatePathAndRandomName)
            throws InvalidExtensionException, FileUploadBase.FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException {

        int filenameLength = file.getOriginalFilename().length();
        if (filenameLength > DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(file.getOriginalFilename(), filenameLength, DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file, allowedExtension, maxSize);

        String filename = extractFilename(file, baseDir, needDatePathAndRandomName);
        File dest = getAbsoluteFile(extractUploadDir(request), filename);
        file.transferTo(dest);
        return filename;
    }

    /**
     * 提取上传路径
     *
     * @param request   请求
     * @return          上传到服务上下文的主路径
     */
    public static String extractUploadDir(HttpServletRequest request) {
        return request.getServletContext().getRealPath("/");
    }

    /**
     * 提取文件名
     *
     * @param file                          文件
     * @param baseDir                       基础存储目录
     * @param needDatePathAndRandomName     是否要日期文件夹
     * @return                              文件名
     * @throws UnsupportedEncodingException
     */
    public static String extractFilename(MultipartFile file, String baseDir, boolean needDatePathAndRandomName)
            throws UnsupportedEncodingException {
        String filename = file.getOriginalFilename();
        int slashIndex = filename.indexOf("/");
        if (slashIndex >= 0) {
            filename = filename.substring(slashIndex + 1);
        }
        if(needDatePathAndRandomName) {
            filename = baseDir + File.separator + datePath() + File.separator + encodingFilename(filename);
        } else {
            filename = baseDir + File.separator + filename;
        }

        return filename;
    }

    /**
     * 是否允许文件上传
     *
     * @param file             上传的文件
     * @param allowedExtension 文件类型  null 表示允许所有
     * @param maxSize          最大大小 字节为单位 -1表示不限制
     * @throws InvalidExtensionException      如果MIME类型不允许
     * @throws FileUploadBase.FileSizeLimitExceededException 如果超出最大大小
     */
    public static void assertAllowed(MultipartFile file, String[] allowedExtension, long maxSize)
            throws InvalidExtensionException, FileUploadBase.FileSizeLimitExceededException {

        String filename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension, filename);
            } else if (allowedExtension == FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension, filename);
            } else if (allowedExtension == MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension, filename);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, filename);
            }
        }

        long size = file.getSize();
        if (maxSize != -1 && size > maxSize) {
            throw new FileUploadBase.FileSizeLimitExceededException("not allowed upload upload", size, maxSize);
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     */
    public static boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private static File getAbsoluteFile(String uploadDir, String filename) throws IOException {
        uploadDir = FilenameUtils.normalizeNoEndSeparator(uploadDir); //去掉结尾路径
        File file = new File(uploadDir + File.separator + filename);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 重命名文件, 将原文件名改成
     * 1.替换" "成"_";
     * 2.加密
     *
     * @param filename  原文件名
     * @return
     */
    private static String encodingFilename(String filename) {
        filename = filename.replace("_", " ");
        filename = Coder.encryptMD5(filename + System.nanoTime() + counter++) + "_" + filename;
        return filename;
    }

    private static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }
}
