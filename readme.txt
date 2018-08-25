#AppBasicArchiTecture

*author: user_zf*
*date: 2018.08.24*
*desc: AppBasicArchiTecture说明文档*

###项目说明
该项目旨在搭建一个App基础框架，任何App都可以基于此框架进行快速开发。
该框架包括组件化项目结构，采用MVP设计模式。
并且包含一些基类、工具类，还有一些简单的demo作为开发参考。

###项目设计到的技术
- 组件化方案：JIMU（https://github.com/mqzhangw/JIMU）
- 日志工具：logger（https://github.com/orhanobut/logger）

###整体项目结构
该项目采用JIMU组件化方案，搭建基础项目结构，包括以下几个Module:

                  app
                   + ++++++++++++++++++++++++++++++++++++++++++
                   +                                          +
         user      +     comp1  comp2 ... +++++++++ router-anno-compile(jimu)
             +     +     +      +
                +  +    +    +
                routerservice
                   +
                   +
                basicres
                   +
                   +
                basiclib
                   +
                   +
                componentlib(jimu)
                   +
                   +
                router-annotation(jimu)

- app：项目主module，开发过程不依赖于其他组件，集成打包时依赖其他组件。
- user：用户组件，主要包含用户注册登录、用户信息管理，对外提供用户相关的接口。开发过程可单独运行，发布时作为app依赖的lib
- comp：其他模块组件，用法同user组件
- routerservice：路由服务模块，定义每个组件对外提供的服务，还有各组件需要用到的常量配置
- basicres：基础资源库，定义App的公共资源
- basiclib：基础库，配置常用的三方库依赖，定义自己的工具类和一些基类，可以轻松转移到任何App中
- componentlib：JIMU库中的路由方案，包括UIRouter负责页面跳转，Router为组件提供服务
- router-annotation：JIMU的注解库，包含两个组件，RouteNode为Activity提供到可跳转，Autowired为Activity自动填充参数
- router-anno-compile：JIMU注解解析库，主要负责解析RouteNode和Autowired两个注解

###当个组件的目录结构
包含积木的runalone

