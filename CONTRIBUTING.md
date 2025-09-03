#### **⚠️注意：仅想修改前端的朋友可不用部署后端服务**

## 如何部署

> Step1 先克隆仓库

```shell
git clone https://github.com/nagisa77/OpenIsle.git
cd OpenIsle
```

> Step2 后端部署

```shell
cd backend
```

以IDEA编辑器为例，IDEA打开backend文件夹。

- 设置VM Option，最好运行在其他端口，非8080，这里设置8081
- 设置jdk版本为java 17

```shell
-Dserver.port=8081
```

![配置1](contributing_img\backend_img_3.png)
![配置2](contributing_img\backend_img_2.png)

- 本机配置MySQL服务（网上很多教程，忽略）
  + 可以用Laragon,自带MySQL包括Nodejs,版本建议6.x，7以后需要Lisence
  + 下载地址(https://github.com/leokhoa/laragon/releases)
- 设置环境变量.env 文件 或.properties 文件（二选一）

1. 环境变量文件生成

```shell
cp open-isle.env.example open-isle.env
```

修改环境变量，留下需要的，比如你要开发Google登录业务，就需要谷歌相关的变量，数据库是一定要的


![环境变量](contributing_img\backend_img_7.png)
应用环境文件， 选择刚刚的`open-isle.env`

![环境变量](contributing_img\backend_img_6.png)

1. 直接修改 .properities 文件

位置src/main/application.properties, 数据库需要修改标红处，其他按需修改

![配置数据库](contributing_img\backend_img_5.png)

执行db/init.sql脚本，导入基本的数据
![初始化脚本](contributing_img\resources_img.png)

处理完环境问题直接跑起来就能通了

![运行画面](contributing_img\backend_img_4.png)

> Step3 前端部署

**⚠️ 环境要求：Node.js 版本最低 20.0.0（因为 Nuxt 框架要求）**

前端可以依赖本机部署的后端，也可以直接调用线上的后端接口

```shell
cd ../frontend_nuxt/
```

copy环境.env文件


利用预发环境
**(⚠️强烈推荐只部署前端的朋友使用该环境)**
```shell
cp .env.staging.example .env
```

利用生产环境
```shell
cp .env.production.example .env
```

利用本地环境
```shell
cp .env.dev.example .env
```


前端安装编译运行

```shell
# 安装依赖
npm install --verbose

# 运行前端服务
npm run dev
```

如此一来，浏览器访问 http://127.0.0.1:3000 即可访问前端页面

## 其他配置
1. 配置第三方登录

这里以github为例
修改application.properties配置
![后端配置](contributing_img\backend_img.png)

修改.env配置
![前端](contributing_img\fontend_img.png)

配置第三方登录回调地址
![github配置](contributing_img\github_img.png)
![github配置2](contributing_img\github_img_2.png)