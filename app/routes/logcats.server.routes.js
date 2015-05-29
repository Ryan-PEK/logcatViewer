'use strict';

/**
 * Module dependencies.
 */
var logcats = require('../../app/controllers/logcats.server.controller');

module.exports = function(app) {

    app.route('/logcats/:imei/:id')
        .get(logcats.getPageByID);

    app.route('/firstPageID/:imei')
        .get(logcats.getFirstPageID);

};
