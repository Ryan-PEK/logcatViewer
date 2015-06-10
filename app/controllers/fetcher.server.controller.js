'use strict';

/**
 * Created by hongyanyin on 5/26/15.
 */

var Fetcher = require('../services/logcatfetcher.server.service.js');
var Device = require('mongoose').model('Device');
var DeviceStatus = require('../models/devicestatus.server.model.js');

exports.stop = function(req, res, next){
    console.log('imei in request', req.param('imei'));
    Device.findOne({'imei' : req.param('imei')}, function(err, deviceInDb) {
        if (err) {
            return next(err);
        } else {
            if (deviceInDb != null) {
                Fetcher.stopFetcher(deviceInDb);
                deviceInDb.status = DeviceStatus.stoppedbyuser;
                res.json(deviceInDb);
            }
        }
    });
};