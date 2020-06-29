package com.cryptext.utils;

import static com.cryptext.utils.StringMethods.containsIgnoreCase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class StringMethodsTest {

    private final static String TEST_PATH = "/test_path/file_name";
    private final StringMethods stringMethods = StringMethods.getInstance();

    @Test
    public void When_CheckExtensionIsCalled_Expect_CorrectStringReturned() {
        assertThat(stringMethods.checkExtension(TEST_PATH), is("/test_path/file_name.enc"));
        assertThat(stringMethods.checkExtension(".enc"), is(".enc"));
    }

    @Test
    public void When_GetNameDBIsCalled_Expect_CorrectStringReturned() {
        assertThat(stringMethods.getNameDB(TEST_PATH), is("file_name"));
    }

    @Test
    public void When_ContainsIgnoreCaseIsCalled_Expect_CorrectResult() {
        assertThat(containsIgnoreCase("STRING1", "StRiNg1"), is(true));
        assertThat(containsIgnoreCase("STRING1", "StRiNg12"), is(false));
    }
}