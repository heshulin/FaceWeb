package com.iheshulin.faceweb.controller;

/**
 * Created by HeShulin on 2017/11/30.
 */

import com.iheshulin.faceweb.dao.CheckCode;
import com.iheshulin.faceweb.dao.User;
import com.iheshulin.faceweb.util.MD5;
import com.iheshulin.faceweb.util.Message;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by HeShulin on 2017/5/28.
 */

@IocBean
public class RegisterController {
    private Log log = Logs.get();
    private MD5 md5= MD5.getMd5();

    @Inject
    Dao dao;

    @Ok("json")
    @Fail("http:500")
    @At("doregister1")
    @POST
    public Object register1(@Param("userphone")String userphone, HttpServletRequest request){

        //获取验证码过期时间
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
        Date tomorrow = c.getTime();

        try{
            NutMap re = new NutMap();
            Message message=new Message();
            System.out.println(userphone);
            String messagetext=message.sendMessage(userphone);
            CheckCode checkCode=new CheckCode();
            checkCode.setUserphone(userphone);
            checkCode.setCheckcode(messagetext);
            checkCode.setExpiration(tomorrow);
            dao.insert(checkCode);
            re.put("status",1);
            re.put("msg","OK");
            return re;
        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("status", 0);
            re.put("msg", "error in register1");
            return re;
        }

    }

    @Ok("json")
    @Fail("http:500")
    @At("doregister2")
    @POST
    public Object register2(@Param("username")String username,@Param("userphone")String userphone,@Param("password")String password,@Param("checkcode")String checkcode, HttpServletRequest request){

        //获取当前时间
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        password = md5.getMd5(password);
        try{
            NutMap re = new NutMap();
            User isuser = dao.fetch(User.class, Cnd.where("userphone", "=", userphone));
            User user=new User();
            List<CheckCode> tempuser=dao.query(CheckCode.class, Cnd.where("userphone","=",userphone));
            //记得要-1
            Date temptime=tempuser.get(tempuser.size()-1).getExpiration();
            String tempcheckcode=tempuser.get(tempuser.size()-1).getCheckcode();
            System.out.println(tempcheckcode+" "+checkcode);
            if(tempcheckcode.equals(checkcode)){
                if(today.before(temptime)) {
                    if(isuser==null){
                        user.setPassword(password);
                        user.setUserphone(userphone);
                        user.setUsername(username);
                        user.setMoney(0);
                        dao.insert(user);
                        re.put("status", 1);
                        re.put("msg", "OK");
                        return re;
                    }else{
                        re.put("status", 0);
                        re.put("msg", "用户已存在");
                        return re;
                    }

                }
                else{
                    re.put("statues", 0);
                    re.put("msg", "验证码已过期");
                    return re;
                }
            }
            else {
                re.put("statues",0);
                re.put("msg","验证码错误");
                return re;
            }

        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in register2");
            return re;
        }

    }
}