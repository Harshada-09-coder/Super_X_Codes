package com.techtrio.services;

import java.util.ArrayList;
import java.util.List;

import com.techtrio.model.ConflictResult;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;
import com.techtrio.controller.AdminController;
import com.techtrio.model.Conflict;

import java.time.LocalDateTime;

public class ConflictDetectionService {

        private final EventService eventService = new EventService();
        private final ConflictService conflictService = new ConflictService();
        private final VendorBookingService vendorBookingService = new VendorBookingService();
       

   /* public ConflictResult checkAllConflicts(Event newEvent) {

        ConflictResult result = new ConflictResult();


        if (newEvent == null) {
            return result;
        }

        List<Event> allEvents = eventService.getAllEvents();

        if (allEvents == null) {
            return result;
        }

        for (Event existingEvent : allEvents) {

            // Same event असल्यास skip
            if (newEvent.getEventId() != null &&
                newEvent.getEventId().equals(existingEvent.getEventId())) {
                continue;
            }

            // Date check
            if (newEvent.getEventDate() == null ||
                existingEvent.getEventDate() == null) {
                continue;
            }

            if (!newEvent.getEventDate()
                    .equals(existingEvent.getEventDate())) {
                continue;
            }

            // Venue check
            boolean sameVenue = false;

            if (newEvent.getVenueType() != null &&
                existingEvent.getVenueType() != null) {

                sameVenue = newEvent.getVenueType()
                        .trim()
                        .equalsIgnoreCase(
                                existingEvent.getVenueType().trim()
                        );
            }

            if (!sameVenue) {
                continue;
            }

            // Time check
            boolean timeOverlap =
                    isTimeOverlap(
                            newEvent.getTime(),
                            existingEvent.getTime()
                    );

            if (timeOverlap) {

                result.setConflict(true);
                result.setVenueConflict(true);
                result.setTimeConflict(true);
                saveDetectedConflict(
        newEvent,
        existingEvent,
        "Venue",
        "High",
        newEvent.getVenueType(),
        "Venue overlap detected."
);

                result.getConflictingEvents()
                        .add(existingEvent);
            }
        }

        return result;
    }*/

        public ConflictResult checkAllConflicts(Event newEvent) {

    ConflictResult result = new ConflictResult();

    if (newEvent == null) {
        return result;
    }

    List<Event> allEvents = eventService.getAllEvents();

    if (allEvents == null) {
        return result;
    }

    for (Event existingEvent : allEvents) {

        if (existingEvent == null) {
            continue;
        }

        // Same event असल्यास skip
        if (newEvent.getEventId() != null
                && newEvent.getEventId()
                        .equals(existingEvent.getEventId())) {

            continue;
        }

        // ------------------------------------------------
        // 1. DATE CHECK
        // ------------------------------------------------

        if (newEvent.getEventDate() == null
                || existingEvent.getEventDate() == null) {

            continue;
        }

        if (!newEvent.getEventDate()
                .trim()
                .equalsIgnoreCase(
                        existingEvent.getEventDate().trim())) {

            continue;
        }


        // ------------------------------------------------
        // 2. TIME CHECK
        // ------------------------------------------------

        boolean timeOverlap =
                isTimeOverlap(
                        newEvent.getTime(),
                        existingEvent.getTime()
                );

        if (!timeOverlap) {
            continue;
        }


        // ------------------------------------------------
        // 3. VENUE CONFLICT
        // ------------------------------------------------

        boolean sameVenue =
                sameVenue(
                        newEvent,
                        existingEvent
                );

        if (sameVenue) {

            result.setConflict(true);
            result.setVenueConflict(true);
            result.setTimeConflict(true);

            saveDetectedConflict(
                    newEvent,
                    existingEvent,
                    "Venue",
                    "High",
                    newEvent.getVenueType(),
                    "Venue and time overlap detected."
            );

            addConflictingEvent(
                    result,
                    existingEvent
            );
        }


        // ------------------------------------------------
        // 4. ORGANIZER CONFLICT
        // ------------------------------------------------

        boolean sameOrganizer =
                sameOrganizer(
                        newEvent,
                        existingEvent
                );

        if (sameOrganizer) {

            result.setConflict(true);
            result.setOrganizerConflict(true);
            result.setTimeConflict(true);

            saveDetectedConflict(
                    newEvent,
                    existingEvent,
                    "Organizer",
                    "Critical",
                    newEvent.getOrganizerName(),
                    "Same organizer is assigned to overlapping events."
            );

            addConflictingEvent(
                    result,
                    existingEvent
            );
        }
    }


    // ------------------------------------------------
    // 5. VENDOR CONFLICT
    // ------------------------------------------------

    checkVendorConflicts(
            newEvent,
            result
    );


    return result;
}


private boolean sameVenue(
        Event event1,
        Event event2) {

    if (event1 == null || event2 == null) {
        return false;
    }

    String city1 = event1.getCity();
    String city2 = event2.getCity();

    String locality1 = event1.getLocality();
    String locality2 = event2.getLocality();

    String venueType1 = event1.getVenueType();
    String venueType2 = event2.getVenueType();

    if (city1 == null || city2 == null
            || locality1 == null || locality2 == null
            || venueType1 == null || venueType2 == null) {

        return false;
    }

    if (city1.isBlank() || city2.isBlank()
            || locality1.isBlank() || locality2.isBlank()
            || venueType1.isBlank() || venueType2.isBlank()) {

        return false;
    }

    return city1.trim().equalsIgnoreCase(city2.trim())
            && locality1.trim().equalsIgnoreCase(locality2.trim())
            && venueType1.trim().equalsIgnoreCase(venueType2.trim());
}




