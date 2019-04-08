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



## 插件接口说明


- 服务器设置
  服务器设置分为两种，通过总机号指定服务器登录和手动设置服务器信息。
  1. 总机号登录方式：
	```
	cordova.plugins.YouduIMPlugin.setBuin(buin,success,error)；//buin 总机号,success 成功回调函数,error错误回调函数
	```
  2. 手动设置服务器信息:
	```
	cordova.plugins.YouduIMPlugin.setServerSetting(host1, host2, port, success, error);//host1 外网服务器,host2 内网服务器,port 端口,success 成功回调函数,error错误回调函数
	```

-  通过账号密码登录 

	```
	cordova.plugins.YouduIMPlugin.loginWithAccount(account, password, success, error);//account账号,success 成功回调函数,error错误回调函数
	```

- 获取会话列表

	```
	cordova.plugins.YouduIMPlugin.getSessionList(success，error); //success 回调函数获取会话列表信息 ,error错误回调函数
	```	

- 打开会话

	```
	cordova.plugins.YouduIMPlugin.gotoSession(sessionId，success, error); //sessionId 目标会话ID,success 成功回调函数,error错误回调函数
	```

- 创建会话

	```
	cordova.plugins.YouduIMPlugin.gotoCreateSession(success, error); // success 成功回调函数,error错误回调函数
	```
	
- 退出登录
	
	```
	cordova.plugins.YouduIMPlugin.logout(success, error); // success 成功回调函数,error错误回调函数
	```
	
