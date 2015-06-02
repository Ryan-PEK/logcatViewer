'use strict';

/**
 * Created by hongyanyin on 5/26/15.
 */

var fetcher = require('../../app/controllers/fetcher.server.controller');

module.exports = function(app){
    app.route('/fetcher/stop').post(fetcher.stop);
};