    private boolean sameOrganizer(Event event1, Event event2) {

    if (event1 == null || event2 == null) {
        return false;
    }

    String organizer1 = event1.getOrganizerId();
    String organizer2 = event2.getOrganizerId();

    if (organizer1 == null || organizer2 == null) {
        return false;
    }

    if (organizer1.isBlank() || organizer2.isBlank()) {
        return false;
    }

    return organizer1.trim()
            .equalsIgnoreCase(organizer2.trim());
}



    private boolean isTimeOverlap(
            String newTime,
            String existingTime) {

        if (newTime == null ||
            existingTime == null ||
            newTime.isBlank() ||
            existingTime.isBlank()) {

            return false;
        }

        /*
         * Expected format:
         *
         * 10:00 AM - 01:00 PM
         * 11:00 AM - 02:00 PM
         *
         */

        String[] newParts = newTime.split("-");

        String[] existingParts = existingTime.split("-");

        if (newParts.length != 2 ||
            existingParts.length != 2) {

            return false;
        }

        int newStart =
                convertToMinutes(newParts[0].trim());

        int newEnd =
                convertToMinutes(newParts[1].trim());

        int existingStart =
                convertToMinutes(existingParts[0].trim());

        int existingEnd =
                convertToMinutes(existingParts[1].trim());

                if (newStart < 0
        || newEnd < 0
        || existingStart < 0
        || existingEnd < 0) {

    return false;
}

        return newStart < existingEnd &&
               newEnd > existingStart;
    }

    private int convertToMinutes(String time) {

        try {

            String value = time
                    .trim()
                    .toUpperCase();

            boolean pm = value.contains("PM");
            boolean am = value.contains("AM");

            value = value
                    .replace("AM", "")
                    .replace("PM", "")
                    .trim();

            String[] parts = value.split(":");

            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            if (pm && hour != 12) {
                hour += 12;
            }

            if (am && hour == 12) {
                hour = 0;
            }

            return hour * 60 + minute;

        } catch (Exception e) {

            return -1;
        }
    }

