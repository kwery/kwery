/**
 * Copyright (C) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kwery.conf;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.kwery.custom.TemplateEngineJsFreemarker;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.OneOffSqlQueryTaskSchedulerReaper;
import it.sauronsoftware.cron4j.Scheduler;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.MysqlSqlQueryRunner;
import com.kwery.services.scheduler.PreparedStatementExecutorFactory;
import com.kwery.services.scheduler.QueryTaskSchedulerFactory;
import com.kwery.services.scheduler.ResultSetProcessorFactory;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryRunner;
import com.kwery.services.scheduler.SqlQueryTaskFactory;

@Singleton
public class Module extends AbstractModule {
    protected void configure() {
        bind(TemplateEngineJsFreemarker.class);
        bind(SqlQueryRunner.class).to(MysqlSqlQueryRunner.class);
        bind(SchedulerService.class);
        bind(OneOffSqlQueryTaskSchedulerReaper.class);


        install(new FactoryModuleBuilder().build(SqlQueryTaskFactory.class));
        install(new FactoryModuleBuilder().build(QueryTaskSchedulerFactory.class));
        install(new FactoryModuleBuilder().build(ResultSetProcessorFactory.class));
        install(new FactoryModuleBuilder().build(PreparedStatementExecutorFactory.class));
    }

    @Provides
    protected Scheduler provideScheduler() {
        return new Scheduler();
    }

    @Provides
    protected SqlQueryExecution provideQueryRunExecution() {
        return new SqlQueryExecution();
    }

    @Provides
    protected SqlQueryModel provideQueryRun() {
        return new SqlQueryModel();
    }
}
