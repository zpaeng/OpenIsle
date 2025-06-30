# OpenIsle

OpenIsle 是一个基于 Spring Boot 的社区后端平台示例，提供注册、登录和基于 JWT 的认证功能，支持使用 MySQL 作为数据库，并通过 [Resend](https://resend.com) API 发送注册邮件。

## 功能特性

- **注册/登录**：用户可以注册并登录，密码使用 BCrypt 加密保存。
- **JWT 认证**：登录成功后返回 JWT，后续请求需在 `Authorization` 头中携带 `Bearer` token。
- **邮件通知**：示例通过 Resend API 发送欢迎邮件，可根据需要修改。
- **灵活配置**：数据库地址、账户密码、Resend API Key 等均可通过环境变量或 `application.properties` 配置。

## 快速开始

### 环境准备

- Java 17+
- Maven 3+
- MySQL 数据库

### 构建与运行

1. 修改 `src/main/resources/application.properties`，或通过环境变量配置：
   - `MYSQL_URL`：数据库连接 URL，例如 `jdbc:mysql://localhost:3306/openisle`。
   - `MYSQL_USER`：数据库用户名。
   - `MYSQL_PASSWORD`：数据库密码。
   - `RESEND_API_KEY`：Resend 邮件服务 API Key。
   - `JWT_SECRET`：JWT 签名密钥。
   - `JWT_EXPIRATION`：JWT 过期时间（毫秒）。

2. 构建并运行：

```bash
mvn spring-boot:run
```

启动后访问：

- `POST /api/auth/register`：注册新用户，参数示例：
  ```json
  {
    "username": "test",
    "email": "test@example.com",
    "password": "password"
  }
  ```
- `POST /api/auth/login`：登录，返回 `{ "token": "..." }`。
- 其他受保护接口示例：`GET /api/hello`，需在请求头加入 `Authorization: Bearer <token>`。

## 目录结构

```
src/main/java/com/openisle
├── OpenIsleApplication.java    // 应用入口
├── config                      // Spring Security 配置
├── controller                  // 控制器
├── model                       // 数据模型
├── repository                  // 数据访问层
└── service                     // 业务逻辑
```

## 许可

本项目使用 MIT License，可自由修改和分发。
