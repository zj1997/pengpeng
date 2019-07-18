package cn.pengpeng.qiniu;

import com.qiniu.util.Auth;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.*;

/**
 * @author zhaojie
 * @date 2018\12\9 0009 - 16:07
 */
public class DownLoad {

    String accessKey = "suu2QQWc6hQiu0uIfkl9aSUbgtFk7RoaF2j0I8mF";      //AccessKey的值
    String secretKey = "NrexwIhzJ0VdmiSfl5_FrTW1xOhXaRO-7HZmF6Dm";      //SecretKey的值

    //密钥配置
    Auth auth = Auth.create(accessKey,secretKey);

    /**
     * 获取下载文件路径，即：donwloadUrl
     * @return
     */
    public String getDownloadUrl(String targetUrl) {
        String downloadUrl = auth.privateDownloadUrl(targetUrl);
        return downloadUrl;
    }

    /**
     * 下载
     */
    public void download(String targetUrl) {
        //获取downloadUrl
        String downloadUrl = getDownloadUrl(targetUrl);
        //本地保存路径
        String filePath = "C:\\Users\\Administrator\\Desktop\\";
        download(downloadUrl, filePath);
    }


    /**
     * 通过发送http get 请求获取文件资源
     * @param url
     * @param filepath
     * @return
     */
    private static void download(String url, String filepath) {
        OkHttpClient client = new OkHttpClient();
        System.out.println(url);
        Request req = new Request.Builder().url(url).build();
        Response resp = null;
        try {
            resp = client.newCall(req).execute();
            System.out.println(resp.isSuccessful());
            if(resp.isSuccessful()) {
                ResponseBody body = resp.body();
                InputStream is = body.byteStream();
                byte[] data = readInputStream(is);
                File imgFile = new File(filepath + "xyz.png");          //下载到本地的图片命名
                FileOutputStream fops = new FileOutputStream(imgFile);
                fops.write(data);
                fops.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unexpected code " + resp);
        }
    }

    /**
     * 读取字节输入流内容
     * @param is
     * @return
     */
    private static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 2];
        int len = 0;
        try {
            while((len = is.read(buff)) != -1) {
                writer.write(buff, 0, len);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toByteArray();
    }


    /**
     * 主函数：测试
     * @param args
     */
    public static void main(String[] args) {
        //构造私有空间的需要生成的下载的链接；
        //格式： http://私有空间绑定的域名/空间下的文件名

        String targetUrl = "http://pjgl2pbbi.bkt.clouddn.com/123.png";         //外链域名下的图片路径
        new DownLoad().download(targetUrl);
    }

}
