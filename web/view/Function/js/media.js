var MediaUtils = {
    /**
     * 获取用户媒体设备(处理兼容的问题)
     * @param videoEnable {boolean} - 是否启用摄像头
     * @param audioEnable {boolean} - 是否启用麦克风
     * @param callback {Function} - 处理回调
     */
    getUserMedia: function (videoEnable, audioEnable, callback) {
        navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia
            || navigator.msGetUserMedia || window.getUserMedia;
        var constraints = {video: videoEnable, audio: audioEnable};
        if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
            navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
                callback(false, stream);
            })['catch'](function(err) {
                callback(err);

            });
        } else if (navigator.getUserMedia) {
            navigator.getUserMedia(constraints, function (stream) {
                callback(false, stream);
            }, function (err) {
                callback(err);
            });
        } else {
            callback(new Error('Not support userMedia'));
        }
    },

    /**
     * 关闭媒体流
     * @param stream {MediaStream} - 需要关闭的流
     */
    closeStream: function (stream) {
        if (typeof stream.stop === 'function') {
            stream.stop();
        }
        else {
            var trackList = [stream.getAudioTracks(), stream.getVideoTracks()];

            for (var i = 0; i < trackList.length; i++) {
                var tracks = trackList[i];
                if (tracks && tracks.length > 0) {
                    for (var j = 0; j < tracks.length; j++) {
                        var track = tracks[j];
                        if (typeof track.stop === 'function') {
                            track.stop();
                        }
                    }
                }
            }
        }
    }
};
// 用于存放 MediaRecorder 对象和音频Track，关闭录制和关闭媒体设备需要用到
var recorder, mediaStream;

// 用于存放录制后的音频文件对象和录制结束回调
var recorderFile, stopRecordCallback;

// 用于存放是否开启了视频录制
var videoEnabled = false;

// 录制短语音
function startRecord(enableVideo) {
    videoEnabled = enableVideo;
    MediaUtils.getUserMedia(enableVideo, true, function (err, stream) {
        if (err) {
            throw err;
        } else {
            // 通过 MediaRecorder 记录获取到的媒体流
            recorder = new MediaRecorder(stream);
            mediaStream = stream;
            var chunks = [], startTime = 0;
            recorder.ondataavailable = function(e) {
                chunks.push(e.data);
            };
            recorder.onstop = function (e) {
                recorderFile = new Blob(chunks, { 'type' : recorder.mimeType });
                chunks = [];
                if (null != stopRecordCallback) {
                    stopRecordCallback();
                }
            };
            recorder.start();
        }
    });
}


// 停止录制
function stopRecord(callback) {
    stopRecordCallback = callback;
    // 终止录制器
    recorder.stop();
    // 关闭媒体流
    MediaUtils.closeStream(mediaStream);
}

// 播放录制的音频
function playRecord() {
    var url = URL.createObjectURL(recorderFile);
    var dom = document.createElement(videoEnabled ? 'video' : 'audio');
    dom.autoplay = true;
    dom.src = url;
    if (videoEnabled) {
        dom.width = 640;
        dom.height = 480;
        dom.style.zIndex = '9999999';
        dom.style.position = 'fixed';
        dom.style.left = '0';
        dom.style.right = '0';
        dom.style.top = '0';
        dom.style.bottom = '0';
        dom.style.margin = 'auto';
        document.body.appendChild(dom);
    }
}

// 用于存放 MediaRecorder 对象和音频Track，关闭录制和关闭媒体设备需要用到
var recorderVedio, mediaStreamVedio;

// 用于存放录制后的音频文件对象和录制结束回调
var recorderVedioFile, stopRecordVedioCallback;

// 用于存放是否开启了音频录制
var audioEnabled = false;
// 录制短视频
function startVedioRecord(button) {
    button.disabled = true;
    var buttongif2 = document.getElementById("buttongif2");
    buttongif2.disabled = false;
    audioEnabled = false;
    MediaUtils.getUserMedia(true, audioEnabled, function (err, stream) {
        if (err) {
            throw err;
        } else {
            // 通过 MediaRecorder 记录获取到的媒体流
            recorderVedio = new MediaRecorder(stream);
            mediaStreamVedio = stream;
            var chunks = [], startTime = 0;
            recorderVedio.ondataavailable = function(e) {
                chunks.push(e.data);
            };
            recorderVedio.onstop = function (e) {
                recorderVedioFile = new Blob(chunks, { 'type' : 'video/mp4' });
                chunks = [];
                if (null != stopRecordVedioCallback) {
                    stopRecordVedioCallback();
                }
            };
            recorderVedio.start();
        }
    });
}
// 停止录制
function stopRecord(button) {
    button.disabled = true;
    var buttongif1 = document.getElementById("buttongif1");
    buttongif1.disabled = false;
    stopRecordVedioCallback = null;    // 终止录制器
    recorderVedio.stop();
    // 关闭媒体流

    MediaUtils.closeStream(mediaStreamVedio);

}

function hslgifConnect() {
    var storage=window.localStorage;
    var tempCookie = storage["userphone"];
    var formdata = new FormData();
    formdata.append('userphone',tempCookie);
    formdata.append('vedio',recorderVedioFile);
    //createDownloadLink();
    $.ajax({
        type: "post",
        url: "../../gifimage",
        //**文件上传空间的id属性,这边需要写入对应的上传文件的fileId
        data: formdata,
        async: false,
        cache: false,
        contentType: false,
        processData: false,
        dataType: "json",
        success: function(data) {//data为返回值
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
            var data = eval(data);
            if(data.success == true) {//data为 url跳转网页中传回的值。
                var gifimage =document.getElementById("gifimage");
                gifimage.src = data.data;
                gifimage.style.display="block";
                var gifbutton =document.getElementById("gifbutton");
                gifbutton.innerHTML = "再次购买";
                gifbutton.onclick=function(){
                    var gifimage =document.getElementById("gifimage");
                    gifimage.style.display="none";
                    gifimage.src="";
                    var gifbutton =document.getElementById("gifbutton");
                    gifbutton.innerHTML = "￥2确认购买";
                    gifbutton.onclick=hslgifConnect;
                };
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
function createDownloadLink() {
    alert(recorderVedioFile);
    url = URL.createObjectURL(recorderVedioFile);
    var li = document.createElement('li');
    var au = document.createElement('audio');
    var hf = document.createElement('a');

    hf.href = url;
    hf.download = new Date().toISOString() + '.mp4';
    hf.innerHTML = hf.download;
    li.style.margin="0 auto";
    li.appendChild(au);
    li.appendChild(hf);
    var testtemp = document.getElementById("testtemp");
    testtemp.appendChild(li);
}