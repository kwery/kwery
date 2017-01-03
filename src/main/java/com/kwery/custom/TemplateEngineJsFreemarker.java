package com.kwery.custom;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.template.*;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;

@Singleton
public class TemplateEngineJsFreemarker extends TemplateEngineFreemarker {
    @Inject
    public TemplateEngineJsFreemarker(Messages messages, Lang lang, Logger logger, TemplateEngineHelper templateEngineHelper, TemplateEngineManager templateEngineManager, TemplateEngineFreemarkerReverseRouteMethod templateEngineFreemarkerReverseRouteMethod, TemplateEngineFreemarkerAssetsAtMethod templateEngineFreemarkerAssetsAtMethod, TemplateEngineFreemarkerWebJarsAtMethod templateEngineFreemarkerWebJarsAtMethod, NinjaProperties ninjaProperties) throws Exception {
        super(messages, lang, logger, templateEngineHelper, templateEngineManager, templateEngineFreemarkerReverseRouteMethod, templateEngineFreemarkerAssetsAtMethod, templateEngineFreemarkerWebJarsAtMethod, ninjaProperties);
    }

    @Override
    public String getContentType() {
        return "text/javascript";
    }
}
