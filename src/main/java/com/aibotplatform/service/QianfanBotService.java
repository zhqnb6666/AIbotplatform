package com.aibotplatform.service;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import org.springframework.stereotype.Service;

@Service
public class QianfanBotService {
    //这两个key最好是从环境变量中读取
    private final String ak = "uMF5PVIQDQYY58QZJ0J04XrF";
    private final String sk = "zzNMgEl8pDpDBEQLVpawuQLRzRnYkVh1";
    private final Qianfan qianfan = new Qianfan(Auth.TYPE_OAUTH, ak, sk);

    public QianfanBotService(){}

    public Object getQianFan(){
        return qianfan;
    }

}
