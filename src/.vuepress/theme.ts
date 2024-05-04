import {hopeTheme} from "vuepress-theme-hope";
//中文导航栏
import {Navbar} from "./navbar";
//中文侧边栏
import {Sidebar} from "./sidebar";
import {siteConfig} from "./custom";

// 主题设置
export default hopeTheme({
    ...siteConfig,
    // 博客配置
    blog: {},
    // 是否在导航栏内显示仓库链接-默认为true
    repoDisplay: true,
    // 导航栏布局
    navbarLayout: {
        start: ["Brand"],
        center: ["Links"],
        end: ["Language", "Repo", "Outlook", "Search"],
    },

    // 页面显示信息
    pageInfo: ["Category", "Tag", "ReadingTime", "Author", "Original"],

    // 路径导航
    breadcrumb: true,

    // 路径导航的图标显示
    breadcrumbIcon: true,

    // 用户可以自定义的多主题色
    themeColor: true,
    // 暗黑模式切换-在深色模式和浅色模式中切换
    darkmode: "toggle",
    // 全屏按钮
    fullscreen: true,
    // 纯净模式-禁用
    pure: true,

    // 文章的最后更新时间
    lastUpdated: true,

    // 显示页面的贡献者
    contributors: false,

    // 文章所在目录
    docsDir: "src",
    navbar: Navbar,
    sidebar: Sidebar,


    // 显示页脚
    displayFooter: true,

    // 页面配置信息
    metaLocales: {
        editLink: "在【Github】上编辑此页",
    },
    prevLink: true,
    nextLink: true,


    plugins: {
        searchPro: {
            autoSuggestions: true,
        },
        // 在MD文件中启用的组件
        components: {
            // 你想使用的组件
            components: [
                "Badge",
                "BiliBili",
                "CodePen",
                "PDF",
                "Share",
                "SiteInfo",
                "StackBlitz",
                "VPBanner",
                "VPCard",
                "XiGua",
            ],
        },
        // 代码复制功能-vuepress-plugin-copy-code2
        copyCode: {
            // 在移动端也可以实现复制代码
            showInMobile: true,
            // 代码复制成功提示消息的时间-ms
            duration: 3000,
            // 纯净模式
        },
        // MarkDown文件增强
        mdEnhance: {
            align: true,
            attrs: true,
            codetabs: true,
            hint: true,
            demo: true,
            gfm: true,
            imgSize: true,
            include: true,
            // lazyLoad: true,
            mark: true,
            playground: {
                presets: ["ts", "vue"],
            },
            stylize: [
                {
                    matcher: "Recommanded",
                    replacer: ({tag}) => {
                        if (tag === "em")
                            return {
                                tag: "Badge",
                                attrs: {type: "tip"},
                                content: "Recommanded",
                            };
                    },
                },
            ],
            sub: true,
            sup: true,
            tabs: true,
            // vpre: true,
            // vuePlayground: true,
        },
        // 打开博客功能
        blog: {
            // 在文章列表页面自动提取文章的摘要进行显示
            excerptLength: 200,
        },
        // 开启git实现编辑此页面-最后更新时间-贡献者功能
        git: true,
        // 关闭sitemap插件
        sitemap: false,
    },
});
