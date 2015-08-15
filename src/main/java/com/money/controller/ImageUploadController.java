package com.money.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by happysky on 15-8-4.
 * 图片上传
 */
@Controller
@RequestMapping("/ImageUploadController")
public class ImageUploadController extends ControllerBase implements IController {
    private static Auth auth = Auth.create("p9HKzhfunZ7r03le8JrahIEF_BsjzSkeG44VCBrJ",
            "OkRYOozlQyW3WdUm8raxRlSx-Htp0s3ELnUSmjiJ");

    @RequestMapping("/getUploadToken")
    @ResponseBody
    public String getUploadToken(HttpServletRequest request, HttpServletResponse response) {
        return getUpToken0();
    }

    @RequestMapping("/getUploadTokenUserHead")
    @ResponseBody
    public String getUploadTokenUserHead(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        return getUpTokenUserPortrait(userId);
    }

    // 简单上传，使用默认策略
    private String getUpToken0(){
        return auth.uploadToken("self");
    }

    // 覆盖上传
    private String getUpTokenUserPortrait(String userId){
        return auth.uploadToken("self", "user_portrait_" + userId);
    }

    // 设置指定上传策略
    private String getUpToken2(){
        return auth.uploadToken("bucket", null, 3600, new StringMap()
                .put("callbackUrl", "call back url").putNotEmpty("callbackHost", "")
                .put("callbackBody", "key=$(key)&hash=$(etag)"));
    }

    // 设置预处理、去除非限定的策略字段
    private String getUpToken3(){
        return auth.uploadToken("bucket", null, 3600, new StringMap()
                .putNotEmpty("persistentOps", "").putNotEmpty("persistentNotifyUrl", "")
                .putNotEmpty("persistentPipeline", ""), true);
    }
}
