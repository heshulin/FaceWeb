package com.iheshulin.faceweb.controller;

/**
 * Created by HeShulin on 2017/11/30.
 */

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

@IocBean
public class LoginController {
    private Log log = Logs.get();
    private MD5 md5= MD5.getMd5();

    @Inject
    Dao dao;

    @Ok("json")
    @Fail("http:500")
    @At("/dologin")
    @GET
    public Object doLogin(@Param("userphone")String userPhone, @Param("password")String password,HttpServletRequest request){
        try{
            NutMap re = new NutMap();
            if(userPhone!=null&&password!=null) {
                password = md5.getMd5(password);
                User u = dao.fetch(User.class, Cnd.where("userphone", "=", userPhone).and("password", "=", password));
                if (u!=null) {
                    re.put("status", 1);
                    re.put("msg", "OK");
                    re.put("id", u.getId());
                    HttpSession session = request.getSession();
                    session.setAttribute("userphone", userPhone);
                } else {
                    re.put("status", 0);
                    re.put("msg", "账号或密码错误");
                }
            }else{
                re.put("status", 0);
                re.put("msg", "账号或密码错误");
            }
            return re;
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("status", 0);
            re.put("msg", "error in login");
            return re;
        }

    }

}
