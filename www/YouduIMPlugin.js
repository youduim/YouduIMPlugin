var exec = require('cordova/exec');


exports.setServer = function(host1,host2,port,success,error) {
    exec(success, error, 'YouduIMPlugin', 'setServer',[host1,host2,port]);
}

exports.loginWithAccount = function(account,password,success,error) {
    exec(success,error,'YouduIMPlugin', 'loginWithAccount',[account,password]);
}

exports.getSessionList = function (success, error) {
    exec(success, error, 'YouduIMPlugin', 'getSessionList', []);
}

exports.gotoSession = function (sessionId, success, error) {
    exec(success, error, 'YouduIMPlugin', 'gotoSession', [sessionId]);
}

exports.gotoCreateSession = function (success, error) {
     exec(success, error, 'YouduIMPlugin', 'gotoCreateSession',[]);
}

exports.logOut = function (success,error) {
    exec(success, error, 'YouduIMPlugin', 'logOut', []);
}

exports.getUnreadCount = function(callback) {
    exec(callback, null, 'YouduIMPlugin', 'getUnreadCount', []);
}

exports.chatWith = function(callback) {
    exec(callback, null, "YouduIMPlugin", 'chatWith', ['100334']);
}
