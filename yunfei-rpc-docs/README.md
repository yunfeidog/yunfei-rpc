## 1.项目介绍
这是一个手写RPC框架项目  
造轮子主要是为了学习，因为我觉得"会用"、"会读源码"、"会写出来"是完全不一样的水平。
## 2.如何安装

1. 使用npm安装(推荐)

```shell
npm install yunfei-docs-template-npm -g
````

然后在项目根目录执行以下命令(app是项目名称，可以自定义)

```shell
yunfei-docs-template-npm create app
```

2. 使用git安装

```shell
git clone git@github.com:yunfeidog/docs-template.git
```

然后删除 `yunfei-docs-template-npm`文件夹

## 3.如何运行

安装依赖：

```shell
pnpm install
```

运行：

```shell
pnpm run docs:dev
```

## 4.配置说明

+ 网站的信息只需要在`src/.vuepress/custom.ts`文件中修改即可

## todo

| 任务              | 状态 |
|-----------------|----|
| 自动部署Github Page | ✅  |
| 发布到npm仓库        | ✅  |
| 终端配置用户信息        | ❌  |
| 搜索插件            | ✅  |


