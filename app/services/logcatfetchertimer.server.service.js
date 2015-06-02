var CronJob = require('cron').CronJob;

var Device = require('mongoose').model('Device');
var logcatfetchermanager = require('./logcatfetchermanager.server.service.js');
var DeviceStatus = require('../models/devicestatus.server.model.js');

//遍历数据库注册的设备,执行相应的操作
module.exports = new CronJob('*/10 * * * * *', function() {
    //get device list from db
    Device.find({}, function(err, devices) {
        if(err) {
            console.error(err);
        } else {
            devices.forEach(function(device){
                logcatfetchermanager.handleDevice(device);
            });
        }
    });

}, null, true, 'America/Los_Angeles');