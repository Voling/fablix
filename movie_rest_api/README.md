# General

    - #### Team#:

    - #### Names:Yiqun Du /Dylan Vo

    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution:

- # Connection Pooling

  - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.

    movie_rest_api/WebContent/META-INF/context.xml
    all backend servlets that uses connection pooling:
    \_dashboard.java/autosuggest.java
    BrowseServlet.java
    dashboardServlet.java
    login.java
    mobilelogin.java
    movieinsert.java
    movieroute.java
    PaymentServlet.java
    SearchinputsServlet.java
    singlemovieroute.java
    singleStarServlet.java

  - #### Explain how Connection Pooling is utilized in the Fabflix code.

    in context xml
    " <Resource name="jdbc/moviedb"
              auth="Container"
              driverClassName="com.mysql.cj.jdbc.Driver"
              factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
              maxTotal="100" maxIdle="30" maxWaitMillis="10000"
              type="javax.sql.DataSource"
              username="**"
              password="*****"
              url="jdbc:mysql://localhost:3306/moviedb?allowMultiQueries=true&amp;autoReconnect=true&amp;allowPublicKeyRetrieval=true&amp;useSSL=false&amp;cachePrepStmts=true"/>
    "
    we defined a connection pool of 100 total connections with maximum idle 30 connections and maximum wait time 10000 seconds
    in fablix the backend code is different in the sense that in each servlet that needs database connections , in the servlet init function it connects to the above datasource connection pool instead

  - #### Explain how Connection Pooling works with two backend SQL.
    if you look at the current context.xml
    we established two datasources each connecting to a different port in mysql router which sits in the master server.
    Therefore we are maintaining two connection pools, one for read/write connections and one for readonly.
    The backend serlets only connects to readwrite when it needs to update the database.
    in our simple-router ini, the router will route the two connection pools to the appropriate databases.

- # Master/Slave

  - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    readwrite-route (to master only):
    movie_rest_api/src/dashboardServlet.java
    movie_rest_api/src/PaymentServlet.java
    read-route(load balanced between master and slave):
    \_dashboard.java/autosuggest.java
    BrowseServlet.java
    login.java
    mobilelogin.java
    movieinsert.java
    movieroute.java
    SearchinputsServlet.java
    singlemovieroute.java
    singleStarServlet.java
    config fileS:
    context.xml

  - #### How read/write requests were routed to Master/Slave SQL?
    we used mysql router on the master backend
    that routes readonly and readwrite connections seperately:
    readonly is load balanced between master and slave
    readwrite is only sent to master

- # JMeter TS/TJ Time Logs

  - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**         | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
| --------------------------------------------- | ---------------------------- | -------------------------- | ----------------------------------- | ------------------------- | ------------ |
| Case 1: HTTP/1 thread                         | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                      | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                  | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
| --------------------------------------------- | ---------------------------- | -------------------------- | ----------------------------------- | ------------------------- | ------------ |
| Case 1: HTTP/1 thread                         | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling | ![](path to image in img/)   | ??                         | ??                                  | ??                        |

"10tsingleHttpsPOoling:ts_average: 340.9500825638343 tj_average: 340.80429591332364
10tsingleHttpPOOLing:ts_average: 356.31919578217963 tj_average: 356.222502378406
10tsingnopooling:ts_average: 310.56679960565083 tj_average: 310.515937921364
1tsinglepooling:ts_average: 47.853849563262685 tj_average: 47.81630769171479

scaled:
10tnopooling:ts_average: 1200.4086951044587 tj_average: 1200.3575410454368
10twithpooling:ts_average: 274.82498198004026 tj_average: 274.7857208742734
ts_average: 47.790490450031406 tj_average: 47.642381506599584

"
10tsinglepoolinghttps: average 551.6963387124762
10tsinglepooling: average461.9662126068376
10tsinglenopooling:average454.9459734964322
1tsinglepooling: average194.85879332477535
scaled
10tBalancedPooling: average437.0841369671558
1tBalancePooling: average196.58694287507848
10tBalancednoPooling average7148.619433198381
