# kwery
Kwery makes it simple to generate, schedule, view and email business reports from datasources using SQL.

### What is the problem that Kwery solves?
All businesses have a need of generating reports from data sources at a particular frequency - hourly, daily, weekly etc and email these reports. Kwery makes this a breeze and self-serve.

### Getting Started
1.  [Download](https://github.com/kwery/kwery/releases) the jar.
2.  Run the jar `java -jar kwery.jar`.
3.  Go to browser and load localhost:8080.

For more information see the [Kwery documentation](https://documentation.getkwery.com/#installation) and follow the simple setup process. 

Kwery comes packaged with all the components needed to run. The only system requirement is the availability of Java 8 runtime.

### Kwery's database
Kwery uses [Apache Derby](http://db.apache.org/derby/) as it's database. 

To peek at the database, you can use [ij](https://builds.apache.org/job/Derby-docs/lastSuccessfulBuild/artifact/trunk/out/tools/ctoolsij32837.html) utility. Since Kwery uses Derby in embedded mode, at a time, only one application can access the database. Hence, you need to shutdown Kwery if you want to access Kwery's database through `ij`.

Accessing Kwery's database through `ij`:
1.  Go the the directory where you are running Kwery.
1.  Ensure that there is a directory called `kwery_db`.
1.  Start `ij`.
1.  Run `connect 'jdbc:derby:kwery_db';`.

> Please ensure you know what you are doing, you might end up deleting data inadvertantly and prevent Kwery for starting  again.



