package server.common;

/**
 * Created by ezybarev on 14.11.2017.
 */

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;
import server.Constants;
import server.exception.CommonException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by ezybarev on 02.11.2016.
 */
public class Utils {

    public static boolean isEmpty(String str) {
        return !(null != str && str.length() > 0);
    }

    public static boolean isEmpty(Integer i) {
        boolean result = true;
        if (null != i && i > 0) {
            result = false;
        }
        return result;
    }


    public static boolean isEmpty(List array) {
        boolean result = true;
        if (null != array && array.size() > 0) {
            result = false;
        }
        return result;
    }

    public static boolean isEmpty(Map map) {
        boolean result = true;
        if (null != map && map.size() > 0) {
            result = false;
        }
        return result;
    }


    public static boolean isEmpty(String[] str) {
        if (str != null && str.length > 0) {
            for (String aStr : str)
                if (!Utils.isEmpty(aStr))
                    return false;
            return true;
        } else
            return true;
    }


    public static boolean isEmpty(MultipartFile file) {
        return !(null != file && !file.isEmpty());
    }


    public static boolean isEmpty(byte[] arr) {
        boolean result = true;
        if (null != arr && arr.length > 0) {
            result = false;
        }
        return result;
    }

    public static void downloadFile(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String type, byte[] data, String name) throws CommonException {
        OutputStream os = null;
        try {
            response.reset();

            if(name.toUpperCase().endsWith("PS"))
                response.setContentType("application/octet-stream");
            else
                response.setContentType(type);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(data);
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            if(data != null)
                response.setContentLength(data.length);
            else
                response.setContentLength(baos.size());
            response.setHeader("Set-Cookie", Constants.COOKIE_FILEDOWNLOAD + "=true;path=/");

            String userAgent = request.getHeader("User-Agent");
            String encodedFileName;

            if (!userAgent.contains("Firefox")) {
                encodedFileName = URLEncoder.encode(name, "UTF-8");
            } else {
                encodedFileName = "=?UTF-8?B?" + new String(org.apache.commons.codec.binary.Base64.encodeBase64(name.getBytes("UTF-8")), "UTF-8") + "?=";
            }
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");


            //response.setHeader("Content-Disposition", disposition);
            os = response.getOutputStream();
            baos.close();
            if (data != null)
                IOUtils.write(data, os);
            else if (data != null)
                baos.writeTo(os);
            else
                throw new CommonException("File data is null");
            os.flush();
        } catch (IOException e) {
            throw new CommonException(e);
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void downloadImage(HttpServletResponse response,
                                     String type,
                                     byte[] data) throws CommonException {
        try {
            response.setContentType("image/" + type);
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setHeader("Set-Cookie", Constants.COOKIE_FILEDOWNLOAD + "=true;path=/");
            response.setHeader("Content-Disposition", "inline;filename=\"image." + type + "\"");
            OutputStream os = response.getOutputStream();
            IOUtils.write(data, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    public static void handleDownloadException(HttpServletResponse response, Exception e) throws CommonException {
        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(e.toString());
            e.printStackTrace(out);
            out.flush();
            out.close();
        } catch (IOException e1) {
            throw new CommonException(e1);
        }
    }

    public static boolean hasRole(Authentication auth, String role) {
        return hasAnyRole(auth,new String[] {role});
    }

    public static boolean hasAnyRole(Authentication auth,String[] roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        if (auth != null) {
            for (GrantedAuthority ga : auth.getAuthorities()) {
                for (String role : roles) {
                    if (ga.getAuthority().contains(role))
                        return true;
                }
            }
        }
        return false;
    }

    public static String getMd5Hash(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(data, 0, data.length);
            return new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (Exception ignored) {}
        return null;
    }
}
