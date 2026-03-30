package JStream.entity;

import JStream.entity.FeaturedItem;
import JStream.entity.WatchStatus;

public class FeaturedItemProgress {
    private FeaturedItem item;      // The film or series/season
    private WatchStatus status;     // NOT_STARTED / IN_PROGRESS / COMPLETED
    private int lastPosition;       // Last watched position (seconds for film, last episode for series)

    public FeaturedItemProgress(FeaturedItem item, WatchStatus status, int lastPosition) {
        this.item = item;
        this.status = status;
        this.lastPosition = lastPosition;
    }

    public FeaturedItem getItem() { return item; }
    public void setItem(FeaturedItem item) { this.item = item; }

    public WatchStatus getStatus() { return status; }
    public void setStatus(WatchStatus status) { this.status = status; }

    public int getLastPosition() { return lastPosition; }
    public void setLastPosition(int lastPosition) { this.lastPosition = lastPosition; }
}