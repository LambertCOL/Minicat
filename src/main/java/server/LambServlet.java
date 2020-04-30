package server;

import server.HttpServlet;
import server.Request;
import server.Response;

import java.io.IOException;

/**
 * @Description TODO
 * @Date 2020/4/29 13:34
 * @Creator Lambert
 */
public class LambServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {
        try {
            Thread.sleep(100000);   //假设处理某个请求的耗时很长
            System.out.println("执行了 GET 方法");
            String content = "<h1>LambServlet GET</h1>";
            response.output(HttpProtocolUtil.getHttpHeader200(content.length())+content);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        try {
            System.out.println("执行了 POST 方法");
            String content = "<h1>LambServlet POST</h1>";
            response.output(HttpProtocolUtil.getHttpHeader200(content.length())+content);
        } catch (IOException e) {
            e.printStackTrace();
        }
   }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void destory() throws Exception {

    }
}
