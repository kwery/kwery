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
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobTaskFactory;
import com.kwery.services.job.SchedulerListenerImpl;
import com.kwery.services.job.TaskExecutorListenerImpl;
import com.kwery.services.migration.ResultMigrator;
import com.kwery.services.scheduler.JsonToHtmlTableConverterFactory;
import com.kwery.services.scheduler.PreparedStatementExecutorFactory;
import com.kwery.services.scheduler.ResultSetProcessorFactory;
import com.kwery.utils.KweryDirectoryFactory;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.TaskExecutorListener;

@Singleton
public class Module extends AbstractModule {
    protected void configure() {
        bind(SchedulerListener.class).to(SchedulerListenerImpl.class);
        bind(TaskExecutorListener.class).to(TaskExecutorListenerImpl.class);
        install(new FactoryModuleBuilder().build(ResultSetProcessorFactory.class));
        install(new FactoryModuleBuilder().build(PreparedStatementExecutorFactory.class));
        install(new FactoryModuleBuilder().build(JobTaskFactory.class));
        install(new FactoryModuleBuilder().build(com.kwery.services.job.SqlQueryTaskFactory.class));
        install(new FactoryModuleBuilder().build(JsonToHtmlTableConverterFactory.class));
        install(new FactoryModuleBuilder().build(KweryDirectoryFactory.class));
        bind(ResultMigrator.class);
    }

    @Provides
    protected Scheduler provideScheduler() {
        return new Scheduler();
    }

    @Provides
    protected SqlQueryExecutionModel provideQueryRunExecution() {
        return new SqlQueryExecutionModel();
    }

    @Provides
    protected SqlQueryModel provideQueryRun() {
        return new SqlQueryModel();
    }
}
