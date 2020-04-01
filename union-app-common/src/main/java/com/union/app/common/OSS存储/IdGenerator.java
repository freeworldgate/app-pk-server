package com.union.app.common.OSS存储;

import com.union.app.plateform.storgae.redis.RedisStringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class IdGenerator {



    @Value("${spring.application.nodeId}")
    private String nodeId;

    private List<String> allNodes;
    @Autowired
    OssStorage ossStorage;

    @Autowired
    CacheStorage cacheStorage;

    @Autowired
    RedisStringUtil redisStringUtil;


    @PostConstruct
    public void initNode(){

        //注册本节点到OSS上
        System.out.println(nodeId);
        redisStringUtil.setList("ALL_NODES_REGISTORY",nodeId);
    }


    /**
     * 获取所有节点
     * @return
     * @throws IOException
     */
    public List<String> getAllNodes() throws IOException {
        String nodeStatu = cacheStorage.getKey("NODE_STATU",String.class);
        if(StringUtils.equalsIgnoreCase(nodeStatu,"true")){
            allNodes = redisStringUtil.getList("ALL_NODES_REGISTORY");
        }
        return allNodes;
    }


    /**
     * Seq-{{pkId}}-postId
     * Seq-{{posId}}-commentId
     * Seq-{{posId}}-viewId
     * Seq-{{posId}}-shareId
     *
     * PK/PK001/POST/PK001_POST001
     * PK/PK001/COMMENT/PK001_NODE1_POST001/COMMENT01
     * PK/PK001/VIEW/PK001_NODE1_POST001/VIEW01
     * PK/PK001/SHARE/PK001_NODE1_POST001/SHARE01
     * @param pkId
     * @return
     */
    public String getPostId(String pkId){
//
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append(pkId);
//        stringBuffer.append("_");
//        stringBuffer.append(nodeId);
//        stringBuffer.append("_");
//        stringBuffer.append();
//


        return "";
    };


    /**
     * @param postId
     * @return
     */
    public String getCommentId(String postId){ return ""; };

    /**
     * @param postId
     * @return
     */
    public String getViewId(String postId){ return ""; };

    /**
     * @param postId
     * @return
     */
    public String getShareId(String postId){ return ""; };


    public String getCurrentPostId(String pkId){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("PK");
        stringBuffer.append("/");
        stringBuffer.append(pkId);
        stringBuffer.append("/");
        stringBuffer.append("POST");
        stringBuffer.append("/");
        stringBuffer.append(pkId);
        stringBuffer.append("_");
        stringBuffer.append(nodeId);
        stringBuffer.append("_");
        stringBuffer.append("POST");
        String currentId = ossStorage.getCurrentTopId(stringBuffer.toString());
        return null;
    }
    public String getCurrentCommentId(String postId){return null;}
    public String getCurrentViewId(String postId){return null;}
    public String getCurrentShareId(String postId){return null;}





}






