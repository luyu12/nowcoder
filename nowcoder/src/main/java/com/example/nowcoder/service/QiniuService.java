package com.example.nowcoder.service;

import com.alibaba.fastjson.JSONObject;
import com.example.nowcoder.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);

    String accessKey = "4474SnQBvyWmKlJK9wy1TOYVVUZYr1cFmfLiE5Vv";
    String secretKey = "qEeSp2NHw2Fz4GBBJE91awsUyjuBRvCksPF-kFiQ";
    String bucketname = "nowcoder";

    //密钥配置
    Auth auth = Auth.create(accessKey, secretKey);

    Configuration cfg = new Configuration(Zone.zoneNa0()

    );

    UploadManager uploadManager=new UploadManager(cfg);


    public String getUpToken() {
        return auth.uploadToken(bucketname);
    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                //格式不符合jpep,jpg,bmp,png
                return null;
            }
            //保存图片到本地文件夹
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;

            //调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());

            //打印返回的信息
            System.out.println(res.toString());

            if(res.isOK()&&res.isJson()){
                String key=JSONObject.parseObject(res.bodyString()).get("key").toString();
                return ToutiaoUtil.QINIU_DOMAIN_PREFIX+key;
            }else{
                logger.error("上传出错"+res.bodyString());
                return null;
            }

        } catch (QiniuException e) {
            logger.error("七牛异常" + e.getMessage());
            return null;
        }

    }

}
