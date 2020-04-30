package server;

import server.Request;

/**
 * @Description TODO
 * @Date 2020/4/29 13:32
 * @Creator Lambert
 */
public abstract class HttpServlet implements Servlet {

    public abstract void doGet(Request request, Response response);

    public abstract void doPost(Request request, Response response);

    @Override
    public void service(Request request, Response response) throws Exception {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }
}
