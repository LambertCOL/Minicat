package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Description TODO
 * @Date 2020/4/29 11:28
 * @Creator Lambert
 */
public class StaticResourceUtil {
    /**
     * 获取静态资源文件的物理路径
     *
     * @param path 逻辑路径
     * @return
     */
    public static String getAbsolutePath(String path) {
        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        return absolutePath.replaceAll("\\\\", "/") + path;
    }


    /**
     * 输出静态资源文件
     *
     * @param inputStream
     * @param outputStream
     */
    public static void outputStaticResource(FileInputStream inputStream, OutputStream outputStream) throws IOException {
        int resourceSize = inputStream.available();//从输入流中获取请求信息的长度，为什么？因为我得知道用多大的容器能装下那些信息
        while (resourceSize == 0) {
            resourceSize = inputStream.available();
        }
        //输出HTTP响应头
        outputStream.write(HttpProtocolUtil.getHttpHeader200(resourceSize).getBytes());
        //输出HTTP响应体
        long written = 0;   //记录已经读取的长度
        int byteSize = 1024;    //计划每次读取的长度
        while (written < resourceSize) {    //在总资源长度还比已读取的长度大，说明还没读完，循环读
            if (written + byteSize > resourceSize) { //当总资源长度比（已读取长度+即将读取长度）小，说明可以一次读完，且这是最后一次读取
                byteSize = (int) (resourceSize - written);  //重新获取将要读取的长度
            }

            byte[] bytes = new byte[byteSize];
            inputStream.read(bytes);    //将资源从inputstream中读取到bytes
            outputStream.write(bytes);  //将读取到bytes中的数据通过outputstream输出出去
            outputStream.flush();   //别忘了刷新

            written += byteSize;  //更新已读取长度
        }
    }
}
