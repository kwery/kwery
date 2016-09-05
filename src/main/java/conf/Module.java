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

package conf;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import custom.TemplateEngineJsFreemarker;
import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.scheduler.ApplicationStartupScheduler;
import services.scheduler.MysqlQueryRunner;
import services.scheduler.QueryRunner;
import services.scheduler.QueryTaskFactory;

@Singleton
public class Module extends AbstractModule {
    private static Logger logger = LoggerFactory.getLogger(Module.class);

    protected void configure() {
        bind(TemplateEngineJsFreemarker.class);
        bind(Scheduler.class).toProvider(SchedulerProvider.class).asEagerSingleton();
        bind(QueryRunner.class).to(MysqlQueryRunner.class);
        bind(ApplicationStartupScheduler.class);
        install(new FactoryModuleBuilder().build(QueryTaskFactory.class));
    }

    public static class SchedulerProvider implements Provider<Scheduler> {
        private Scheduler scheduler = new Scheduler();

        @Override
        public Scheduler get() {
            return scheduler;
        }
    }
}
