package de.tu_berlin.dima.niteout.routing;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Created by Thomas on 02.02.17.
 * Basic test of formatting date time.
 */
public class LocalDateTimeTest {

    @Test
    public void localDateTimeWithoutSecondsToStringShouldReturnStringContainingSeconds() {
        // fails: String date = LocalDateTime.now().withSecond(0).withNano(0).toString();
        // fails: String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String date = LocalDateTime.now().format(HereWrapper.ISO_LOCAL_DATE_TIME);
        assertEquals(date.length(), 19);
    }
}
