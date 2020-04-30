package server;

/**
 * @Description Mapper体系结构：Mapper-> Host-> Context-> Wrapper-> Servlet
 * @Date 2020/4/29 15:54
 * @Creator Lambert
 */
public class Mapper {
    private Host host;

    public Mapper() {
    }

    public Mapper(Host host) {
        this.host = host;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }
}

class Host {
    private String name;
    private String appBase;
    private Context[] contexts;

    public Host() {}

    public Host(String name, String appBase) {
        this.name= name;
        this.appBase= appBase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    public Context[] getContexts() {
        return contexts;
    }

    public void setContexts(Context[] contexts) {
        this.contexts = contexts;
    }
}

class Context {
    private String name;
    private Wrapper[] wrappers;

    public Context() {}

    public Context(String name) {
        this.name= name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Wrapper[] getWrappers() {
        return wrappers;
    }

    public void setWrappers(Wrapper[] wrappers) {
        this.wrappers = wrappers;
    }
}

class Wrapper {
    private String url;
    private HttpServlet servlet;

    public Wrapper() {
    }

    public Wrapper(String url, HttpServlet servlet) {
        this.url = url;
        this.servlet = servlet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpServlet getServlet() {
        return servlet;
    }

    public void setServlet(HttpServlet servlet) {
        this.servlet = servlet;
    }
}