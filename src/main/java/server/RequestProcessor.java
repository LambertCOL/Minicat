package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2020/4/29 14:33
 * @Creator Lambert
 */
public class RequestProcessor extends Thread {

    private Socket socket;
    /*private Map<String, HttpServlet> servletMap;

    public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap) {
        this.socket = socket;
        this.servletMap = servletMap;
    }

    @Override
    public void run() {
        try {
            Request request = new Request(socket.getInputStream());
            Response response = new Response(socket.getOutputStream());

            //对请求的url类型判断
            if (servletMap.get(request.getUrl()) == null) {
                //静态资源请求
                response.outputHtml(request.getUrl());
            } else {
                //动态资源请求
                HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request, response);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private Mapper servletMapper;

    public RequestProcessor(Socket socket, Mapper servletMapper) {
        this.socket = socket;
        this.servletMapper = servletMapper;
    }

    @Override
    public void run() {
        try {
            Request request = new Request(socket.getInputStream());
            Response response = new Response(socket.getOutputStream());

            boolean isDynamicResourceRequest = false;   //判断是否动态请求
            String url = request.getUrl();
            Wrapper w = null;

            Context[] contexts = servletMapper.getHost().getContexts();
            for (Context context : contexts) {
                if (url.startsWith(context.getName())) {
                    Wrapper[] wrappers = context.getWrappers();
                    for (Wrapper wrapper : wrappers) {
                        String wrapperUrl = wrapper.getUrl();
                        url = url.replaceFirst(context.getName(), "");
                        //url一致则是动态请求
                        if (wrapperUrl.equals(url)) {
                            isDynamicResourceRequest = true;
                            w = wrapper;
                        }
                    }
                }
            }

            if (isDynamicResourceRequest){
                //动态资源请求
                Servlet servlet = w.getServlet();
                servlet.service(request, response);
            }else {
                //静态资源请求
                response.outputHtml(request.getUrl());
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
