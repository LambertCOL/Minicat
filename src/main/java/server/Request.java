package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description 将浏览器请求信息封装成Request对象（根据InputStream输入流封装）
 * @Date 2020/4/29 11:11
 * @Creator Lambert
 */
public class Request {
    private String method;  //请求方式：比如GET、POST
    private String url; //例如 /、/index.html
    private InputStream inputStream;    //根据inputstream封装请求信息，其他Request属性都是从inputstream中获取的

    public Request() {
    }

    /**
     * 只接受Inputstream参数的构造器
     * @param inputStream
     */
    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        int available = inputStream.available();//从输入流中获取请求信息的长度，为什么？因为我得知道用多大的容器能装下那些信息
        //由于网络等因素影响，需要判断available长度不是0，如果是0证明数据还没传过来，就循环获取
        while (available == 0) {
            available = inputStream.available();
        }
        //获取到请求信息长度后，我就知道信息的大小了，用byte数组来装，然后通过输入流将信息读取到容器bytes中供后面使用
        byte[] bytes = new byte[available];
        inputStream.read(bytes);
        String inputStr = new String(bytes);
        System.out.println("===>>>请求信息：\n"+ inputStr);
        /**
         * 输出看一下，结果类似如下：
         * GET / HTTP/1.1   （要实现2.0版本需求，只需要关注第一行：GET是请求方式，和后续处理Servlet有关；/是url，表示资源路径；HTTP/1.1是协议）
         * Host: localhost:8080
         * Connection: keep-alive
         * Cache-Control: max-age=0
         * Upgrade-Insecure-Requests: 1
         * ...省略
         */

        String[] firstLineDatas = inputStr.split("\n")[0].split(" ");
        this.method = firstLineDatas[0];
        this.url = firstLineDatas[1];

    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
