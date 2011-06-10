package de.halbekunst.versuch.util;

import java.util.Calendar;
import org.springframework.stereotype.Component;

/**
 * UpdateTracker fängt Application-Events ab, die Veränderungen an der
 * Bilddatenbank kund tun und aktualisiert einen globalen Zeitstempel für
 * Änderungen entsprechend
 *
 * TODO: Events auch tatsächlich senden und abfangen ;)
 * @author kai
 */
@Component
public class UpdateTracker {
  private long lastModified;
  private String eTag;


  public UpdateTracker() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    lastModified = calendar.getTimeInMillis();
    eTag = Long.toString(lastModified);
  }


  public long getLastModified() {
    return lastModified;
  }

  public String getETag() {
    return eTag;
  }
}
