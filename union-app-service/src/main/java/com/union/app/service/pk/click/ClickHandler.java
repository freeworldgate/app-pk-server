package com.union.app.service.pk.click;

import com.union.app.plateform.data.resultcode.PageAction;

public interface ClickHandler {


    PageAction handle(String userId, String id);

}
