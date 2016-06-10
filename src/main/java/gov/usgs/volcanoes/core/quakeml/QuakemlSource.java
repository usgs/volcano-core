package gov.usgs.volcanoes.core.quakeml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class QuakemlSource {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuakemlSource.class);

  private final long refreshIntervalMs;
  private final List<QuakemlObserver> observers;
  private final URL url;

  private EventSet eventSet;
  private boolean doUpdate;
  private ScheduledExecutorService scheduler;

  /**
   * Constructor.
   * 
   * @param url QuakeML source
   * @param refreshIntervalMs refresh interval in ms
   */
  public QuakemlSource(URL url, long refreshIntervalMs) {
    this.url = url;
    this.refreshIntervalMs = refreshIntervalMs;
    observers = new ArrayList<QuakemlObserver>();
    eventSet = new EventSet();
    updateQuakeml();
    if (refreshIntervalMs < Long.MAX_VALUE) {
      doUpdate = true;
      startUpdateThread();
    } else {
      doUpdate = false;
    }

  }

  public QuakemlSource(URL url) {
    this(url, Long.MAX_VALUE);
  }

  private void startUpdateThread() {
    final Runnable updater = new Runnable() {
      public void run() {
        try {
          updateQuakeml();
        } catch (Throwable throwable) {
          LOGGER.warn("Caught {} while updating QuakeML\n", throwable);
        }
      }
    };

    scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.scheduleAtFixedRate(updater, refreshIntervalMs, refreshIntervalMs,
        TimeUnit.MILLISECONDS);
  }

  private void updateQuakeml() {
    try {
      eventSet = EventSet.parseQuakeml(url.openStream());
      notifyObservers();
    } catch (SAXException ex) {
      LOGGER.warn("Unable to parse QuakeML. {}", ex);
    } catch (IOException ex) {
      LOGGER.warn("Unable to retrieve QuakeML. {}", ex);
    } catch (ParserConfigurationException ex) {
      LOGGER.warn("Internal QuakeML error. {}", ex);
    }
  }

  private void notifyObservers() {
    for (QuakemlObserver observer : observers) {
      observer.update(this);
    }
  }

  /**
   * Stop update thread.
   */
  public void stop() {
    doUpdate = false;
    if (scheduler != null) {
      scheduler.shutdownNow();
    }
  }

  public EventSet getEventSet() {
    return eventSet;
  }

  public void addObserver(QuakemlObserver observer) {
    observers.add(observer);
  }

  private static QuakemlObserver quakemlDumper() {
    return new QuakemlObserver() {
      @Override
      public void update(QuakemlSource source) {
        EventSet eventSet = source.getEventSet();
        System.out.println("Got " + eventSet.size() + " events.");

        for (Event event : eventSet.values()) {
          System.out.println("----------------------------------------------\n");
          System.out.println("Event: \n" + event.toString().replaceAll("(?m)^", "\t"));
        }
      }
    };
  }

  /**
   * A simple facade.
   * 
   * @param args none
   * @throws Exception when things go wrong
   */
  public static void main(String[] args) throws Exception {
    URL url;
    if (args.length > 0) {
      url = new URL(args[0]);
    } else {
      url = new URL("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.quakeml");
    }

    System.out.println("URL: " + url);
    QuakemlSource quakemlSource = new QuakemlSource(url, 5000);
    quakemlSource.addObserver(quakemlDumper());

    final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    boolean run = true;
    while (run) {
      String cmd = in.readLine();
      if (cmd.startsWith("q")) {
        System.out.println("Exiting...");
        run = false;
        quakemlSource.stop();
      }
    }
  }
}
