/**
 * Created by hongyanyin on 5/31/15.
 */

var Device = require('mongoose').model('Device');
var DeviceStatus = require('../models/devicestatus.server.model.js');

var exec = require('child_process').exec;
var commandPath = __dirname + '/shell';
var adbPath = __dirname + '/tools/android-sdk-linux/platform-tools';
var fetcherShellName = 'remote-logcat-fetcher';
var processMaxCount = 2;

var deviceOfflineKeywords = ['unable to connect to', '- waiting for device -'];
var deviceOnlineKeywords = ['connected to'];

//杀死进程
exports.killFetcher = function(device) {
    var script = commandPath + '/killp.sh' + ' ' + fetcherShellName + ' ' + device.imei;
    exports.hasFetcherRun(device,function(hasRun){
        if(hasRun)
        {
            child = exec(script,
                function (error, stdout, stderr) {
                    console.log('杀死进程输出: ' + stdout);
                    if (error != null) {
                        console.error('exec error: ' + error);
                    }

                    if(stdout != null){
                    }
                });
        }
    });

    script = commandPath + '/killp.sh' + ' \"adb -s ' + device.imei + ':5555\" logcat';
    exports.hasFetcherRun(device,function(hasRun){
        if(hasRun)
        {
            child = exec(script,
                function (error, stdout, stderr) {
                    console.log('杀死进程输出: ' + stdout);
                    if (error != null) {
                        console.error('exec error: ' + error);
                    }

                    if(stdout != null){
                    }
                });
        }
    });

};

//连接设备,更新设备在数据中的状态
exports.connectDevice = function(device){
    var command = adbPath + '/adb connect ' + device.ip + ":5555";
    console.log('开始连接设备: ' + device.ip);

    var child = exec(command);

    child.stdout.on('data',function(data){
        //检测是否连接成功
        for(var i = 0; i < deviceOnlineKeywords.length ; i++)
        {
            if(data.toString().indexOf(deviceOnlineKeywords[i]) > -1){
                //连接成功
                Device.updateStatus(device.imei, DeviceStatus.connected);
                return;
            }
        }
        //检测是否连接失败
        for(var i = 0; i < deviceOfflineKeywords.length ; i++)
        {
            if(data.toString().indexOf(deviceOfflineKeywords[i]) > -1){
                //杀掉进程,更新数据库
                Device.updateStatus(device.imei, DeviceStatus.offline);
                return;
            }
        }
    });

    child.stderr.on('data', function(data){
        console.log('连接设备出错_: ' + data);
    });

    child.on('close', function(code){
        console.log('close: ' + code);
    });
};

//抓取日志
exports.startFetcher = function(device){
    var fetcherCommand = commandPath +  '/remote-logcat-fetcher.sh ' + device.imei + ' ' + device.ip
        + ' \"' + device.os + '\" \"' + device.deviceModel + '\"';
    console.log("fetcherCommand: " + fetcherCommand);


    exports.hasFetcherRun(device, function(hasRun) {
        if(!hasRun)
        {
            //start the process
            console.log("开始启动抓取进程");
            var process = exec(fetcherCommand);

            process.stdout.on('data', function(data){
                console.log('获取日志过程的输出: ' + data);
                //根据输出处理未成功传输的进程
                for(var i = 0; i < deviceOfflineKeywords.length ; i++)
                {
                    if(data.toString().indexOf(deviceOfflineKeywords[i]) > -1){
                        //杀掉进程,更新数据库
                        Device.updateStatus(device.imei, DeviceStatus.offline);
                        exports.killFetcher(device);
                        return;
                    }
                }
                //进程正常工作,更新数据库
                Device.updateStatus(device.imei, DeviceStatus.tranfering);
            });

            process.stderr.on('data', function(data){
                console.log('获取日志过程的错误: ' + data);
                //根据输出处理未成功传输的进程
                for(var i = 0; i < deviceOfflineKeywords.length ; i++)
                {
                    if(data.toString().indexOf(deviceOfflineKeywords[i]) > -1){
                        //杀掉进程,更新数据库
                        Device.updateStatus(device.imei, DeviceStatus.offline);
                        exports.killFetcher(device);
                        return;
                    }
                }
            });

            process.on('SIGINT', function() {
                //console.log('Got SIGHUP signal.');
                //process.stdin.stop();
                //process.kill(process.pid);
            });
        }
    });
};

//停止抓取
exports.stopFetcher = function(device){
    Device.updateStatus(device.imei, DeviceStatus.stoppedbyuser);
    exports.killFetcher(device);
};

//检查是否有进程正在运行
exports.hasFetcherRun = function (devcie, callback)
{
    var script = 'ps -ef | grep ' + fetcherShellName  + ' | grep ' + devcie.imei + ' | wc -l';
    var child = exec(script);

    child.stdout.on('data', function(data){
        if(data != null && data >= processMaxCount) {
            //已经有进程在运行
            console.log('已有进程在运行');
            if(callback != null)
            {
                callback(true);
            }
        } else {
            //未有进程在运行
            console.log('未有进程在运行');
            if(callback != null)
            {
                callback(false);
            }
        }
    });

    child.stderr.on('data', function(data){
        console.log('exec error: ' + data);
    });

    child.on('close', function(code){
        console.log('close: ' + code);
    });

};

//检查设备状态
exports.checkDeviceState = function(device, callback){
    var command = adbPath + '/adb devices';
    var onlineKeywords = [device.ip + '  device'];
    var keywords = [device.ip + '  device', device.ip + '  device'];

    var process = exec(command,function (error, stdout, stderr) {
        if(stdout != null){
            var out = stdout.toString();
            if(out.indexOf(device.ip + '  device') > 0)
            {
                callback(DeviceStatus.connected);
            } else if(out.indexOf(device.ip + '  unauthorized') > 0) {
                callback(DeviceStatus.unauthorized);
            } else if(out.indexOf(device.ip + '  offline') > 0) {
                callback(DeviceStatus.offline);
            } else {
                callback(DeviceStatus.unkown);
            }
        }
    });
};



