'use strict';
/**
 * Created by hongyanyin on 5/13/15.
 */

var device = require('../../app/controllers/device.server.controller');

module.exports = function(app){
    app.route('/devices').get(device.list);
    app.route('/device').post(device.create);
    app.route('/device/:imei').get(device.read);
    app.param('imei', device.deviceByImei);
};