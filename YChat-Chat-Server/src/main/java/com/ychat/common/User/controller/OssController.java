package com.ychat.common.User.Controller;


import com.ychat.Oss.domain.OssResp;
import com.ychat.common.Constants.front.Response.ApiResult;
import com.ychat.common.User.Domain.dto.req.UploadUrlReq;
import com.ychat.common.User.Services.OssService;
import com.ychat.common.Utils.Request.RequestHolder;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Description: oss控制层
 */
@RestController
@RequestMapping("/capi/oss")
@Api(tags = "oss相关接口")
public class OssController {

    @Autowired
    private OssService ossService;

    @Autowired
    private MinioClient minioClient;

    @GetMapping("/upload/url")
    @ApiOperation("获取临时上传链接")
    public ApiResult<OssResp> getUploadUrl(@Valid UploadUrlReq req) {
        return ApiResult.success(ossService.getUploadUrl(RequestHolder.get().getUid(), req));
    }

    @PostMapping("/upload/file")
    @ApiOperation("上传文件")
    public ApiResult<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InputStream inputStream = file.getInputStream();
        PutObjectArgs objectArgs = PutObjectArgs.builder().bucket("ychat").object(file.getOriginalFilename())
                .stream(inputStream, file.getSize(), -1).contentType(file.getContentType()).build();
        //文件名称相同会覆盖
        minioClient.putObject(objectArgs);
        return ApiResult.success();
    }

}
