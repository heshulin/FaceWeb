package com.iheshulin.faceweb.controller;

import com.iheshulin.faceweb.dao.Image;
import com.iheshulin.faceweb.dao.User;
import com.iheshulin.faceweb.util.*;
import okio.Buffer;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import static com.iheshulin.faceweb.util.BaiduWord.handleText;

/**
 * Created by HeShulin on 2017/12/1.
 */
@IocBean
public class ImageController {
    private Log log = Logs.get();
    private MD5 md5= MD5.getMd5();
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }



    @Inject
    Dao dao;

    @Ok("json")
    @Fail("http:500")
    @At("/wordimage")
    @GET
    public Object doword(@Param("userphone")String userPhone, @Param("text")String text,HttpServletRequest request){
        System.out.println(text);
        try{
            HttpSession session = request.getSession();
            String sessionUserName = ""+session.getAttribute("userphone");
            User u = dao.fetch(User.class, Cnd.where("userphone", "=", userPhone));
            if(sessionUserName.equals(userPhone)){
                if(u!=null){
                    if(u.getMoney()>=2){
                        NutMap re = new NutMap();
                        String tempUrl = BaiduWord.handleText(text);
                        re.put("data", tempUrl);
                        System.out.println(BaiduWord.handleText(text));

                        Image image = new Image();
                        image.setUserid(u.getId());
                        image.setImageurl(tempUrl);
                        dao.insert(image);
                        u.setMoney(u.getMoney()-2);
                        dao.update(u);
                        re.put("success",true);
                        re.put("msg","操作成功");
                        return re;
                    }else{
                        NutMap re = new NutMap();
                        re.put("success", false);
                        re.put("msg", "资金不足");
                        return re;
                    }

                }
                else
                {
                    NutMap re = new NutMap();
                    re.put("success", false);
                    re.put("msg", "请注册后再进行操作");
                    return re;
                }
            }else {
                NutMap re = new NutMap();
                re.put("success", false);
                re.put("msg", "请登陆后再进行操作");
                return re;
            }





        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("success", false);
            re.put("msg", "error in doword");
            return re;
        }

    }
    @Ok("json")
    @Fail("http:500")
    @At("/voiceimage")
    @AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
    public Object dovoice(@Param("userphone")String userPhone,@Param("voice")File blobVoice, HttpServletRequest request){
        try{
            HttpSession session = request.getSession();
            String sessionUserName = ""+session.getAttribute("userphone");
            User u = dao.fetch(User.class, Cnd.where("userphone", "=", userPhone));
            if(sessionUserName.equals(userPhone)) {
                if(u.getMoney()>=2) {
                    InputStream in = new FileInputStream(blobVoice);
                    File dir = new File("E:\\");
                    File file = new File(dir, "test.wav");
                    FileOutputStream out = new FileOutputStream(file);
                    Streams.copy(in, out, true);
                    //System.out.println(blobVoice.isFile());
                    NutMap re = new NutMap();
                    String text = null;
                    try {
                        text = Recognition.method3("E:\\test.wav");
                        System.out.println("text" + text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (text != null) {
                        re.put("data", handleText(text));
                        System.out.println(handleText(text));
                    }
                    u.setMoney(u.getMoney()-2);
                    dao.update(u);
                    re.put("success", true);
                    re.put("msg", "操作成功");
                    return re;
                }
                else{
                    NutMap re = new NutMap();
                    re.put("success", false);
                    re.put("msg", "资金不足");
                    return re;
                }
            }else {
                NutMap re = new NutMap();
                re.put("success", false);
                re.put("msg", "请登陆后再进行操作");
                return re;
            }




        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("success", false);
            re.put("msg", "error in doword");
            return re;
        }

    }

    @Ok("json")
    @Fail("http:500")
    @At("/imageimage")
    @AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
    public Object doimage(@Param("userphone")String userPhone,@Param("image1")File image1,@Param("image2")File image2, HttpServletRequest request){
        try{
            HttpSession session = request.getSession();
            String sessionUserName = ""+session.getAttribute("userphone");
            User u = dao.fetch(User.class, Cnd.where("userphone", "=", userPhone));
            if(sessionUserName.equals(userPhone)) {
                if (u.getMoney() >= 2) {

                    InputStream in = new FileInputStream(image1);
                    File dir = new File("E:\\python\\change_face\\");
                    File file = new File(dir, "test1.png");
                    FileOutputStream out = new FileOutputStream(file);
                    Streams.copy(in, out, true);

                    in = new FileInputStream(image2);
                    dir = new File("E:\\python\\change_face\\");
                    file = new File(dir, "test2.png");
                    out = new FileOutputStream(file);
                    Streams.copy(in, out, true);
                    //System.out.println(blobVoice.isFile());
                    NutMap re = new NutMap();
                    Runtime.getRuntime().exec("cmd /k start python main.py", null, new File("E:\\python\\change_face\\"));
                    String tmp2 = null;
                    deleteFile("E:\\python\\change_face\\result.png");
                    while (true) {
                        try {
                            File tmp = new File("E:\\python\\change_face\\result.png");
                            if (tmp.exists()) {
                                UploadFile up = new UploadFile();
                                tmp2 = up.uploadDiaryPhoto("E:\\python\\change_face\\result.png");
                                Image image = new Image();
                                image.setUserid(u.getId());
                                image.setImageurl(tmp2);
                                dao.insert(image);
                                System.out.println("qqqq" + tmp2);
                                break;
                            }
                        } catch (Exception E) {
                            E.printStackTrace();
                        }


                    }
                    u.setMoney(u.getMoney()-2);
                    dao.update(u);
                    re.put("success", true);
                    re.put("msg", "操作成功");
                    re.put("data", tmp2);
                    return re;
                }else{
                    NutMap re = new NutMap();
                    re.put("success", false);
                    re.put("msg", "资金不足");
                    return re;
                }
            }else {
                NutMap re = new NutMap();
                re.put("success", false);
                re.put("msg", "请登陆后再进行操作");
                return re;
            }






        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("success", false);
            re.put("msg", "error in doword");
            return re;
        }

    }
    @Ok("json")
    @Fail("http:500")
    @At("/dofindimage")
    @GET
    public Object dofindimage(@Param("userphone")String userPhone){
        try{


            NutMap re = new NutMap();
            User u = dao.fetch(User.class, Cnd.where("userphone", "=", userPhone));
            List<Image> i = dao.query(Image.class, Cnd.where("userid", "=", u.getId()));
            re.put("data",i);
            re.put("size",i.size());
            re.put("success",true);
            re.put("msg","操作成功");
            return re;



        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("success", false);
            re.put("msg", "error in doword");
            return re;
        }
    }

    @Ok("json")
    @Fail("http:500")
    @At("/gifimage")
    @AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
    public Object dogifimage(@Param("userphone")String userPhone,@Param("vedio")File blobVedio, HttpServletRequest request){
        try{
            HttpSession session = request.getSession();
            String sessionUserName = ""+session.getAttribute("userphone");
            User u = dao.fetch(User.class, Cnd.where("userphone", "=", userPhone));
            if(sessionUserName.equals(userPhone)) {
                if (u.getMoney() >= 2) {
                    InputStream in = new FileInputStream(blobVedio);
                    File dir = new File("E:\\python\\change_face2\\");
                    File file = new File(dir, "test.mp4");
                    FileOutputStream out = new FileOutputStream(file);
                    Streams.copy(in, out, true);
                    //System.out.println(blobVoice.isFile());
                    NutMap re = new NutMap();
                    Runtime.getRuntime().exec("cmd /k start python main.py", null, new File("E:\\python\\change_face2\\"));
                    String tmp2 = null;
                    while (true) {
                        try {
                            File tmp = new File("E:\\python\\change_face2\\gif.gif");
                            if (tmp.exists()) {
                                UploadFile up = new UploadFile();
                                tmp2 = up.uploadDiaryPhoto("E:\\python\\change_face2\\gif.gif");
                                System.out.println("qqqq" + tmp2);
                                Image image = new Image();
                                image.setUserid(u.getId());
                                image.setImageurl(tmp2);
                                dao.insert(image);
                                break;
                            }
                        } catch (Exception E) {
                            E.printStackTrace();
                        }


                    }
                    u.setMoney(u.getMoney()-2);
                    dao.update(u);
                    re.put("data", tmp2);
                    re.put("success", true);
                    re.put("msg", "操作成功");
                    return re;
                }else{
                    NutMap re = new NutMap();
                    re.put("success", false);
                    re.put("msg", "资金不足");
                    return re;
                }
            }else {
                NutMap re = new NutMap();
                re.put("success", false);
                re.put("msg", "请登陆后再进行操作");
                return re;
            }

        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("success", false);
            re.put("msg", "error in dogif");
            return re;
        }

    }


    @Ok("json")
    @Fail("http:500")
    @At("/checkcode")
    @GET
    public Object checkcode( HttpServletRequest request){

        try{

            NutMap re = new NutMap();
            //生成随机字串
            String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
            //存入会话session
            HttpSession session = request.getSession(true);
            session.setAttribute("checkcode", verifyCode.toLowerCase());
            File dir = new File("E:/verifies.png");
            //生成图片
            int w = 200, h = 80;
            try {
                VerifyCodeUtils.outputImage(w, h, dir, verifyCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UploadFile up = new UploadFile();
            String tmp2 = up.uploadDiaryPhoto("E:/verifies.png");
            re.put("success", true);
            re.put("checkcode", tmp2);
            re.put("msg", "yes");
            return re;

        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("success", false);
            re.put("msg", "error in doword");
            return re;
        }
    }
}

