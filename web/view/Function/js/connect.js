function getCookie(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return null;
}

function hslwordConnect() {
    var storage=window.localStorage;
    var tempCookie = storage["userphone"];
    var text = $("#word").val();
    $.ajax({
        type: "get",
        url: "../../wordimage",
        data: {
            userphone: tempCookie,
            text: text
        },
        dataType: "json",
        success: function(data) {//data为返回值
            var data = eval(data);
            if(data.success == true) {//data为 url跳转网页中传回的值。
                var wordimage =document.getElementById("wordimage");
                wordimage.src = data.data;
                wordimage.style.display="block";
                var word =document.getElementById("word");
                word.style.display="none";
                var wordbutton =document.getElementById("wordbutton");
                console.log("hslhslhsl");
                wordbutton.innerHTML = "再次购买";
                wordbutton.onclick=function(){
                    var wordimage =document.getElementById("wordimage");
                    wordimage.style.display="none";
                    wordimage.src="";
                    var word =document.getElementById("word");
                    word.style.display="block";
                    var wordbutton =document.getElementById("wordbutton");
                    wordbutton.innerHTML = "￥2确认购买";
                    wordbutton.onclick=hslwordConnect;
                };
                var storage = window.localStorage;
                var tempCookie = storage["userphone"];
                $.ajax({
                    type: "get",
                    url: "../../getonemoney",
                    data: {
                        userphone: tempCookie,
                    },
                    dataType: "json",
                    success: function (data) {//data为返回值
                        var getonesmoney = document.getElementById("onesmoney");
                        getonesmoney.innerHTML = "账户余额¥" + data.money;
                    },
                    error: function () {
                        var getonesmoney = document.getElementById("onesmoney");
                        getonesmoney.innerHTML = "网络状况差";
                    }
                });
                alert(data.msg);
            } else {
                alert(data.msg);
            }
        },
        error: function () {
            var getonesmoney = document.getElementById("onesmoney");
            getonesmoney.innerHTML = "服务器错误";
        }
    });
}
function tempwordConnect() {
    var wordimage =document.getElementById("wordimage");
    wordimage.style.display="none";
    var word =document.getElementById("word");
    word.style.display="block";
    var wordbutton =document.getElementById("wordbutton");
    wordbutton.text = "￥2确认购买";
    wordbutton.onclick=hslwordConnect;
}
function hslinit() {
    alert("asdasdas");
    var storage = window.localStorage;
    var tempCookie = storage["userphone"];
    alert("asdasdas");
    $.ajax({
        type: "get",
        url: "../../getonemoney",
        data: {
            userphone: tempCookie,
        },
        dataType: "json",
        success: function (data) {//data为返回值
            var getonesmoney = document.getElementById("onesmoney");
            getonesmoney.innerHTML = "账户余额¥" + data.money;
        },
        error: function () {
            var getonesmoney = document.getElementById("onesmoney");
            getonesmoney.innerHTML = "网络状况差";
        }
    });
}