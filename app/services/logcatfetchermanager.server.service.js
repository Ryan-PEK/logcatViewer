/**
 * Created by hongyanyin on 5/18/15.
 */

var Fetcher = require('./logcatfetcher.server.service.js');
var DeviceStatus = require('../models/devicestatus.server.model.js');

//根据设备状态,调用相应的进程处理设备
exports.handleDevice = function(device){
    if(device == null){
        return;
    }

    switch(device.status)
    {
        case DeviceStatus.booked:
        case DeviceStatus.transferstopped:
        case DeviceStatus.online:
        case DeviceStatus.null:
            //启动连接进程
            Fetcher.connectDevice(device);
            break;

        case DeviceStatus.connected:
        case DeviceStatus.tranfering:
            //启动抓取进程
            Fetcher.startFetcher(device);
            break;

        case DeviceStatus.offline:
        case DeviceStatus.unauthorized:
        case DeviceStatus.unkown:
        case DeviceStatus.clientipchange:
        case DeviceStatus.transferstopped:
            //杀死可能存在的进程
            Fetcher.killFetcher(device);
            //启动连接进程
            Fetcher.connectDevice(device);
            break;

        case DeviceStatus.stoppedbyuser:
            //杀死可能存在的进程
            Fetcher.stopFetcher(device);
            break;
    }
};

//监控设备状态
exports.monitorFetcher = function()
{

};




