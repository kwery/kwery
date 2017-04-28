package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import com.kwery.models.SqlQueryEmailSettingModel;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(Parameterized.class)
public class SqlQueryEmailSettingIgnoreLabelTest extends AbstractSqlQueryEmailSettingTest {
    protected boolean ignoreLabel = false;

    public SqlQueryEmailSettingIgnoreLabelTest(boolean ignoreLabel) {
        this.ignoreLabel = ignoreLabel;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true},
                {false},
        });
    }

    @Before
    public void setUp() throws IOException {
        super.setUp();
        SqlQueryEmailSettingModel model = new SqlQueryEmailSettingModel();
        model.setIncludeInEmailAttachment(true);
        model.setIncludeInEmailBody(true);
        sqlQueryModel0.setSqlQueryEmailSettingModel(model);

        if (ignoreLabel) {
            setEmailSetting(true, true, true);
        } else {
            setEmailSetting(true, true, false);
        }
    }

    @Test
    public void test() throws Exception {
        reportEmailSender.send(jobExecutionModel);

        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> !wiserRule.wiser().getMessages().isEmpty());
        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();

        String html = mimeMessageParser.getHtmlContent();

        Document doc = Jsoup.parse(html);

        if (ignoreLabel) {
            Element table = doc.select(".report-content-t").first();
            assertThat(table.attr("style"), containsString("border: none;"));
            assertThat(table.attr("style"), not(containsString("border: 1px solid black;")));

            Element columnLabel = doc.select(".report-label-0-t").first();
            assertThat(columnLabel, nullValue());

            Element row = doc.select(".section-0-row-0-t").first();
            assertThat(row.attr("style"), containsString("border: none;"));
            assertThat(row.attr("style"), not(containsString("border: 1px solid black;")));

            Element col = doc.select(".section-0-row-0-col-0-t").first();
            assertThat(col.attr("style"), containsString("border: none; font-size: x-large"));
            assertThat(col.attr("style"), not(containsString("border: 1px solid black;")));
        } else {
            Element table = doc.select(".report-content-t").first();
            assertThat(table.attr("style"), containsString("border: 1px solid black"));
            assertThat(table.attr("style"), not(containsString("border: none;")));

            Element columnLabel = doc.select(".report-label-0-t").first();
            assertThat(columnLabel, notNullValue());

            Element row = doc.select(".section-0-row-0-t").first();
            assertThat(row.attr("style"), containsString("border: 1px solid black;"));
            assertThat(row.attr("style"), not(containsString("border: none;")));

            Element col = doc.select(".section-0-row-0-col-0-t").first();
            assertThat(col.attr("style"), containsString("border: 1px solid black;"));
            assertThat(col.attr("style"), not(containsString("border: none; font-size: x-large;")));
        }
    }
}
