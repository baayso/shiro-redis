# shiro-redis

shiro only provide the support of ehcache and concurrentHashMap. Here is an implement of redis cache can be used by shiro. Hope it will help you!

How to use it?
===========

You can choose these 2 ways to include shiro-redis into your project
* use "git clone https://github.com/alexxiyang/shiro-redis.git" to clone project to your local workspace and build jar file by your self
* add maven dependency

How to configure ?
===========
You can choose 2 ways : shiro.ini or spring-*.xml

shiro.ini:

```properties
#redisManager
redisManager = com.baayso.shiro.redis.RedisManager
#optional if you don't specify host the default value is 127.0.0.1
redisManager.host = 127.0.0.1
#optional , default value: 6379
redisManager.port = 6379
#optional, default value:0 .The expire time is in second
redisManager.expire = 30
#optional, timeout for jedis try to connect to redis server(In milliseconds), not equals to expire time! 
redisManager.timeout = 0
#optional, password for redis server
redisManager.password = 

#============redisSessionDAO=============
redisSessionDAO = com.baayso.shiro.redis.RedisSessionDAO
redisSessionDAO.redisManager = $redisManager
sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager
sessionManager.sessionDAO = $redisSessionDAO
securityManager.sessionManager = $sessionManager

#============redisCacheManager===========
cacheManager = com.baayso.shiro.redis.RedisCacheManager
cacheManager.redisManager = $redisManager
#custom your redis key prefix, if you doesn't define this parameter shiro-redis will use 'shiro_redis_session:' as default prefix
cacheManager.keyPrefix = users:security:authz:
securityManager.cacheManager = $cacheManager
```

spring.xml:
```xml
<!-- shiro filter -->
<bean id="ShiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
	<property name="securityManager" ref="securityManager"/>
	<!--
	<property name="loginUrl" value="/login.jsp"/>
	<property name="successUrl" value="/home.jsp"/>  
	<property name="unauthorizedUrl" value="/unauthorized.jsp"/>
	-->
	<!-- The 'filters' property is not necessary since any declared javax.servlet.Filter bean  -->
	<!-- defined will be automatically acquired and available via its beanName in chain        -->
	<!-- definitions, but you can perform instance overrides or name aliases here if you like: -->
	<!-- <property name="filters">
		<util:map>
			<entry key="anAlias" value-ref="someFilter"/>
		</util:map>
	</property> -->
	<property name="filterChainDefinitions">
		<value>
			/login.jsp = anon
			/user/** = anon
			/register/** = anon
			/unauthorized.jsp = anon
			/css/** = anon
			/js/** = anon
			/** = authc
		</value>
	</property>
</bean>

<!-- shiro securityManager -->
<bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
	<!-- Single realm app.  If you have multiple realms, use the 'realms' property instead. -->
	<!-- sessionManager -->
	<property name="sessionManager" ref="sessionManager" />
	<!-- cacheManager -->
	<property name="cacheManager" ref="cacheManager" />
	<!-- By default the servlet container sessions will be used.  Uncomment this line
		 to use shiro's native sessions (see the JavaDoc for more): -->
	<!-- <property name="sessionMode" value="native"/> -->
</bean>

<bean id="lifecycleBeanPostProcessor" class="org.apache.shiro.spring.LifecycleBeanPostProcessor"/>	

<!-- shiro redisManager -->
<bean id="redisManager" class="com.baayso.shiro.redis.RedisManager">
	<property name="host" value="127.0.0.1"/>
	<property name="port" value="6379"/>
	<property name="expire" value="1800"/>
	<!-- optional properties:
	<property name="timeout" value="10000"/>
	<property name="password" value="123456"/>
	-->
</bean>

<!-- redisSessionDAO -->
<bean id="redisSessionDAO" class="com.baayso.shiro.redis.RedisSessionDAO">
	<property name="redisManager" ref="redisManager" />
</bean>

<!-- sessionManager -->
<bean id="sessionManager" class="org.apache.shiro.web.session.mgt.DefaultWebSessionManager">
	<property name="sessionDAO" ref="redisSessionDAO" />
</bean>

<!-- cacheManager -->
<bean id="cacheManager" class="com.baayso.shiro.redis.RedisCacheManager">
	<property name="redisManager" ref="redisManager" />
</bean>
```

> NOTE
> Shiro-redis don't support SimpleAuthenticationInfo created by this constructor `org.apache.shiro.authc.SimpleAuthenticationInfo.SimpleAuthenticationInfo(Object principal, Object hashedCredentials, ByteSource credentialsSalt, String realmName)`.
> Please use `org.apache.shiro.authc.SimpleAuthenticationInfo.SimpleAuthenticationInfo(Object principal, Object hashedCredentials, String realmName)` instead.

