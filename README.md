# General

    - #### Team#: s23-122b-tryna_find_a_teammate

    - #### Names:Yiqun Du /Dylan Vo

    - #### Project 5 Video Demo Link:
        https://youtu.be/69dYNkNgfgE
    - #### Instruction of deployment:
    for master/slave build mvn in movie_rest_api
    for singleins build mvn in proj1singlins/movierestapi
    for master and slave:
    after the deployment of master and slave
    on master run mysqlrouter with simple-router config file in etc
    with command mysqlrouter --log-file=/path/to/logfile.log
    

    - #### Collaborations and Work Distribution:
    Dean: connection pooling, apache, mysql router, master-slave, log processing, readme, aws, ddos mitigation and investigation
    Dylan: jmeter graphs, jmeter tests, apache, master-slave, gcp, aws, ddos mitigation
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
  - the parselog file is the log_processing script, 
  - every time you run it , in this line 
  - "ts_avg, tj_avg = com_av("./scaled/1tBalancedPooling/1tBalancedPoolingServer.txt")"
  - replace the parameter with the filename you try to parse
  - the output will be written to average1.txt file 

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**         | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
| --------------------------------------------- | ---------------------------- | -------------------------- | ----------------------------------- | ------------------------- | ------------ |
| Case 1: HTTP/1 thread                         | /img/single/1tSinglePooling.png   | 194.85879332477535                         | 47.853849563262685                                  | 47.81630769171479                        | ??           |
| Case 2: HTTP/10 threads                       | /img/single/10tSinglePooling.png   | 461.9662126068376                         | 356.31919578217963                                  | 356.222502378406                        | ??           |
| Case 3: HTTPS/10 threads                      | /img/single/10tSinglePoolingHTTPS.png   | 551.6963387124762                         | 340.9500825638343                                  | 340.80429591332364                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling | /img/single/10tSingleNoPooling.png   | 454.9459734964322                         | 310.56679960565083                                  | 310.515937921364                        | ??           |

| **Scaled Version Test Plan**                  | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
| --------------------------------------------- | ---------------------------- | -------------------------- | ----------------------------------- | ------------------------- | ------------ |
| Case 1: HTTP/1 thread                         | /img/scaled/1tBalancedPooling.png   | 196.58694287507848                         | 47.790490450031406                                  | 47.642381506599584                        | ??           |
| Case 2: HTTP/10 threads                       | /img/scaled/10tBalancedPooling.png   | 437.0841369671558                         | 274.82498198004026                                  | 274.7857208742734                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling | /img/scaled/10tBalancedNoPooling.png   | 7148.619433198381                         | 1200.4086951044587                                  | 1200.3575410454368                        |

"10tSingleHttpsPooling:ts_average: 340.9500825638343 tj_average: 340.80429591332364
10tSinglePooling:ts_average: 356.31919578217963 tj_average: 356.222502378406
10tSingleNoPooling:ts_average: 310.56679960565083 tj_average: 310.515937921364
1tSinglePooling:ts_average: 47.853849563262685 tj_average: 47.81630769171479

scaled:
10tBalancedNoPooling:ts_average: 1200.4086951044587 tj_average: 1200.3575410454368
10tBalancedPooling:ts_average: 274.82498198004026 tj_average: 274.7857208742734
1tBalancedPooling:ts_average: 47.790490450031406 tj_average: 47.642381506599584

"
10tSinglePoolingHTTPS: average 551.6963387124762
10tSinglePooling: average461.9662126068376
10tSingleNoPooling:average454.9459734964322
1tSinglePooling: average194.85879332477535
scaled
10tBalancedPooling: average437.0841369671558
1tBalancePooling: average196.58694287507848
10tBalancedNoPooling average7148.619433198381
