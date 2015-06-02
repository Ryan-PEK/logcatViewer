/**
 * Created by hongyanyin on 5/13/15.
 */

var Device = require('mongoose').model('Device');
var DeviceStatus = require('../models/devicestatus.server.model.js');

exports.create = function(req, res, next){
    var device  = new Device(req.body);

    Device.findOne({'imei' : device.imei}, function(err, deviceInDb) {
        if(err){
            return next(err);
        } else {
            if(deviceInDb == null)
            {
                //新设备,插入一条新纪录
                device.creationTime = Date.now();
                device.latestHeartbeat = Date.now();
                device.status = '注册成功';
                device.save(function(err){
                    if(err) {

                    }else {
                        res.json(device);
                    }
                });
            } else {
                //执行更新,更新所有信息
                device.latestHeartbeat = Date.now();
                device.status = DeviceStatus.booked;
                var deviceJson = device.toJSON();
                var key = '_id';
                delete deviceJson[key];

                Device.findOneAndUpdate({'imei': device.imei} , deviceJson, {'new': true}, function(err, deviceFromDB) {
                        if(err){
                            console.error('更新信息出错: ' + err);
                        } else {
                            res.json(deviceFromDB);
                        }
                    }
                );
            }
        }
    });
};

exports.list = function (req, res, next){
    Device.find({}, function(err, devices) {
        if(err){
            return next(err);
        } else {
            res.json(devices);
        }
    });
};

exports.read = function(req, res){
    res.json(req.device);
};

exports.deviceByImei = function(req, res, next, imei){
    Device.updateHeartbeat(imei, function(device){
        if(device == null)
        {

        } else {
            res.json(device);
        }
    });
};

exports.update = function(req, res, next){
    Device.findOneAndUpdate({'imei': req.device.imei} , req.body, function(err, device) {
         if(err){
             return next(err);
         } else {
             if(device!=null)
             {
                 Device.updateHeartbeat(device.imei);
             }
             res.json(device);
         }
        }
    );
};

exports.updateHeartbeat = function(req, res, next){
    Device.updateHeartbeat(req.device.imei, function(device){
        if(device == null)
        {

        }else {
            res.json(device);
        }
    });
};

