package deti.tqs.webmarket.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class UtilsTest {

    @Test
    void parseTimestampTest() {
        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var timestamp = "2021-05-30 17:00:00";

        Timestamp testTimestamp;
        try {
            testTimestamp = new Timestamp(formatter.parse(timestamp).getTime());
        } catch (ParseException e) {
            testTimestamp = new Timestamp(System.currentTimeMillis());
        }

        Assertions.assertThat(
                Utils.parseTimestamp(timestamp)
        ).isEqualTo(testTimestamp);
    }
}