package com.ychat.common.User.Services.Impl;


import com.ychat.Oss.MinIOTemplate;
import com.ychat.Oss.domain.OssReq;
import com.ychat.Oss.domain.OssResp;
import com.ychat.common.Constants.Enums.Impl.OssSceneEnum;
import com.ychat.common.User.Domain.dto.req.UploadUrlReq;
import com.ychat.common.User.Services.OssService;
import com.ychat.common.Utils.Assert.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OssServiceImpl implements OssService {

    @Autowired
    private MinIOTemplate minIOTemplate;

    /**
     * 获取临时上传 OSS 地址
     * @param uid
     * @param req
     * @return
     */
    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum sceneEnum = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(sceneEnum, "OSS 类型有误");
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(sceneEnum.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }

}
