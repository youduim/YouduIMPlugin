# YouduIMPlugin 集成
1. 集成方式
2. 推送与高德地图信息配置
3. 接口说明
4. 其他接口


## 集成步骤


1. 打开terminal，在主工程目录下 cordova plugin add https://github.com/jeason789/YouduIMPlugin.git 添加插件

## 推送与高德地图信息配置


1. 打开主工程AndroidManifest.xml，修改如下信息
	- your\_app\_id 替换为主工程build.gradle的application id,如果build.gradle没指定，则以AndroidManifest.xml的package为准
	
2. 替换string.xml 推送信息
	- 	your\_huawei\_app\_id 替换为华为推送app id
	- 	your\_huawei\_app\_secret 替换为华为推送app secret
	- 	your\_meizu\_app\_id 替换为魅族推送app id
	- 	your\_meizu\_app\_secret 替换为魅族推送app secret
	- 	your\_meizu\_app\_key 替换为魅族推送app key
	- 	your\_xiaomi\_app\_id 替换为小米推送app id
	- 	your\_xiaomi\_app\_secret 替换为小米推送app secret
	- 	your\_xiaomi\_app\_key 替换为小米推送app key
	- 	your\_amap\_app\_id 替换为高德地图app_id

	- 	[高德地图集成申请地址](https://lbs.amap.com/dev/key/app)
	- 	[华为推送集成申请地址](https://developer.huawei.com/consumer/cn/console#/appManage)
	- 	[小米推送集成申请地址](http://admin.xmpush.xiaomi.com/zh_CN/)
	- 	[魅族推送集成申请地址](http://push.meizu.com)

## 插件接口说明


- 服务器设置
  服务器设置分为两种，通过总机号指定服务器登录和手动设置服务器信息。
 
  手动设置服务器信息:
	```
	cordova.plugins.YouduIMPlugin.setServerSetting(host1, host2, port, success, error);//host1 外网服务器,host2 内网服务器,port 端口,success 成功回调函数,error错误回调函数
	```

-  通过账号密码登录 

	```
	cordova.plugins.YouduIMPlugin.loginWithAccount(account, password, success, error);//account账号,success 成功回调函数,error错误回调函数 
	成功回调数据：{"loginSucc": gid}
	失败回调数据: {"loginFailed": {"title":"登录失败","message":"密码错误"} }
	```

- 获取会话列表

	```
	cordova.plugins.YouduIMPlugin.getSessionList(success, error); //success 回调函数获取会话列表信息 ,error错误回调函数
	成功回调数据：
	{
		"sessionList": 
		[
			{
			"at":false,   //是否有@消息
			"avoid":false, // 会话是否为消息免扰
			"content":"adf", // 最后一条消息内容 
			"failSend":false, // 是否有发送失败消息
			"msgId":1554689749, // 最后一条消息id
			"operationTime":1554713729314, //最后操作时间，与sticky标记一起，用于排序
			"read":false, // 是否已读
			"reference":false, //是否有回复消息
			"sessionId":"331532-331593", //会话id
			"sticky":false, 	//是否会话置顶
			"text":"adf",		// 用于会话列表显示最后一条消息内容简要
			"time":1554713729314, //最后一条消息时间
			"title":"张三", 		//标题
			"type":0,		//0-单人会话 1-多人会话 2-广播 3-系统, 4-SMS(未用), 5-ios小助手(占用), 6-APP 100-有度小助手,  101-应用会话  10-互联单人会话 11-互联多人会话
			"unreadSize":2, //会话未读数
			"unreadText":"2", //会话未读数
			"userId":331532  //最后一条消息发送者id
			}
		]
	}
	```	

- 打开会话

	```
	cordova.plugins.YouduIMPlugin.gotoSession(sessionId, success, error); //sessionId 目标会话ID,success 成功回调函数,error错误回调函数
	```

- 创建会话

	```
	cordova.plugins.YouduIMPlugin.gotoCreateSession(success, error); // success 成功回调函数,error错误回调函数
	```
	
- 退出登录
	
	```
	cordova.plugins.YouduIMPlugin.logout(success, error); // success 成功回调函数,error错误回调函数
	//成功回调数据：{"logout":"success"} 
	```
- 获取会话未读数
	
	
	```
	cordova.plugins.YouduIMPlugin.getUnreadCount(callback); // callback 插件通过此回调函数通知未读数变化 回调数据如下: {"unreadCount" : 0}
	```
	
