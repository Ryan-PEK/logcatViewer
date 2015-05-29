'use strict';

/**
 * Module dependencies.
 */
var mongoose = require('mongoose'),
    errorHandler = require('./errors.server.controller'),
    _ = require('lodash');

/**
 * Show the current article
 */
exports.read = function(req, res) {
    res.json(req.logcats);
};

/**
 * List of Logcat
 */
exports.getPageByID = function(req, res) {
    var imei = req.params.imei,
        from_id = req.params.id - 0,
        to_id = req.params.id - 0 + 40;
    mongoose.connection.db.collection('imei_' + req.params.imei, function(err, collection) {
        collection.find({
            _id: {
                $gte: from_id,
                $lt: to_id
            }
        }).toArray(function(err, result) {
            if (result.length > 0) {
                console.log(result.length);
                res.json({
                    'next_id': result[result.length - 1]._id - 0 + 1,
                    'page_content': result
                });
                console.log(result[result.length - 1]._id);
            } else {
                res.json({
                    'next_id': req.params.id,
                    'page_content': []
                });
            }
        });
    });
};

/**
 * List of Articles
 */
exports.getFirstPageID = function(req, res) {
    mongoose.connection.db.collection('imei_' + req.params.imei, function(err, collection) {
        collection.count(function(err, count) {
            res.send(count.toString());
        });
    });
};
