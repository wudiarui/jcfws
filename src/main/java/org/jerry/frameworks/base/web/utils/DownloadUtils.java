package org.jerry.frameworks.base.web.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * <p>Date : 16/5/31</p>
 * <p>Time : 下午1:55</p>
 *
 * @author jerry
 */
public class DownloadUtils {

    public static void download(HttpServletRequest request, HttpServletResponse response, String filePath) throws IOException {
        download(request, response, filePath, "");
    }

    public static void download(HttpServletRequest request, HttpServletResponse response, String filePath, String displayName) throws IOException {
        File file = new File(filePath);

        if(StringUtils.isEmpty(displayName)) {
            displayName = file.getName();
        }

        if (!file.exists() || !file.canRead()) {
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("您下载的文件不存在！");
        } else {
            String userAgent = request.getHeader("User-Agent");
            int length = (int)file.length();

            response = getHeader(response, displayName, userAgent, length);

            BufferedInputStream is = null;
            OutputStream os = null;
            try {

                os = response.getOutputStream();
                is = new BufferedInputStream(new FileInputStream(file));
                IOUtils.copy(is, os);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    public static void download(HttpServletRequest request, HttpServletResponse response, String displayName, byte[] data)
            throws IOException {
        if (ArrayUtils.isEmpty(data)) {
            response.setContentType("text/html;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("您下载的文件不存在!");
        } else {
            String userAgent = request.getHeader("User-Agent");
            int length = data.length;

            response = getHeader(response, displayName, userAgent, length);

            BufferedInputStream is = null;
            OutputStream os = null;
            try {
                os = response.getOutputStream();
                is = new BufferedInputStream(new ByteArrayInputStream(data));
                IOUtils.copy(is, os);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    private static HttpServletResponse getHeader(HttpServletResponse response, String displayName, String userAgent, int length) {
        boolean isIE = (userAgent != null) && (userAgent.toLowerCase().contains("msie"));

        response.reset();
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "must-revalidate, no-transform");
        response.setDateHeader("Expires", 0L);

        response.setContentType("application/x-download");
        response.setContentLength(length);

        String displayFilename = displayName.substring(displayName.lastIndexOf("_") + 1);
        displayFilename = displayFilename.replace(" ", "_");
        try {
            if (isIE) {
                displayFilename = URLEncoder.encode(displayFilename, "UTF-8");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + displayFilename + "\"");
            } else {
                displayFilename = new String(displayFilename.getBytes("UTF-8"), "ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;filename=" + displayFilename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
