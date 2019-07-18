package cn.pengpeng.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.File;

/**
 * @author zhaojie
 * @date 2018\12\9 0009 - 17:01
 */
public class FileService {

    private static final String accessKey = "suu2QQWc6hQiu0uIfkl9aSUbgtFk7RoaF2j0I8mF";      //AccessKey的值
    private static final String secretKey = "NrexwIhzJ0VdmiSfl5_FrTW1xOhXaRO-7HZmF6Dm";      //SecretKey的值
    private static final String bucket = "pengpeng";  //存储空间名
    private static final String link_url = "http://pjgl2pbbi.bkt.clouddn.com/";              //文件外链地址


    /**
     * 文件上传
     * urlPath为上传文件的路径
     * fileName为上传到七牛云上的文件名称
     * @return
     */

    public static String upload(File faceFile, String fileName){

        Configuration cfg = new Configuration(Zone.zone0());                //zong0() 代表华东地区
        UploadManager uploadManager = new UploadManager(cfg);


        Auth auth = Auth.create(accessKey, secretKey);

        String upToken = auth.uploadToken(bucket);

        String successUrl = null;

        try {

            Response response = uploadManager.put(faceFile, fileName+".png", upToken);

            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            successUrl = link_url+putRet.key;


        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }

        return successUrl;
    }



}
