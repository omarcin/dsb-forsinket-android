package com.oczeretko.dsbforsinket.data;

import org.w3c.dom.*;
import org.xml.sax.*;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

public class DeparturesParser {

    public ArrayList<DepartureInfo> parseDepartures(InputStream responseStream) throws ParseException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(responseStream);
        NodeList childNodes = document.getDocumentElement().getElementsByTagName("entry");

        ArrayList<DepartureInfo> list = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            DepartureInfo departure = processNode(childNodes.item(i));
            if (!departure.getDepartureTime().isEmpty()) {
                list.add(departure);
            }
        }

        return list;
    }

    private DepartureInfo processNode(Node item) throws ParseException {
        String trainDestination = getDestination(item);
        String departureTime = getDepartureTime(item);
        DepartureInfo departureInfo = new DepartureInfo(trainDestination, departureTime);

        String isCancelled = getProperty(item, "d:Cancelled");
        if (!isCancelled.isEmpty()) {
            departureInfo.setCancelled(Boolean.parseBoolean(isCancelled));
        }

        String delay = getProperty(item, "d:DepartureDelay");
        if (!delay.isEmpty() && !delay.equals("0")) {
            Integer delayInMin = Integer.parseInt(delay) / 60;
            departureInfo.setDelay(delayInMin.toString());
        }

        return departureInfo;
    }

    private String getDepartureTime(Node item) throws ParseException {
        String departureTime = "";
        String scheduledDeparture = getProperty(item, "d:ScheduledDeparture");
        String minutesToDeparture = getProperty(item, "d:MinutesToDeparture");
        if (!scheduledDeparture.isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = format.parse(scheduledDeparture);
            departureTime = new SimpleDateFormat("HH:mm").format(date);
        } else if (!minutesToDeparture.isEmpty()) {
            minutesToDeparture = minutesToDeparture.replace("½", "0.5");
            double minToDeparture = Double.parseDouble(minutesToDeparture);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("Europe/Copenhagen"));
            calendar.add(Calendar.SECOND, (int)(minToDeparture * 60));
            departureTime = new SimpleDateFormat("HH:mm").format(calendar.getTime());
        }
        return departureTime;
    }

    private String getDestination(Node item) {
        String trainDestination = getProperty(item, "d:DestinationName");
        String line = getProperty(item, "d:Line");
        if (line != null && !line.isEmpty()) {
            return line + " (" + trainDestination + ")";
        }
        return trainDestination;
    }

    private String getProperty(Node item, String propertyName) {
        return ((Element)item).getElementsByTagName(propertyName).item(0).getTextContent();
    }
}
