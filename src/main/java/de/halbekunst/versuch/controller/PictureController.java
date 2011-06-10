package de.halbekunst.versuch.controller;

import de.halbekunst.fotos.model.Picture;
import de.halbekunst.fotos.model.User;
import de.halbekunst.fotos.service.PictureService;
import de.halbekunst.fotos.service.PictureService.GrantedAccess;
import de.halbekunst.utils.cachecontrol.Cacheable;
import de.halbekunst.versuch.util.PicturesPage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author kai
 */
@Controller
@Transactional(readOnly = true)
public class PictureController implements Cacheable, InitializingBean {

  private final static Logger log = LoggerFactory.getLogger(PictureController.class);
  private final static String MATCHER = PictureController.class.getCanonicalName() + "_MATCHER";
  private final static String PICTURE = PictureController.class.getCanonicalName() + "_PICTURE";
  private final static String ACCESS = PictureController.class.getCanonicalName() + "ACCESS";
  private final static int POS_SIZE = 1;
  private final static int POS_MD5SUM = 2;
  private final static int POS_SUFFIX = 3;
  private Pattern pattern = Pattern.compile("(?:/([0-9]{2,4}))?/([0-9a-f]{32,32})(?:\\.(jpg|jpeg|tiff|bmp|png|html))?");
  private Semaphore available;

  @Autowired PictureService service;
  @Autowired Float quality;
  @Autowired Integer max;
  @Autowired Integer wait;

  private int maxAge;
  private String serverMaxAge;


  @Autowired
  @Qualifier("maxAge")
  public void setMaxAge(Integer seconds) {
    maxAge = seconds.intValue();
  }

  @Autowired
  @Qualifier("serverMaxAge")
  public void setServerMaxAge(Integer seconds) {
    serverMaxAge = seconds.toString();
  }


  @Override
  public void afterPropertiesSet() {
    available = new Semaphore(max, true);
  }


  @Override
  public boolean accepts(HttpServletRequest request) {
    Matcher matcher = pattern.matcher(request.getRequestURI());
    if (matcher.find()) {
      Picture picture = service.get(matcher.group(POS_MD5SUM));
      if (picture == null) {
        log.info("Bild unbekannt: {}", matcher.group(POS_MD5SUM));
        return false;
      }
      request.setAttribute(MATCHER, matcher);
      request.setAttribute(PICTURE, picture);
      return true;
    }
    else {
      log.info("Passt nicht: {}", request.getRequestURI());
      return false;
    }
  }

  @Override
  public boolean isGenerateCacheHeaders(HttpServletRequest request) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = principal instanceof User ? (User)principal : null;
    Picture picture = (Picture)request.getAttribute(PICTURE);
    GrantedAccess access = service.hasAccess(user, picture);
    log.debug("{}: {} -> {}", new Object[] { access, user, picture });

    // Gewährten Zugriff für spätere Entscheidungen merken
    request.setAttribute(ACCESS, access);

