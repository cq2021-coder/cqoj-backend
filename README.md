## 准备工作

### MySQL
版本：我是用的 MySQL 版本为 **8.0.26**

配置：在 [application-dev.yml](src%2Fmain%2Fresources%2Fapplication-dev.yml) 中配置，在这里配置你的 MySQL 账号密码


### Redis
配置：同样在 [application-dev.yml](src%2Fmain%2Fresources%2Fapplication-dev.yml) 中配置，在这里配置你的 Redis


## 环境配置

本项目使用了多环境配置，分别为：dev, test, prod，可以在 [.gitignore](.gitignore) 中看到


## 代码沙箱
代码沙箱在 [RemoteCodeSandbox.java](src%2Fmain%2Fjava%2Fcom%2Fcq%2Fcqoj%2Fjudge%2Fcodesandbox%2Fimpl%2FRemoteCodeSandbox.java) 中配置，如果需要修改代码沙箱地址，就在这里面修改

