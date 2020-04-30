package server;

import server.Request;

/**
 * @Description TODO
 * @Date 2020/4/29 13:30
 * @Creator Lambert
 */
public interface Servlet {

    void init() throws Exception;
    void destory() throws Exception;
    void service(Request request, Response response) throws Exception;
}
