package JStream.service;

import JStream.dao.NotificationDAO;
import JStream.entity.NewEpisodeInfo;
import JStream.entity.Notification;

import java.util.List;

public class NotificationService {

    private final NotificationDAO dao = new NotificationDAO();

    public boolean           isFirstLogin(int userId)                                          { return dao.isFirstLogin(userId); }
    public void              markFirstLoginDone(int userId)                                    { dao.markFirstLoginDone(userId); }
    public void              addNotification(int userId, String title, String body, String type) { dao.addNotification(userId, title, body, type); }
    public List<Notification>    getNotifications(int userId)                                  { return dao.getNotifications(userId); }
    public int               getUnreadCount(int userId)                                        { return dao.getUnreadCount(userId); }
    public void              markRead(int notificationId)                                      { dao.markRead(notificationId); }
    public void              markAllRead(int userId)                                           { dao.markAllRead(userId); }
    public List<NewEpisodeInfo>  getNewEpisodesForUser(int userId)   
    { return dao.getNewEpisodesForUser(userId); }
    public void deleteNotification(int notificationId) {
        dao.deleteNotification(notificationId);
    }

    public void deleteAll(int userId) {
        dao.deleteAll(userId);
    }
}