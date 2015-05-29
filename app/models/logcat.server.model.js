'use strict';

/**
 * Module dependencies.
 */
var mongoose = require('mongoose'),
	Schema = mongoose.Schema;

/**
 * Article Schema
 */
var LogcatSchema = new Schema({
	timestamp: String,
	pid: Number,
	ppid: Number,
	level: String,
	pname: String,
	msg: String
});

module.exports = LogcatSchema;

