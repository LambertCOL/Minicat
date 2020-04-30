package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Description Minicat的主类
 * @Date 2020/4/29 10:21
 * @Creator Lambert
 */
public class Bootstrap {

    //监听端口，Tomcat的端口配置是写在配置文件中的，这里直接写在主类
    private int port = 0;

    private Mapper mapper;

    /**
     * 服务器启动后要有一些初始化操作，比如监听端口。这些初始化工作统一写到 start 方法中
     * Minicat 启动需要初始化展开的一些操作
     * 1.0版本：实现浏览器请求http://localhost:8080，返回一个固定的字符串“Hello，Minicat！”到页面
     * 2.0版本：封装Request和Response对象，处理静态资源请求
     * 3.0版本：处理动态资源请求
     * 4.0版本：多线程-无线程池版
     * 5.0版本：多线程-线程池版
     */
    public void start() throws Exception {
        //要处理动态资源请求servlet，在启动时就要先对servlet进行加载
        //loadServlet();

        //读取server.xml配置文件进行加载
        loadServerConfig();

        //Socket监听
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("===>>>Minicat start on port:" + port);

        /*1.0版本：实现浏览器请求http://localhost:8080，返回一个固定的字符串“Hello，Minicat！”到页面*/
        /*while (true) {  //阻塞式监听端口，就是一直监听
            Socket socket = serverSocket.accept();
            //接收到请求
            OutputStream outputStream = socket.getOutputStream();//获取输出流，目的是完成1.0版本任务：往外输出
            String responseData = "Hello,Minicat!"; //响应体数据
            String responseHeader = HttpProtocolUtil.getHttpHeader200(responseData.length());   //获取相应头
            outputStream.write((responseHeader + responseData).getBytes());
            socket.close();
        }*/

        /*2.0版本：封装Request和Response对象，处理静态资源请求。要处理资源请求，就要知道客户端请求的是什么资源，这个信息是有请求头告诉我的，所以我需要用一个输入流inputstream获取请求头*/
        /*while (true) {
            Socket socket = serverSocket.accept();
            Request request = new Request(socket.getInputStream());
            Response response = new Response(socket.getOutputStream());
            response.outputHtml(request.getUrl());
            socket.close();
        }*/

        /*3.0版本：处理动态资源请求*/
        /*while (true) {
            Socket socket = serverSocket.accept();
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
        }*/

        /*4.0版本：多线程-无线程池版本*/
        /*while (true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket, servletMap);
            requestProcessor.start();
        }*/

        /*5.0版本：多线程-线程池版*/
        //定义一个线程池
        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

        /*while (true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket, servletMap);
            threadPoolExecutor.execute(requestProcessor);
        }*/

        /*6.0版本：模拟webapps部署*/
        while (true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket, mapper);
            threadPoolExecutor.execute(requestProcessor);
        }

    }

    /**
     * 加载配置文件server.xml
     */
    private void loadServerConfig() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            Element connector = (Element) rootElement.selectSingleNode("/Server/Service/Connector");    //找Connector
            String port = connector.attribute("port").getValue();   //获取监听端口
            this.port = Integer.parseInt(port); //设置监听端口

            /**
             * <Server>
             *     <Service>
             *         <Connector port="8080" />
             *         <Engine>
             *             <Host name="localhost" appBase="D:\Code\My_Tomcat\Minicat\webapps"></Host>
             *         </Engine>
             *     </Service>
             * </Server>
             */
            Element hostElement = (Element) rootElement.selectSingleNode("/Server/Service/Engine/Host");    //找Host
            String name = hostElement.attribute("name").getValue();
            String appBase = hostElement.attribute("appBase").getValue();

            Host host = new Host(name, appBase);
            //加载每个项目中的servlet并封装到Mapper
            Context[] contexts = loadCustomizedServlet(appBase);
            host.setContexts(contexts);
            this.mapper = new Mapper(host);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Context[] loadCustomizedServlet(String webappsPath) {
        File appBaseDir = new File(webappsPath);    //获取到项目的物理路径
        File[] webappsDir = appBaseDir.listFiles();
        Context[] contexts = new Context[webappsDir.length];
        for (int i = 0; i < webappsDir.length; i++) {
            File webDir = webappsDir[i];
            String contextName = webDir.getName();
            Context context = new Context("/" + contextName); //每个部署在webapps下的项目都起一个独立的上下文

            //获取webDir的所有class文件
            List<Servlet> servlets = getServlets(webDir);
            Wrapper[] wrappers = new Wrapper[servlets.size()];
            for (int j = 0; j < servlets.size(); j++) {
                Servlet servlet = servlets.get(j);
                String url = servlet.getClass().getPackage().getName().replaceFirst(contextName, "").replaceAll("\\.", "\\/");
                Wrapper wrapper = new Wrapper(url, servlet);
                wrappers[j] = wrapper;
            }
            context.setWrappers(wrappers);
            contexts[i] = context;
        }

        return contexts;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param webappsDir
     */
    public List getServlets(File webappsDir) {
        List<Servlet> servlets = new ArrayList<>();
        try {
            // 获取此包的目录 建立一个File
            File[] webPrjs = webappsDir.listFiles();
            // 循环所有文件
            for (int i = 0; i < webPrjs.length; i++) {
                File prj = webPrjs[i];
                String prjPath = prj.getPath();
                // 如果是class文件
                if (prj.isFile()) {
                    if (prjPath.substring(prjPath.lastIndexOf(".")).equalsIgnoreCase(".class")) {
                        // 添加到集合中去
                        Servlet servlet = (HttpServlet) Class.forName(prjPath.split("\\webapps\\\\")[1].replaceAll("\\\\", ".").replaceAll(".class", "")).newInstance();
                        servlets.add(servlet);
                    }
                } else {
                    //如果是目录则递归
                    servlets.addAll(getServlets(prj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return servlets;
    }


    private Map<String, HttpServlet> servletMap = new HashMap<>(); //存储Servlet映射关系，<url，Servlet>

    /**
     * 加载Servlet
     */
    private void loadServlet() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (Element element : selectNodes) {
                Element servletNameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletNameElement.getStringValue();
                Element servletClassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletClassElement.getStringValue();
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass).newInstance());
            }
        } catch (DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Minicat的程序启动入口
     *
     * @param args
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            //启动Minicat
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
