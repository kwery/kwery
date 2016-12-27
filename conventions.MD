1. Java, follow Oracle Java conventions.
2. Repository names in hyphen for example this-is-a-grep-repo.
3. Message property files using dot for example convention this.is.a.message.
4. Template files in camel case.
6. Messages set in controller should be full name java style variables appended with M for example
createAdminUserButtonTextM, not createAdmUsrBtnTxtM.
7. Variable names can be small/single alphabet only if the scope of the variable is small, if the variable spans a
large no of lines or is an instance variable, then use full names.
8. Static imports as much as possible.
9. Database table names in singular, user not users.
10. If an action is creating something, name all variables associated with the action as add not create, for example, if an action created users in the table, name the
corresponding controller method as addUser, http path as add-user etc.
11. Package names should be plural.
12. DAO method names are prefixed with get and not find for example getUser not findUser.
13. JavaScript module and file names in hyphen case.
14. Database foreign keys should be suffixed with _fk.
15. Classname should not be verbs.
16. Method name should be verbs.
17. While naming models, each constraint should be on a line of it's own. Column name should be the last annotation in the list.

Fluentlenium test conventions:
1. Organize tests in packages with names of the packages based on components for example datasource, sql-query etc.
2. All base packages should start with the name Abstract.
3. Page classes should begin with the component name and end in page for example DatasourceAddPage.
4. Test names should begin with the feature being tested and suffixed with UiTest for example DatasourceAddUiTest. They should not have page in them.