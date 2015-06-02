'use strict';

/**
 * Created by hongyanyin on 5/12/15.
 */
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var DeviceSchema = new Schema({
    imei: {type: String, trim: true, unique: true},
    ip : {type: String, trim: true},
    os : {type: String, trim: true},
    deviceModel : {type: String, trim: true},
    creationTime : { type: Date},
    latestHeartbeat : { type: Date, default: Date.now },
    status : {type: String, enum : ['注册成功', '在线', '未认证','离线', '日志传输中', '日志传输中断', '已停止', ''], default: '' }
}
    /*,{ _id: false }*/
);

DeviceSchema.statics.updateStatus = function(imei, status, callback){
    this.findOneAndUpdate({'imei': imei} , {'status': status}, {'new': true},function(err, device) {
            if(err){
                console.error(err);
                if(callback!=null)
                {
                    return callback(null);
                }
            } else {
                if(callback!=null)
                {
                    return callback(device);
                }
            }
        }
    );
};

DeviceSchema.statics.updateHeartbeat = function(imei, callback){
    this.findOneAndUpdate({'imei': imei} , {'latestHeartbeat': Date.now()}, {'new': true}, function(err, device) {
            if(err){
                console.error(err);
                return callback(null);
            } else {
                return callback(device);
            }
        }
    );
};

mongoose.model('Device', DeviceSchema);
