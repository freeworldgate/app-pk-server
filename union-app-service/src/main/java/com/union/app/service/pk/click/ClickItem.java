package com.union.app.service.pk.click;

import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ClickItem {

    用户界面(1,true),

    收藏PK(2,true),

    分享PK(3,true),

    打榜记录(4,true),

    立榜记录(5,true),

    榜帖页面(6,false),

    PK礼品详情页面(7,false),

    PK贡献榜(8,false),
    POST贡献榜(9,false),
    选择城市(10,false),


    ;
    private int clickId;

    private boolean loginStatu;

    private static Map<Integer,ClickItem> clickItemMap = new ConcurrentHashMap<>();

    ClickItem(int clickId, boolean loginStatu) {
        this.clickId = clickId;
        this.loginStatu = loginStatu;

    }



    public int getClickId() {
        return clickId;
    }

    public void setClickId(int clickId) {
        this.clickId = clickId;
    }

    public boolean isLoginStatu() {
        return loginStatu;
    }

    public void setLoginStatu(boolean loginStatu) {
        this.loginStatu = loginStatu;
    }

    public static ClickItem valueOf(int tab) throws AppException {

        ClickItem clickItem = clickItemMap.get(tab);
        if(ObjectUtils.isEmpty(clickItem))
        {

            for(ClickItem item:ClickItem.values()){
                if(item.getClickId() == tab){
                    clickItemMap.put(tab,item);
                    clickItem = item;

                }
            }
            if(ObjectUtils.isEmpty(clickItem)){
                throw AppException.buildException(PageAction.信息反馈框("","操作异常"));
            }
            else {
                return clickItem;
            }
        }
        else
            {
            return clickItem;

        }
    }


}
