package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Ensemble;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class ScheduleEnsembleForm {
    private String name;
    private String date;
    private String time;
    private String zoomMeetingLink;
    private String timezone;

    public ScheduleEnsembleForm() {
    }

    public ScheduleEnsembleForm(String name, String zoomMeetingLink, String date, String time, String timezone) {
        this.name = name;
        this.zoomMeetingLink = zoomMeetingLink;
        this.date = date;
        this.time = time;
        this.timezone = timezone;
    }

    public static ScheduleEnsembleForm from(Ensemble ensemble) {
        ZonedDateTime zonedDateTime = convertFromUtcToTimeZone(ensemble.startDateTime(), DateTimeFormatting.PACIFIC_TIME_ZONE_ID.toString());
        return new ScheduleEnsembleForm(ensemble.name(),
                                        ensemble.meetingLink().toString(),
                                        DateTimeFormatting.extractFormattedDateFrom(zonedDateTime),
                                        DateTimeFormatting.extractFormattedTimeFrom(zonedDateTime),
                                        zonedDateTime.getZone().toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getZoomMeetingLink() {
        return zoomMeetingLink;
    }

    public void setZoomMeetingLink(String zoomMeetingLink) {
        this.zoomMeetingLink = zoomMeetingLink;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    ZonedDateTime getDateTimeInUtc() {
        LocalDateTime localDateTime = browserDateInputToLocalDateTime();
        ZonedDateTime zonedDateTimeInOriginalTimeZone = ZonedDateTime.of(localDateTime, ZoneId.of(timezone));
        return convertToUtcTimeZone(zonedDateTimeInOriginalTimeZone);
    }

    private LocalDateTime browserDateInputToLocalDateTime() {
        return LocalDateTime.parse(date + " " + time, DateTimeFormatting.YYYY_MM_DD_HH_MM_FORMATTER);
    }

    private ZonedDateTime convertToUtcTimeZone(ZonedDateTime zonedDateTimeInOriginalTimeZone) {
        return zonedDateTimeInOriginalTimeZone.withZoneSameInstant(ZoneOffset.UTC);
    }

    private static ZonedDateTime convertFromUtcToTimeZone(ZonedDateTime zonedDateTimeInUtc, String timezone) {
        return zonedDateTimeInUtc.withZoneSameInstant(ZoneId.of(timezone));
    }

}
