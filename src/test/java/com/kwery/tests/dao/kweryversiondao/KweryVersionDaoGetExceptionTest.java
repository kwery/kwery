package com.kwery.tests.dao.kweryversiondao;

import org.junit.Before;
import org.junit.Test;

public class KweryVersionDaoGetExceptionTest extends KweryVersionDaoGetTest {
    @Before
    public void setUp() {
        super.setUp();
        super.setUp();
    }

    @Test(expected = IllegalStateException.class)
    public void test() {
        kweryVersionDao.get();
    }
}