    // Wenn kein Zugriff gewährt wird, sollen auch keine Cache-Header generiert werden!
    return access != GrantedAccess.NO_ACCESS;
  }

  @Override
  public int getCacheSeconds(HttpServletRequest request) {
    return maxAge;
  }

  @Override
  public long getLastModified(HttpServletRequest request) {
    Picture picture = (Picture) request.getAttribute(PICTURE);
    return picture.getModified().getTime();
  }

  @Override
  public String getETag(HttpServletRequest request) {
    Picture picture = (Picture) request.getAttribute(PICTURE);
    return picture.getVersion().toString();
  }

  @Override
  public Map<String, String> getCacheControl(HttpServletRequest request) {
    Map cacheControl = new HashMap<String, String>();
    cacheControl.put("public", null); // Darf von shared und non-shared Caches gespeichert werden
    cacheControl.put("s-max-age", serverMaxAge);
    if (GrantedAccess.RESTRICTED_ACCESS.equals(request.getAttribute(ACCESS))) {
      /**
       * Auch shared Caches dürfen ein Bild mit zugangsbeschränkung
       * zwischenspeichern, damit es nicht bei jedem Zugriff neu erzeugt
       * werden muss -- allerdings nur unter der Vorgabe, dass sie jeden
       * neuen Zugriff gegen den Origin-Server revalidieren!
       * (Siehe dazu: http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4)
       */
      cacheControl.put("proxy-revalidate", null);
    }
    return cacheControl;
  }


  /**
   * Verrückt, aber wahr:
   * Der Servlet-Anteil wird beim Mapping nicht berücksichtigt.
   */
  @RequestMapping("/pictures/**")
  public ModelAndView renderPicture(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      Matcher matcher = (Matcher) request.getAttribute(MATCHER);
      GrantedAccess access = (GrantedAccess) request.getAttribute(ACCESS);

      if (GrantedAccess.NO_ACCESS.equals(access)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return null;
      }

      /**
       * TODO:
       * Wenn das im Request gespeicherte Bild verwendet wird, führt das
       * zu einer LazyInitializationException beim Zugriff auf Picture.exifData.
       * Die Frage ist, warum die automatische Wiederverknüpfung hier scheitert,
       * aber beim OpenSessionInViewFilter funktioniert...
       *
       * Als Workaround wird das Bild hier einfach neu aus der Datenbank
       * geholt :(
       */
      Picture picture = service.get(matcher.group(POS_MD5SUM));

      String suffix = matcher.group(POS_SUFFIX);
      String size = matcher.group(POS_SIZE);

      if (size == null) {
        /** Bild unverändert schicken */
        if (!picture.getSuffix().equals(suffix)) {
          log.debug("{}: Bild war vom Typ {}, gefordert {}", new Object[]{matcher.group(POS_MD5SUM), picture.getSuffix(), suffix});
          response.sendError(HttpServletResponse.SC_NOT_FOUND);
          return null;
        }
        response.setContentType(picture.getMimeType());
        InputStream is = null;
        try {
          is = service.fetch(picture);
          IOUtils.copy(is, response.getOutputStream());
        }
        finally {
          if (is != null) {
            is.close();
          }
        }
        return null;
      }

      Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(suffix);
      if (!writers.hasNext()) {
        log.info("Bildformat unbekannt: {}", suffix);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
      }

      response.setContentType("image/" + suffix);

      int width = picture.getWidth();
      int height = picture.getHeight();
      int center = width > height ? height / 2 : width / 2;
      int longEdge, shortEdge;
      double scale = 1.;

      if (width > height) {
        longEdge = width;
        shortEdge = height;
      }
      else {
        longEdge = height;
        shortEdge = width;
      }

      AffineTransform transformation = new AffineTransform();

      if (size != null) {
        scale = Double.parseDouble(size) / (double) longEdge;
        transformation.scale(scale, scale);
        width = (int) Math.round((double) width * scale);
        height = (int) Math.round((double) height * scale);
      }

      /**
       * Automatisch Rotation des Bildes entsprechend der in den EXIF-Daten
       * gespeicherten Orientierung.
       *
       * Siehe z.B.:
       * http://www.impulseadventure.com/photo/exif-orientation.html
       * http://sylvana.net/jpegcrop/exif_orientation.html
       *
       * Value  0th Row     0th Column
       * -----------------------------
       * 1      top         left side
       * 2      top         right side
       * 3      bottom      right side
       * 4      bottom	    left side
       * 5      left side   top
       * 6      right side  top
       * 7      right side  bottom
       * 8      left side   bottom
       * -----------------------------
       *
       * Read this table as follows (thanks to Peter Nielsen for clarifying this -
       * see also below):
       * Entry #6 in the table says that the 0th row in the stored image is the
       * right side of the captured scene, and the 0th column in the stored image
       * is the top side of the captured scene.
       *
       *   1        2       3      4         5            6           7          8
       *
       * 888888  888888      88  88      8888888888  88                  88  8888888888
       * 88          88      88  88      88  88      88  88          88  88      88  88
       * 8888      8888    8888  8888    88          8888888888  8888888888          88
       * 88          88      88  88
       * 88          88  888888  888888
       *
       *
       * Die Orientierungen werden in der Reihenfolge der Häufigkeiten geprüft
       */
      String orientation = picture.getExifData().get("Orientation");
      if (orientation != null) {
        int tmp;
        try {
          switch (Integer.parseInt(orientation)) {

            case 2:
              transformation.scale(1, -1);
              transformation.translate(0, -height);
            case 1:
              break;

            case 5:
              transformation.scale(1, -1);
              transformation.translate(0, -height);
            case 8:
              transformation.translate(0., (double) (longEdge - shortEdge));
              transformation.rotate(Math.toRadians(-90), center, center);
              tmp = width;
              width = height;
              height = tmp;
              break;

            case 7:
              transformation.scale(1, -1);
              transformation.translate(0, -height);
            case 6:
              transformation.rotate(Math.toRadians(90), center, center);
              tmp = width;
              width = height;
              height = tmp;
              break;

            case 4:
              transformation.scale(1, -1);
              transformation.translate(0, -height);
            case 3:
              transformation.rotate(Math.toRadians(180), center, center);
              break;

            default:
              log.error("Unbekannte Orientierung: {}", orientation);
          }
        }
        catch (NumberFormatException e) {
          log.error("Unbekannte Orientierung: {}", orientation);
        }
      }

      if (!available.tryAcquire(wait, TimeUnit.SECONDS)) {
        log.warn("{} Sekunden gewartet - ABBRUCH: {}", wait, request.getRequestURI());
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        return null;
      }

      try {
        log.trace("Thread beginnt Arbeit - {} weitere erlaubt, {} warten", available.availablePermits(), available.getQueueLength());

        /**
         * Dieser Try-Block ist nötig um sicherzustellen, das die Anzahl
         * aktiver Threads in jedem Fall dekrementiert wird, wenn die
         * Verarbeitung des Bildes abgeschlossen ist, oder wegen einem
         * Fehler unterbrochen wurde.
         */
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        AffineTransformOp operation = new AffineTransformOp(transformation, AffineTransformOp.TYPE_BILINEAR);
        InputStream is = null;
        try {
          is = service.fetch(picture);
          operation.filter(ImageIO.read(is), image);
        }
        finally {
          if (is != null) {
            is.close();
          }
        }

        ImageWriter writer = writers.next();
        ImageOutputStream ios = null;
        try {
          ios = ImageIO.createImageOutputStream(response.getOutputStream());
          writer.setOutput(ios);
          ImageWriteParam iwparam = writer.getDefaultWriteParam();
          if (iwparam.canWriteCompressed()) {
            iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            String[] compressionTypes = iwparam.getCompressionTypes();
            if (compressionTypes != null) {
              iwparam.setCompressionType(compressionTypes[0]);
            }
            iwparam.setCompressionQuality(quality);
          }

          writer.write(null, new IIOImage(image, null, null), iwparam);
        }
        finally {
          if (ios != null) {
            ios.flush();
            ios.close();
          }
          writer.dispose();
        }
      }
      finally {
        log.trace("Thread beebdet Arbeit - {} weitere erlaubt, {} warten", available.availablePermits(), available.getQueueLength());
        available.release();
      }
    }
    catch (Exception e) {
      log.error(e.toString());
      if (log.isDebugEnabled()) {
        e.printStackTrace();
      }
      response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }

    return null;
  }

  @RequestMapping("/pictures/*.html")
  public ModelAndView showDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Picture picture = (Picture) request.getAttribute(PICTURE);
    GrantedAccess access = (GrantedAccess) request.getAttribute(ACCESS);

    if (GrantedAccess.NO_ACCESS.equals(access))
      throw new AccessDeniedException(access.toString());

    ModelAndView mav = new ModelAndView("TILES/details");
    mav.addObject("page", new PicturesPage(picture));
    return mav;
  }
}
