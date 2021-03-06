package com.union.app.api.OSS;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.domain.Oss上传.OssUrlInfo;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.response.ApiResponse;
import com.union.app.service.pk.service.AppService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping(path="/oss")
public class OSSSave {

//
    @Autowired
    AppService appService;


    private static String policy = AppService.getPolicy();
    private String signature = AppService.getSignature(AppConfigService.getConfigAsString(ConfigItem.RAM秘钥));



    @RequestMapping(path="/getUrl",method = RequestMethod.GET)
    public AppResponse 获取临时URL( @RequestParam(value="type")String type) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {


        OssUrlInfo ossUrlInfo = new OssUrlInfo();
        ossUrlInfo.setAliyunServerURL(AppConfigService.getConfigAsString(ConfigItem.OSS基础地址));
        ossUrlInfo.setDirectory(获取前缀(type));
        ossUrlInfo.setPolicyBase64(policy);
        ossUrlInfo.setOSSAccessKeyId(AppConfigService.getConfigAsString(ConfigItem.RAM秘钥ID));
        ossUrlInfo.setSignature(signature);


        ossUrlInfo.setPrefix("wx");




        ossUrlInfo.setTaskId(UUID.randomUUID().toString());

        return AppResponse.buildResponse(PageAction.执行处理器("success",ossUrlInfo));
    }
    private static final Stack<String> names = new Stack<>();
    private String 获取前缀(String type) {
        String ip;
        try {
            ip = StringUtils.replace(InetAddress.getLocalHost().getHostAddress(),".","_");
        } catch (UnknownHostException e) {
            ip = UUID.randomUUID().toString();
        }
        String dir = "dir" + new Random().nextInt(200);
        String path = ip+"/"+type+"/"+dir;
        return path.replaceAll("-","");
    }

//    @RequestMapping(path="/postUploadImgs",method = RequestMethod.GET)
//    public ApiResponse 上传报告( @RequestParam(value="taskId")String taskId,@RequestParam(value="files")String[] files)
//    {
//        List<String> filePaths = new ArrayList<>();
//
//        for(String file:files){
//            String myfile = file.replace("[","").replace("]","").replace("\"","");
//            String filePath = "https://oss.211shopper.com/" + myfile;
//            filePaths.add(filePath);
//        }
//
//
//        return ApiResponse.buildSuccessResponse(filePaths);
//    }




}
