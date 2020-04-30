package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Description 将服务器响应信息封装成 Response 对象（根据OutputStream输出流封装）
 * 2.0版本：输出静态资源html
 * @Date 2020/4/29 11:19
 * @Creator Lambert
 */
public class Response {

    private OutputStream outputStream;

    public Response() {
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * 根据url获取静态资源的物理路径，根据物理路径读取静态资源文件，最终通过输出流输出
     *
     * @param path 请求信息Request的url
     */
    public void outputHtml(String path) throws IOException {
        //分两步走，1.获取物理路径
        path = path.startsWith("/") ? path.substring(1) : path;
        String fileAbsolutePath = StaticResourceUtil.getAbsolutePath(path);
        //2.
        File file = new File(fileAbsolutePath);
        if (file.exists() && file.isFile()) {
            //存在资源就读取静态资源文件并输出
            StaticResourceUtil.outputStaticResource(new FileInputStream(file), outputStream);
        } else {
            //不存在或者不是文件就输出404不存在
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }

    public void output(String content) throws IOException {
        outputStream.write(content.getBytes());
    }
}
