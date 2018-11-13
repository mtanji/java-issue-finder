package com.aurea.codeInspection;

import org.junit.Assert;
import org.junit.Test;

public class HashtableInspectionTest {

    @Test
    public void testgetShortName() {
        // Arrange
        HashtableInspection sut = new HashtableInspection();
        // Act
        String result = sut.getDisplayName();
        // Assert
        Assert.assertEquals("'==' or '!=' instead of 'equals()'", result);
    }
}
