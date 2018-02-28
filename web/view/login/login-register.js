/*
 *
 * login-register modal
 * Autor: Creative Tim
 * Web-autor: creative.tim
 * Web script: #
 * 
 */
function setCookie(name,value)
{
    var Days = 30;
    var exp = new Date();
    exp.setTime(exp.getTime() + Days*24*60*60*1000);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}
function getCookie(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return null;
}
function showRegisterForm(){
    $('.loginBox').fadeOut('fast',function(){
        $('.registerBox').fadeIn('fast');
        $('.login-footer').fadeOut('fast',function(){
            $('.register-footer').fadeIn('fast');
        });
        $('.modal-title').html('Register with');
    }); 
    $('.error').removeClass('alert alert-danger').html('');
       
}
function showLoginForm(){
    $('#loginModal .registerBox').fadeOut('fast',function(){
        $('.loginBox').fadeIn('fast');
        $('.register-footer').fadeOut('fast',function(){
            $('.login-footer').fadeIn('fast');    
        });
        
        $('.modal-title').html('Login with');
    });       
     $('.error').removeClass('alert alert-danger').html(''); 
}

function openLoginModal(){
    showLoginForm();
    setTimeout(function(){
        $('#loginModal').modal('show');    
    }, 230);
    
}
function openRegisterModal(){
    showRegisterForm();
    setTimeout(function(){
        $('#loginModal').modal('show');    
    }, 230);
    
}
function loginAjax(){
    /*   Remove this comments when moving to server
    $.post( "/login", function( data ) {
            if(data == 1){
                window.location.replace("/home");            
            } else {
                 shakeModal(); 
            }
        });
    */

/*   Simulate error message from the server   */
    var uid = $("#una1").val();
    var pwd = $("#pwd1").val();
    $.ajax({
        type: "get",
        url: "../../dologin",
        data: {
            userphone: uid,
            password: pwd
        },
        dataType: "json",
        success: function(data) {//data为返回值
            var data = eval(data);
            if(data.status == "1") {//data为 url跳转网页中传回的值。
                var storage=window.localStorage;
                storage["userphone"]=uid;
                window.location.href = "../Function/index.html";


            } else {
                alert(data.msg);
            }
        }
    });

}

function registerAjax(){
    /*   Remove this comments when moving to server
     $.post( "/login", function( data ) {
     if(data == 1){
     window.location.replace("/home");
     } else {
     shakeModal();
     }
     });
     */

    /*   Simulate error message from the server   */


    var uid = $("#username").val();
    var pwd = $("#password").val();
    var repwd = $("#repassword").val();
    var tele = $("#telephone").val();
    var chc = $("#checkcode").val();

    if(pwd == repwd) {

        $.ajax({
            type: "post",
            url: "../../doregister2",
            data: {
                userphone: tele,
                username: uid,
                password: pwd,
                checkcode: chc
            },
            dataType: "json",
            success: function (data) {//data为返回值
                var data = eval(data);
                if (data.status == "1") {              //y为 url跳转网页中传回的值。
                    window.location.href = "/";
                } else {
                    alert(data.msg);
                }
            }
        });
    }
    else
    {
        alert("密码不一致");
    }
}
var InterValObj; //timer变量，控制时间
var count = 60; //间隔函数，1秒执行
var curCount;//当前剩余秒数
var code = ""; //验证码
var codeLength = 6;//验证码长度

//短信验证码
function hslmessage() {


    //短信验证码
    var phoNum = $("#telephone").val();
    // 向后台发送处理数据
    $.ajax({
        type: "post",
        url: "../../doregister1",
        data: {
            userphone: phoNum
        },
        dataType: "json",
        success: function (data) {//data为返回值
            var data = eval(data);
            if (data.status == "1") {              //y为 url跳转网页中传回的值。
                curCount = count;
                // 设置按钮显示效果，倒计时
                $("#getcode").attr("disabled", "true");
                $("#getcode").val("请在" + curCount + "秒内输入验证码");
                InterValObj = window.setInterval(SetRemainTime, 2000); // 启动计时器，1秒执行一次
            } else {
                alert(data.msg);
            }

        }
    });


}
//timer处理函数
function SetRemainTime() {
    if (curCount == 0) {
        window.clearInterval(InterValObj);// 停止计时器
        $("#getcode").removeAttr("disabled");// 启用按钮
        $("#getcode").val("重新发送验证码");
        code = ""; // 清除验证码。如果不清除，过时间后，输入收到的验证码依然有效
    } else {
        curCount--;
        $("#getcode").val("请在" + curCount + "秒内输入验证码");
    }
}

   