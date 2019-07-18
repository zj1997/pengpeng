package cn.pengpeng.qiniu;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

/**
 * @author zhaojie
 * @date 2018\12\9 0009 - 15:53
 */
public class UpLoad {

    public static void main(String[] args) {
        Configuration cfg = new Configuration(Zone.zone0());                //zong1() 代表华北地区
        UploadManager uploadManager = new UploadManager(cfg);

        String accessKey = "suu2QQWc6hQiu0uIfkl9aSUbgtFk7RoaF2j0I8mF";      //AccessKey的值
        String secretKey = "NrexwIhzJ0VdmiSfl5_FrTW1xOhXaRO-7HZmF6Dm";      //SecretKey的值
        String bucket = "pengpeng";                                          //存储空间名
        String localFilePath = "C:\\Users\\Administrator\\Desktop\\12345.pdf";     //上传图片路径

        String key = "lixiao.pdf";                                               //在七牛云中图片的命名
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            System.out.println(putRet.key);
            System.out.println(putRet.hash);

        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }


}
