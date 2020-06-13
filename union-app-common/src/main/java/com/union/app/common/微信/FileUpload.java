package com.union.app.common.微信;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;


/**
 *
 * @author Sunlight
 *
 */
public class FileUpload {
    private static final String upload_url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
    /**
  * 上传文件
  *
  * @param accessToken
  * @param type
  * @param file
  * @return
  */


    public static Result<MdlUpload> Upload(String accessToken, String type, File file) {

        Result<MdlUpload> result = new Result<MdlUpload>();
        String url = upload_url.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
        JSONObject jsonObject;
        try {
            HttpPostUtil post = new HttpPostUtil(url);
            post.addParameter("media", file);
            String s = post.send();
            jsonObject = JSON.parseObject(s);
            if (jsonObject.containsKey("media_id")) {
                MdlUpload upload=new MdlUpload();
                upload.setMedia_id(jsonObject.getString("media_id"));
                upload.setType(jsonObject.getString("type"));
                upload.setCreated_at(jsonObject.getString("created_at"));
                result.setObj(upload);
                result.setErrmsg("success");
                result.setErrcode("0");
            } else {
                result.setErrmsg(jsonObject.getString("errmsg"));
                result.setErrcode(jsonObject.getString("errcode"));
            }
            } catch (Exception e) {
                e.printStackTrace();
                result.setErrmsg("Upload Exception:"+e.toString());
            }
            return result;
}



}