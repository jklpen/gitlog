
# gitlog

统计开发提交的所有仓库的代码量<br/>


配置projectsDir仓库目录，会自动统计目录下所有项目的数据
```
projectsDir=E:\\projects
```
Linux用户可以配置initCommands和gitUrls。<br/>
initCommands用于解决git相关命令的权限问题。<br/>
gitUrls是远程仓库的地址，用“,”分隔。
```
initCommands=ssh-agent bash,ssh-add ~/.ssh/putty20160523
gitUrls=http://gitlab.511.com/java/com.weixin.git,http://gitlab.511.com/java/order.git
```

启动后访问 http://127.0.0.1:3109/ 