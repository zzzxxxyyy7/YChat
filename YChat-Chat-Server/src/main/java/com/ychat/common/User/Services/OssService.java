package com.ychat.common.User.Services;


import com.ychat.Oss.domain.OssResp;
import com.ychat.common.User.Domain.dto.req.UploadUrlReq;

/**
 * oss 服务类
 */
public interface OssService {

    /**
     * 获取临时的上传链接
     */
    OssResp getUploadUrl(Long uid, UploadUrlReq req);

}
