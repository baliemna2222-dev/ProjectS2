package JStream.service;

import JStream.dao.CommentDAO;
import JStream.entity.Comment;

import java.util.List;

public class CommentService {

    private final CommentDAO commentDAO = new CommentDAO();

    // ── Poster un commentaire ────────────────────────────────────────────────
    public boolean postComment(Comment comment) {
        return commentDAO.insertComment(comment);
    }

    // ── Supprimer ────────────────────────────────────────────────────────────
    public boolean deleteComment(int commentId) {
        return commentDAO.deleteComment(commentId);
    } 

    // ── Signaler ─────────────────────────────────────────────────────────────
    public boolean flagComment(int commentId) {
        return commentDAO.flagComment(commentId);
    }
    public List<Comment> getSignaledComments() {
        return commentDAO.getSignaledComments();
    }
    // ── Charger les commentaires d'un film ───────────────────────────────────
    public List<Comment> getCommentsForFilm(int filmId) {
        return commentDAO.getCommentsByFilm(filmId);
    }

    // ── Charger les commentaires d'une série ─────────────────────────────────
    public List<Comment> getCommentsForEpisode(int serieId) {
        return commentDAO.getCommentsByEpisode(serieId);
    }
   
    public void pardonComment(int commentId) {
        commentDAO.pardonComment(commentId);
    }
   
}