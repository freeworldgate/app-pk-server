package com.union.app.plateform.data.resultcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ToString
@AllArgsConstructor
public enum ResultCode
{

    E03000001("0x03000000","Success!"),
    E03000002("0x03000002","用户未注册"),
    E03000003("0x03000003","没有更多数据"),
    E03000004("0x03000004","本房间是会员制，用户是非会员"),
    E03000005("0x03000005","不存在该房间"),
    E03000006("0x03000006","房间人数已满"),
    E03000007("0x03000007","余额不足"),







    E03001001("0x03001001","用户不存在"),
    E03001002("0x03001002","验证码错误"),

    E03002001("0x03002001","新创建的Meet已存在"),

    E03002002("0x03002002","Meet不存在"),

    //--------------标签------------------
    E03003001("0x03003001","标签不存在"),

    //----------------联谊------------------
    E03004001("0x03004001","联谊过期"),
    E03004002("0x03004002","联谊取消"),
    E03004003("0x03004003","管理员不再接受报名"),
    E03004004("0x03004004","用户已经是成员，无法重复报名"),
    //------------------user-------------------,
    E03005001("0x03005001","余额不足,创建联谊订单"),
    E03005002("0x03005002","已经存在订单"),
    E03005003("0x03005003","用户已经报名成功，不能重复报名"),
    E03005004("0x03005004","订单不存在"),
    E03005005("0x03005005","当前状态无法取消"),
    E03005006("0x03005006","创建代理订单进行支付"),
    E03005007("0x03005007","使用微信接口进行支付"),
    E03005008("0x03005008","用户审核已经通过，不可取消"),
    E03005009("0x03005009","用户是创建者，无法报名操作"),
    E03005010("0x03005010","联谊非活动状态，无法取消活动"),
    E03005011("0x03005011","活动报名已经截止，无法报名"),




    //------------------权限---------------------
    E03006001("0x03006001","用户无权限"),

    /*系统级别错误码*/
    E05000001("0x05000001","Service方法声明异常，非静态，非抽象方法，public类型 "),

    E05000002("0x05000002","Service类注册异常"),

    E99999999("0x99999999","Internal Error!"),




    ;


    private String resultCode;

    private String resultMsg;


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
