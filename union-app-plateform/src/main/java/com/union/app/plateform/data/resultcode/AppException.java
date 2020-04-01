package com.union.app.plateform.data.resultcode;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppException extends Exception{


    private AppResponse appResponse;

    public AppException(AppResponse appResponse)
    {
        this.appResponse = appResponse;
    }

    public static AppException buildException(PageAction pageAction){
        AppResponse appResponse = translate(pageAction);
        return new AppException(appResponse);
    }

    private static AppResponse translate(PageAction pageAction) {

        AppResponse response = new AppResponse();
        response.set_0_title(pageAction.get_0_title());
        response.set_0_message(pageAction.get_0_message());
        response.set_action_type(pageAction.get_action_type());
        response.set_1_message(pageAction.get_1_message());
        response.set_1_message_level(pageAction.get_1_message_level());
        response.set_2_data_sets(pageAction.get_2_data_sets());
        response.set_3_isneed_userId(pageAction.is_3_isneed_userId());
        response.set_3_url(pageAction.get_3_url());
        response.set_4_handler_name(pageAction.get_4_handler_name());
        response.set_4_data(pageAction.get_4_data());
        return response;



    }


}
