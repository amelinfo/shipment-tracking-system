package
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShipmentEvent(
    String trackingId,
    String oldStatus,
    String newStatus,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    
    String eventType
) {
    public ShipmentEvent {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (eventType == null) {
            eventType = "SHIPMENT_STATUS_CHANGED";
        }
    }
    
    public ShipmentEvent(String trackingId, String oldStatus, String newStatus) {
        this(trackingId, oldStatus, newStatus, LocalDateTime.now(), "SHIPMENT_STATUS_CHANGED");
    }
    
    public static ShipmentEvent of(String trackingId, String oldStatus, String newStatus) {
        return new ShipmentEvent(trackingId, oldStatus, newStatus);
    }
}