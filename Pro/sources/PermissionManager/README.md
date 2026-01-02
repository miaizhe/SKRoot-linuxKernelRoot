# X-KRoot Permission Manager (原 SKRoot)

这是 X-KRoot 项目的权限管理应用，用于管理内核级 Root 权限。

## 功能
- **权限管理**：授权或拒绝应用的 Root 请求。
- **环境检查**：检查 X-KRoot 内核环境的运行状态。
- **模块管理**：安装、卸载和更新 X-KRoot 内核模块。
- **系统日志**：查看 X-KRoot 相关的系统日志。
- **个性化设置**：自定义应用界面、背景音乐和主题颜色。

## 技术栈
- **Android**: Java (Fragment, RecyclerView, Service)
- **Native**: C++ (JNI, NativeBridge)
- **UI**: Material Design, ThemeUtils

## 项目结构
- `app/src/main/java`: Java 源代码，包含 UI 片段和业务逻辑。
- `app/src/main/cpp`: C++ 源代码，负责与内核通信的 NativeBridge。
- `app/src/main/res`: 资源文件，包含布局、字符串和主题。

## 如何使用
1. 编译生成 APK 并安装到已补丁 X-KRoot 内核的设备。
2. 输入正确的 Root 密钥进行激活。
3. 在应用中管理其他应用的 Root 权限。

---
更多信息请访问项目主页: [X-KRoot GitHub](https://github.com/X-KRoot/X-KRoot)
