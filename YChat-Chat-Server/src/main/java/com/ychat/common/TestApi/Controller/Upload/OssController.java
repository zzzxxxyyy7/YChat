package com.ychat.common.TestApi.Controller.Upload;

import com.ychat.Oss.MinIOTemplate;
import com.ychat.Oss.domain.OssReq;
import com.ychat.Oss.domain.OssResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/oss")
public class OssController {

    @Resource
    private MinIOTemplate minIOTemplate;

    @GetMapping("/public/uploadFile")
    public void uploadFile() {
        OssReq ossReq = OssReq.builder()
                .fileName("C:\\Users\\Rhss\\Desktop\\后端开发.pdf")
                .filePath("/test")
                .autoPath(false)
                .build();
        OssResp preSignedObjectUrl = minIOTemplate.getPreSignedObjectUrl(ossReq);
        System.out.println(preSignedObjectUrl);
    }

}
