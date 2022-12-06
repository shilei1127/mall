package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.util.FastDfsClient;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理类
 */

@RestController
@RequestMapping("/admin/product")
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileUrl;

    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestParam("file") MultipartFile file) {
        String s = FastDfsClient.fileUpload(file);
        if (s == null) {
            return Result.fail();
        }
        return Result.ok(fileUrl + s);
    }
}
