---
home: true
icon: home
title: 个人主页
heroImage: /logo.png
heroText: 手写RPC框架实现
tagline: ✨为啥造轮子？学习？我觉得"会用"✨<br>✨"会读源码"、"会写出来"是完全不一样的水平✨
bgImage: https://theme-hope-assets.vuejs.press/bg/6-light.svg
bgImageDark: https://theme-hope-assets.vuejs.press/bg/6-dark.svg
bgImageStyle:
  background-attachment: fixed
actions:
  - text: 【RPC设计文档】
    link: /docs/
    type: primary
  - text: 【优化与改进】
    link: /improvement/ 
    type: secondary
  - text: 【关于我】
    link: http://yunfei.plus/

highlights:
  - header: 易于安装
    image: /assets/image/box.svg
    bgImage: https://theme-hope-assets.vuejs.press/bg/3-light.svg
    bgImageDark: https://theme-hope-assets.vuejs.press/bg/3-dark.svg
    highlights:
      - title: 导入maven <code>yunfei-rpc-spring-boot-starter</code> 以下载项目依赖。
      - title: 再添加 <code>EnableYunRpc、YunRpcReference、YunRpcService</code> 即可使用。

  - header: 简单的配置
    description: 我们提供了强大的SPI机制进行扩展，只需简单的配置即可实现自定义的序列化、负载均衡、服务发现等功能。
    image: /assets/image/markdown.svg
    bgImage: https://theme-hope-assets.vuejs.press/bg/2-light.svg
    bgImageDark: https://theme-hope-assets.vuejs.press/bg/2-dark.svg
    bgImageStyle:
      background-repeat: repeat
      background-size: initial
    features:
      - title: 序列化器
        details: Hessain、Kryo、Protostuff等
        link: /docs/序列化器实现.md
      - title: 负载均衡
        details: 轮询、随机、一致性哈希等
        link: /docs/负载均衡器实现.md
      - title: 动态代理
        details: JDK、CGLIB(未实现)
        link: /docs/RPC调用(代理).md
      - title: SPI机制
        details: 服务发现、序列化、负载均衡等
        link: /docs/SPI服务发现机制实现.md
      - title: 注解驱动
        details: YunRpcService、YunRpcReference、EnableYunRpc
        link: /docs/SpringBoot注解驱动设计.md
      - title: 容错机制
        details: 快速失败、失败安全、降级等
        link: /docs/容错机制.md
      - title: 重试机制
        details: 线性、指数退避、固定间隔等
        link: /docs/重试机制.md
      - title: 自定义协议
        details: TCP、消息结构设计
        link: /docs/自定义协议(重点).md
---
