package com.atguigu.gmall.product.util;

import com.atguigu.gmall.common.result.Result;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;



public class FastDfsClient {
    static {
        //1.加载resources目录下的配置文件
        ClassPathResource resource = new ClassPathResource("tracker.conf");
        //2.初始化dfs对象
        try {
            ClientGlobal.init(resource.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param file
     * @return
     */
    public static String fileUpload(MultipartFile file){
        try {
            //3.初始化tracker对象
            TrackerClient trackerClient = new TrackerClient();
            //4.获取tracker中的server信息
            TrackerServer trackerServer = trackerClient.getConnection();
            //5.通过tracker获取storage信息
            StorageClient storageClient = new StorageClient(trackerServer,null);
            //6.使用storage完成文件上传，获取结果
            /**
             * upload_file的三个参数：1.文件内容（getBytes字节码文件） 2.文件的拓展名 3.其他参数（拍摄地点，人物等等）
             */
            String[] strings = storageClient.upload_file(file.getBytes(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()),
                    null);
            //返回文件的地址：[0]=文件的组名，[1]=全量路径名
            return strings[0]+"/"+strings[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