    private void saveDetectedConflict(
        Event newEvent,
        Event existingEvent,
        String type,
        String severity,
        String resource,
        String description) {

    try {

        if (newEvent == null
                || existingEvent == null) {

            return;
        }


        String eventId =
                newEvent.getEventId();

        String conflictingEventId =
                existingEvent.getEventId();


        if (eventId == null
                || conflictingEventId == null) {

            return;
        }


        /*
         * Same pair + same type पुन्हा save होऊ नये
         */
        String conflictId =
                eventId
                + "_"
                + conflictingEventId
                + "_"
                + type;


        Conflict conflict =
                new Conflict();


        conflict.setConflictId(
                conflictId
        );

        conflict.setEventId(
                eventId
        );

        conflict.setConflictingEventId(
                conflictingEventId
        );

        conflict.setType(
                type
        );

        conflict.setSeverity(
                severity
        );

        conflict.setStatus(
                "Open"
        );

        conflict.setDate(
                newEvent.getEventDate()
        );

        conflict.setResource(
                resource
        );

        conflict.setDescription(
                description
        );

        conflict.setCreatedAt(
                LocalDateTime.now().toString()
        );

        conflict.setResolvedAt(
                null
        );

        conflict.setResolutionNotes(
                ""
        );


        conflictService.saveConflict(
                conflict
        );


    } catch (Exception e) {

        System.err.println(
                "Unable to save detected conflict: "
                + e.getMessage()
        );
    }
}

private void addConflictingEvent(
        ConflictResult result,
        Event existingEvent) {

    if (result == null || existingEvent == null) {
        return;
    }

    if (!result.getConflictingEvents()
            .contains(existingEvent)) {

        result.getConflictingEvents()
                .add(existingEvent);
    }
}

private void checkVendorConflicts(
        Event newEvent,
        ConflictResult result) {

    if (newEvent == null || result == null) {
        return;
    }

    if (newEvent.getEventId() == null
            || newEvent.getEventId().isBlank()) {

        return;
    }

    List<VendorBooking> newBookings =
            vendorBookingService.getBookingsByEventId(
                    newEvent.getEventId()
            );

    if (newBookings == null || newBookings.isEmpty()) {
        return;
    }

    for (VendorBooking newBooking : newBookings) {

        if (newBooking == null) {
            continue;
        }

        String vendorId =
                newBooking.getVendorId();

        if (vendorId == null
                || vendorId.isBlank()) {

            continue;
        }

        List<VendorBooking> vendorBookings =
                vendorBookingService.getBookingsByVendorId(
                        vendorId
                );

        if (vendorBookings == null
                || vendorBookings.isEmpty()) {

            continue;
        }

        for (VendorBooking existingBooking :
                vendorBookings) {

            if (existingBooking == null) {
                continue;
            }

            // Same booking skip
            if (newBooking.getBookingId() != null
                    && newBooking.getBookingId()
                            .equals(
                                    existingBooking.getBookingId()
                            )) {

                continue;
            }

            // Same event skip
            if (newEvent.getEventId()
                    .equals(existingBooking.getEventId())) {

                continue;
            }

            // Same date
            if (existingBooking.getEventDate() == null
                    || newEvent.getEventDate() == null) {

                continue;
            }

            if (!newEvent.getEventDate()
                    .trim()
                    .equalsIgnoreCase(
                            existingBooking
                                    .getEventDate()
                                    .trim()
                    )) {

                continue;
            }


            result.setConflict(true);
            result.setVendorConflict(true);

            saveVendorConflict(
                    newEvent,
                    existingBooking
            );
        }
    }
}

/////

private void saveVendorConflict(
        Event newEvent,
        VendorBooking existingBooking) {

    if (newEvent == null
            || existingBooking == null) {

        return;
    }

    String eventId =
            newEvent.getEventId();

    String conflictingEventId =
            existingBooking.getEventId();

    if (eventId == null
            || conflictingEventId == null
            || conflictingEventId.isBlank()) {

        return;
    }

    String conflictId =
            eventId
            + "_"
            + conflictingEventId
            + "_Vendor";

    Conflict conflict =
            new Conflict();

    conflict.setConflictId(
            conflictId
    );

    conflict.setEventId(
            eventId
    );

    conflict.setConflictingEventId(
            conflictingEventId
    );

    conflict.setType(
            "Vendor"
    );

    conflict.setSeverity(
            "High"
    );

    conflict.setStatus(
            "Open"
    );

    conflict.setDate(
            newEvent.getEventDate()
    );

    conflict.setResource(
            existingBooking.getVendorName()
    );

    conflict.setDescription(
            "Same vendor is booked for overlapping events."
    );

    conflict.setCreatedAt(
            LocalDateTime.now().toString()
    );

    conflict.setResolvedAt(
            null
    );

    conflict.setResolutionNotes(
            ""
    );

    conflictService.saveConflict(
            conflict
    );
}


}