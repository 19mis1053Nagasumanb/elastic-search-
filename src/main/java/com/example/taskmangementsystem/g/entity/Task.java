package com.example.taskmangementsystem.g.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "optimizedes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
    @Id
    private String id = String.valueOf(System.currentTimeMillis());// Generates ID based on current time
    private String name;
    private String task;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    private String logHours;

//    private String duration;

    private String day;

    @Enumerated(EnumType.STRING)
    private TaskStatus status; // Using enum instead of string


    private Long date;
    private Long time;

    @PrePersist
    public void setDefaultTimeIfNull() {
        if (this.time == null) {
            this.time = Instant.now().toEpochMilli();
        }
    }
    @JsonProperty("date")
    public String getFormattedDate() {
        if (this.date != null) {
            return Instant.ofEpochMilli(this.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString(); // Converts to yyyy-MM-dd format
        }
        return null; // Return null if date is null
    }

    @JsonProperty("date")
    public void setFormattedDate(Long date) {
        this.date = date;
    }
    // Get time in formatted hh:mm:ss
//    @JsonProperty("time")
//    public String getFormattedTime() {
//        if (this.time != null) {
//            return Instant.ofEpochMilli(this.time)
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalTime()
//                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
//        }
//        return null;
//    }
    @JsonProperty("time")
    public String getFormattedTime() {
        if (this.time != null) {
            // Use LocalTime without offset adjustments
            return LocalTime.ofSecondOfDay(this.time / 1000) // Convert milliseconds to seconds
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        return null; // Return null if time is null
    }
//// Time getter and setter with current time default
//    @JsonProperty("time")
//    public String getFormattedTime() {
//    long effectiveTime = (this.time != null) ? this.time : System.currentTimeMillis();
//    return Instant.ofEpochMilli(effectiveTime)
//            .atZone(ZoneId.systemDefault())
//            .toLocalTime()
//            .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
//    }

    @JsonProperty("time")
    public void setFormattedTime(Long time) {
        this.time = time;
    }
}
