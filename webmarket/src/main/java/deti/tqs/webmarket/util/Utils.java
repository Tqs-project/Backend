package deti.tqs.webmarket.util;

import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Log4j2
public class Utils {
    public static Timestamp parseTimestamp(String timestamp) {
        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return new Timestamp(formatter.parse(timestamp).getTime());
        } catch (ParseException e) {
            log.error(String.format("Error parsing timestamp %s", timestamp));
            log.info("Returning timestamp with current time");
            return new Timestamp(System.currentTimeMillis());
        }
    }
}
