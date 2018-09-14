package server.security;


import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ernest Sadykov
 */
public class WebInterfaceInterceptor extends HandlerInterceptorAdapter {
    private final String partOfRequest;
    private final String redirectPath;

    /**
     *
     * @param partOfRequest если запрос содержит данную часть, то произойдет редирект.
     *                      Например, "/index": тогда перенаправляться будут запросы вида
     *                      "/index/cost.form" и "/index.form".
     * @param redirectPath путь, по которому произойдет перенаправление.
     *                     К пути приписывается contextPath.
     */
    public WebInterfaceInterceptor(String partOfRequest, String redirectPath) {
        this.partOfRequest = partOfRequest;
        this.redirectPath = redirectPath;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws Exception {
        if (request.getRequestURI().contains(partOfRequest)) {
            response.sendRedirect(request.getContextPath() + "/" + redirectPath);
            return false;
        }
        return true;
    }
}
