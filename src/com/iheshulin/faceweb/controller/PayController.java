package com.iheshulin.faceweb.controller;

import com.iheshulin.faceweb.dao.User;
import com.iheshulin.faceweb.util.MD5;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by HeShulin on 2017/12/8.
 */
@IocBean
public class PayController {

    private Log log = Logs.get();
    private MD5 md5= MD5.getMd5();

    @Inject
    Dao dao;

    @Ok("json")
    @Fail("http:500")
    @At("/dopay")
    @GET
    public Object dopay(@Param("userphone")String userPhone,@Param("money")String money, HttpServletRequest request){
        try{
            NutMap re = new NutMap();
            if(userPhone!=null) {
                User u = dao.fetch(User.class, Cnd.where("userphone", "=", userPhone));
                if (u!=null) {
                    u.setMoney(u.getMoney()+Integer.parseInt(money));
                    dao.update(u);
                    re.put("state", 1);
                    re.put("msg", "OK");
                    re.put("id", u.getId());
                    HttpSession session = request.getSession();
                    session.setAttribute("userphone", userPhone);
                } else {
                    re.put("state", 0);
                    re.put("msg", "账号或密码错误");
                }
            }else{
                re.put("state", 0);
                re.put("msg", "账号或密码错误");
            }
            return re;
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("state", 0);
            re.put("msg", "error in login");
            return re;
        }

    }

    @Ok("json")
    @Fail("http:500")
    @At("/getonemoney")
    @GET
    public Object getonemoney(@Param("userphone")String userPhone, HttpServletRequest request){
        try{
            NutMap re = new NutMap();
            if(userPhone!=null) {
                User u = dao.fetch(User.class, Cnd.where("userphone", "=", userPhone));
                if (u!=null) {
                    re.put("state", 1);
                    re.put("msg", "OK");
                    re.put("money",u.getMoney());
                    HttpSession session = request.getSession();
                    session.setAttribute("userphone", userPhone);
                } else {
                    re.put("state", 0);
                    re.put("msg", "账号或密码错误");
                }
            }else{
                re.put("state", 0);
                re.put("msg", "账号或密码错误");
            }
            return re;
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("state", 0);
            re.put("msg", "error in login");
            return re;
        }

    }


}
