# 外卖平台


#### 后端技术栈 Spring Boot、Mybatis plus、MySQL、Redis

#### 描述
因为自己后端开发接口的基础功不扎实，例如常用的业务方法，一切业务逻辑不懂得处理而做的这个项目。  
项目后端有将近60个接口，开发周期大概是一周，但对于项目经验少或者没有真正自己实现过功能的同学，比较有意义。  
因为本人之前有实习所以开发速度还不算慢。  
该项目前端做的很规范，当你开发到另一接口你会神奇的发现接口复用了。  
也可能是我实习公司的前端代码太差了（前后端、测试 都是后端做），经常是copy了前端再copy接口，前端相当规范的代码让我在后端开发过程很顺利。


<hr>

#### 运行本项目
1. 项目代码导入依赖。
2. 文件：数据库 sql、D:\img下该有的图片都在项目根目录。
3. 环境要求：jdk8 、MySQL 5.6、redis（一个包）。

#### 开发过程遇到的一些小问题
1. 有时候传入属性的默认值是null，而被当作0处理可能会有空指针异常。
2. maven 依赖一直在导入就看看镜像源，然后再看看 jdk 版本，然后就是 idea 版本。实在不行就重开 idea。
3. 大部分时间耗在传入数据不成功。  
例如传入 json 格式数据，后端要用 @RequestBody 注解在对应的参数上，返回 json 就是 @ResponseBody ，请求方式 + @ResponseBody 就是对应的 @GetMapping、@PostMapping（新增）、@PutMapping（修改）、@DeleteMapping。  
还有在路径中的参数一般都是在 @RequestMapping 请求路径后 {参数名} 下面参数列表就是对应的 @PathVarible ，参数名称要对应前台，有时候还用 Map 来接受参数，对于一些可能存在可能不存在参数，只需要在构造器的 eq 的第一个参数加上判断。
4. StringUtils 中的 isNotEmpty 和 String 中的 isEmpty 不同，前者会判空，而后者不会，坑了我好一会。

#### 我的收获
- 其中 lombok 的 @Data 放在实体类自动生成set、get方法是我没用过的，该项目严格遵循了 restful 风格。  
- mp 也是第一次用，mapper 继承 BaseMapper<实体类名>、service 继承 IService<实体类名>、serviceImpl 继承 ServiceImpl<对应mapper,实体类名>，真的是让我没有写一句 sql，全在 service 层用内置方法解决了。万能的构造查询器！！！  
- 繁琐一点的就是返回 DTO 的 list ，要 copy 除了 records 以外的所有信息，再改造 records 再传入。  
- implements MetaObjectHandler 来实现公共字段的填充（覆盖重载 insertFill，updateFill），比如 createDate、updateDate、createUser、updateUser 可以统一处理，  
- 一次请求对应一个线程，取 id 不一定要登录 request.Session.setAttribute，可以放在 ThreadLocal 中用 BaseContext get、set id。  
- BeanUtils 下的 copy... 方法很好用，避免了一个一个去 get再set ，而且第三个参数就是不需要的参数，避免 set。
- 登陆用的 session ，登录的验证码生成的配置类不需要记忆，随取随用，还有就是 发送验证码的，用就导依赖、加配置类、调用api 。
- redis 也是随用随导包、配置类 然后用 @AutoWired 导入 RedisTemplate 然后写逻辑......
- common 类里是 上传、下载的逻辑，主要就是拿到源文件的 后缀，uuid 随机生成文件名还有创建文件夹（路径名从 yml 配置文件中导入）等操作。
- implements Filter 来生成一个自动过滤器，对未登录状态的用户禁止访问一些页面。 注意 PATH_MATCHER.match 的参数不要反了，不然很难找 bug 。
- 定义一个 mp 分页插件的配置类
``` java
@Configuration
public class MybatisPlusConfig {//分页插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }

}
```
- 定义一个 WebMvcConfigurationSupport 和 一个 JacksonObjectMapper extends ObjectMapper  来静态资源映射，并且拓展默认的 **消息转换器**，因为前端传输过来的 long 类型的 id 精度丢失了。
- 定义好返回类型的实体，在每一次返回都有 响应码 和 返回数据 or 报错信息。
- 定义全局异常处理器和自定义异常。
- 可以用一些工具来自动生成接口文档，

#### 尚存的几个问题 
1. 客户端的 《最新订单》 内商品个数的回显。
2. 我的订单 --> 每次都请求两次 page 接口。
3. 地址管理 --> （修改地址）标签回显。  
注：因为需要前端的介入，所以一直没有解决。
