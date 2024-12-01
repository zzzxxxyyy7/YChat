package com.ychat.common.User.Controller;


import com.ychat.common.Constants.front.Response.ApiResult;
import com.ychat.common.User.Domain.dto.req.IdReqVO;
import com.ychat.common.User.Domain.dto.req.UserEmojiReq;
import com.ychat.common.User.Domain.vo.IdRespVO;
import com.ychat.common.User.Domain.vo.UserEmojiResp;
import com.ychat.common.User.Services.IUserEmojiService;
import com.ychat.common.Utils.Request.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

/**
 * 用户表情包 前端控制器
 */
@RestController
@RequestMapping("/capi/user/emoji")
@Api(tags = "用户表情包管理相关接口")
public class UserEmojiController {

    /**
     * 用户表情包 Service
     */
    @Autowired
    private IUserEmojiService emojiService;

    /**
     * 表情包列表
     *
     * @return 表情包列表
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @GetMapping("/list")
    @ApiOperation("表情包列表")
    public ApiResult<List<UserEmojiResp>> getEmojisPage() {
        return ApiResult.success(emojiService.list(RequestHolder.get().getUid()));
    }


    /**
     * 新增表情包
     *
     * @param req 用户表情包
     * @return 表情包
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @PostMapping()
    @ApiOperation("新增表情包")
    public ApiResult<IdRespVO> insertEmojis(@Valid @RequestBody UserEmojiReq req) {
        return emojiService.insert(req, RequestHolder.get().getUid());
    }

    /**
     * 删除表情包
     *
     * @return 删除结果
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @DeleteMapping()
    @ApiOperation("删除表情包")
    public ApiResult<Void> deleteEmojis(@Valid @RequestBody IdReqVO reqVO) {
        emojiService.remove(reqVO.getId(), RequestHolder.get().getUid());
        return ApiResult.success();
    }

}

