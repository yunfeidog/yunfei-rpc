import {defineUserConfig} from "vuepress";
import {registerComponentsPlugin} from "@vuepress/plugin-register-components";
import theme from "./theme.js";
import viteBundler from "@vuepress/bundler-vite";
import {siteConfig} from "./custom";


//自定义用户配置
export default defineUserConfig({
    //@ts-ignore
    base: siteConfig.base,
    title: siteConfig.title,
    description: siteConfig.description,
    // 设置favicon
    head: [["link", {rel: "icon", href: "/favicon.svg"}]],
    bundler: viteBundler({
        viteOptions: {},
        vuePluginOptions: {},
    }),
    // 主题设置
    theme,
    plugins: [
        // 注册全局组件的插件
        registerComponentsPlugin({}),
    ],

    shouldPrefetch: false,
});
