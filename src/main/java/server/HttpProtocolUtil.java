package server;

/**
 * @Description http协议工具类，主要是提供响应头信息，这里只提供200成功和404的情况
 * @Date 2020/4/29 10:43
 * @Creator Lambert
 */
public class HttpProtocolUtil {

    /**
     * 为响应码为200提供请求头
     * @param contentLength
     * @return
     */
    public static String getHttpHeader200(long contentLength) {
        return "HTTP/1.1 200 OK \n" +
                "Content-Type:text/html \n" +
                "Content-Length: " + contentLength + "\n"
                + "\r\n";
    }

    /**
     * 为响应码为404提供请求头
     * @return
     */
    public static String getHttpHeader404() {
        String str404 = "<h1>404 NOT FOUND</h1>";
        return "HTTP/1.1 404 NOT FOUND \n" +
                "Content-Type:text/html \n" +
                "Content-Length: " + str404.length() + "\n"
                + "\r\n" + str404;
    }
}
