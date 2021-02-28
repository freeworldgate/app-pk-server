package com.union.app.entity.pk.post;

import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ComplainType {

    用户界面(1),



    ;
    private int type;



    private static Map<Integer,ComplainType> clickItemMap = new ConcurrentHashMap<>();

    ComplainType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static ComplainType valueOf(int tab)  {

        ComplainType clickItem = clickItemMap.get(tab);
        if(ObjectUtils.isEmpty(clickItem))
        {

            for(ComplainType item:ComplainType.values()){
                if(item.getType() == tab){
                    clickItemMap.put(tab,item);
                    clickItem = item;

                }
            }
            return clickItem;
        }
        else
        {
            return clickItem;

        }
    }

}
