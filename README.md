### 技术栈

- Spring Boot、MySQL
- Spring、Spring MVC、MyBatis
- Redis、Kafka、Elasticsearch、Zookeeper
- Spring Security、Spring Actuator
- Caffeine
- Thymeleaf

### 功能

这个项目主要是用于社交的网站，类似于微博。

实现了用户的注册、登录、发帖、评论、搜索、私聊等基本功能。

- 引入了redis数据库来提升网站的整体性能，实现了用户凭证的存取、点赞和关注的功能。
- 基于 Kafka 实现了系统通知：当用户获得点赞、评论或关注后得到通知。
- 用ES存储标题和文章，实现对博客的全文搜索，准确匹配搜索结果，并高亮显示关键词
- 利用Quartz定时任务定期计算帖子的分数，并在页面上展现热帖排行榜。
