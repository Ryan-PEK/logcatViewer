'use strict';

/**
 * Module dependencies.
 */
var logcats = require('../../app/controllers/logcats.server.controller');

module.exports = function(app) {

    app.route('/logcats/:imeiValue/:id')
        .get(logcats.getPageByID);

    app.route('/firstPageID/:imeiValue')
        .get(logcats.getFirstPageID);

};
