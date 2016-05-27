package org.jerry.frameworks.base.web.jcaptcha;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.imageio.ImageIO;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 继承Spring Filter,用于生成验证码,刷新登录验证页面或点击重新获取,就生成
 *
 * <p>Date : 16/5/26</p>
 * <p>Time : 上午8:55</p>
 *
 * @author jerry
 */
public class JCaptchaFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        /*将客户端浏览器清空缓存*/
        httpServletResponse.setDateHeader("Expires", 0L);
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpServletResponse.setHeader("Cache-Control", "post-check=0, pre-check=0");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setContentType("image/jpeg");

        String id = httpServletRequest.getRequestedSessionId();
        BufferedImage image = JCaptcha.captchaService.getImageChallengeForID(id);

        ServletOutputStream out = httpServletResponse.getOutputStream();

        ImageIO.write(image, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }
}
