package controllers;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CreateAdminUserFluentLeniumTest extends DashRepoFluentLeniumTest {
    @Test
    public void test() throws InterruptedException {
        String username = "purvi";
        String password = "password";

        goTo(getServerAddress() + "#onboarding/create-admin-user");
        await().atMost(5, TimeUnit.SECONDS).until("#username").isPresent();
        fill($("#username")).with(username);
        fill($("#password")).with(password);
        click($("#createAdminUser"));

        String successMessage = String.format("Admin user with user name %s created successfully", username);
        await().atMost(5, TimeUnit.SECONDS).until(".isa_info p").hasText(successMessage);

        assertEquals("User creation success message", successMessage, $(".isa_info p").getText());

        click($("#createAdminUser"));

        String userExistsMessage = String.format("An admin user with user name %s already exists, please choose a different username", username);

        await().atMost(5, TimeUnit.SECONDS).until(".isa_error p").hasText(userExistsMessage);

        assertEquals("User already exists message", userExistsMessage, $(".isa_error p").getText());
    }
}
