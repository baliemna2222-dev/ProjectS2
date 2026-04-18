package JStream.controller;

import JStream.entity.Actor;
import JStream.service.ActorService;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class ActorPanelBuilder {

    public enum Mode { FILM, SERIE }

    private static final String BG_ROW     = "rgba(15,22,42,0.75)";
    private static final String BG_HOVER   = "rgba(37,99,235,0.10)";
    private static final String ACCENT     = "#2563eb";
    private static final String ACCENT2    = "#38bdf8";
    private static final String TEXT       = "#e2e8f0";
    private static final String MUTED      = "#64748b";
    private static final String DANGER     = "#dc2626";
    private static final String BORDER_DIM = "rgba(37,99,235,0.18)";

    private final ActorService actorService;
    private final IntSupplier  entityIdSupplier;
    private       Mode         mode = Mode.FILM;

    private VBox      actorListBox;
    private TextField searchField;
    private Node      anchorNode;   

    private final List<Actor> pendingActors = new ArrayList<>();

    public ActorPanelBuilder(ActorService actorService, IntSupplier entityIdSupplier) {
        this.actorService     = actorService;
        this.entityIdSupplier = entityIdSupplier;
    }

    public void setMode(Mode mode) { this.mode = mode; }

    public VBox buildPanel() {
        Label sectionTitle = new Label("CAST  &  ACTORS");
        sectionTitle.setStyle(
            "-fx-text-fill:" + MUTED + ";-fx-font-size:10px;-fx-font-weight:bold;-fx-letter-spacing:2;"
        );

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:" + BORDER_DIM + ";");

        searchField = new TextField();
        searchField.setPromptText("Search actor by name…");
        searchField.setStyle(  "-fx-background-color:rgba(255,255,255,0.05);-fx-text-fill:" + TEXT +
                              ";-fx-prompt-text-fill:" + MUTED + ";-fx-border-color:" + BORDER_DIM +
                               ";-fx-border-radius:8;-fx-background-radius:8;-fx-padding:9 12;"
        );
          
        HBox.setHgrow(searchField, Priority.ALWAYS);
        Button newActorBtn = pill("＋  New Actor", ACCENT, "white");
        newActorBtn.setOnAction(e -> showActorFormDialog(null, null));

        HBox topBar = new HBox(10, searchField, newActorBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox suggestBox = new VBox(4);
        suggestBox.setStyle(
            "-fx-background-color:#0d1424;-fx-border-color:" + BORDER_DIM +
            ";-fx-border-radius:8;-fx-background-radius:8;-fx-padding:6;"
        );
        suggestBox.setVisible(false);
        suggestBox.setManaged(false);
        suggestBox.setMaxHeight(180);

        searchField.textProperty().addListener((obs, o, query) -> {
            if (query == null || query.isBlank()) {
                suggestBox.setVisible(false); suggestBox.setManaged(false); return;
            }
            List<Actor> results = actorService.searchActorsByName(query.trim());
            suggestBox.getChildren().clear();
            if (results.isEmpty()) {
                suggestBox.setVisible(false); suggestBox.setManaged(false);
            } else {
                results.forEach(a -> suggestBox.getChildren().add(buildSuggestionRow(a)));
                suggestBox.setVisible(true); suggestBox.setManaged(true);
            }
        });

        actorListBox = new VBox(6);
        actorListBox.setStyle("-fx-padding:2 0 0 0;");

        VBox panel = new VBox(10, sectionTitle, sep, topBar, suggestBox, actorListBox);
        panel.setStyle("-fx-padding:4 0 0 0;");
        anchorNode = sectionTitle;

        refreshActors();
        return panel;
    }

    private Runnable showOverlay(Node content) {
        Scene scene = anchorNode != null ? anchorNode.getScene() : null;
        if (scene == null) {
            return () -> {};
        }

        StackPane root;
        if (scene.getRoot() instanceof StackPane sp) {
            root = sp;
        } else {
            javafx.scene.Parent oldRoot = scene.getRoot();
            root = new StackPane(oldRoot);
            scene.setRoot(root);
        }
        StackPane backdrop = new StackPane(content);
        backdrop.setStyle("-fx-background-color:rgba(0,0,0,0.65);");
        backdrop.setAlignment(Pos.CENTER);
        StackPane.setAlignment(backdrop, Pos.CENTER);
        content.setScaleX(0.88); content.setScaleY(0.88);
        content.setOpacity(0);
        root.getChildren().add(backdrop);

        FadeTransition  fi = new FadeTransition(Duration.millis(180), content);
        ScaleTransition si = new ScaleTransition(Duration.millis(180), content);
        fi.setToValue(1); si.setToX(1); si.setToY(1);
        new ParallelTransition(fi, si).play();
        StackPane finalRoot = root;
        return () -> {
            FadeTransition fo = new FadeTransition(Duration.millis(140), content);
            ScaleTransition so = new ScaleTransition(Duration.millis(140), content);
            fo.setToValue(0); so.setToX(0.88); so.setToY(0.88);
            fo.setOnFinished(ev -> finalRoot.getChildren().remove(backdrop));
            new ParallelTransition(fo, so).play();
        };
    }

    public void refreshActors() {
        if (actorListBox == null) return;
        actorListBox.getChildren().clear();

        int id = entityIdSupplier.getAsInt();
        if (id <= 0) {
            Label hint = new Label("Save the " + (mode == Mode.FILM ? "film" : "series") +
                                   " first, then add actors.");
            hint.setStyle("-fx-text-fill:" + MUTED + ";-fx-font-size:11px;-fx-padding:8 0;");
            actorListBox.getChildren().add(hint);
            return;
        }

        List<Actor> actors = mode == Mode.FILM
            ? actorService.getActorsByFilm(id)
            : actorService.getActorsBySerie(id);

        if (actors.isEmpty()) {
            Label empty = new Label("No actors linked yet.");
            empty.setStyle("-fx-text-fill:" + MUTED + ";-fx-font-size:11px;-fx-padding:8 0;");
            actorListBox.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < actors.size(); i++) {
            HBox row = buildActorRow(actors.get(i));
            row.setOpacity(0);
            actorListBox.getChildren().add(row);
            int delay = i * 40;
            PauseTransition pt = new PauseTransition(Duration.millis(delay));
            FadeTransition ft  = new FadeTransition(Duration.millis(250), row);
            TranslateTransition tt = new TranslateTransition(Duration.millis(250), row);
            ft.setToValue(1); tt.setFromX(-8); tt.setToX(0);
            pt.setOnFinished(e -> new ParallelTransition(ft, tt).play());
            pt.play();
        }
    }
    private HBox buildActorRow(Actor actor) {
        ImageView avatar = new ImageView();
        avatar.setFitWidth(38); avatar.setFitHeight(38);
        avatar.setPreserveRatio(false);
        loadImage(avatar, actor.getPhotoUrl(), null);
        avatar.setClip(new Circle(19, 19, 19));

        StackPane avatarPane = new StackPane(avatar);
        avatarPane.setPrefSize(38, 38); avatarPane.setMinSize(38, 38); avatarPane.setMaxSize(38, 38);
        avatarPane.setStyle("-fx-background-color:rgba(37,99,235,0.20);-fx-background-radius:19;");

        Label nameLbl = new Label(actor.getName());
        nameLbl.setStyle("-fx-text-fill:" + TEXT + ";-fx-font-size:13px;-fx-font-weight:bold;");
        nameLbl.setMaxWidth(160);          
        nameLbl.setTextOverrun(OverrunStyle.ELLIPSIS);

        String roleText = (actor.getRoleName() != null && !actor.getRoleName().isBlank())
            ? actor.getRoleName() : "—";
        Label roleLbl = new Label(roleText);
        roleLbl.setStyle("-fx-text-fill:" + MUTED + ";-fx-font-size:11px;");
        roleLbl.setMaxWidth(160);
        roleLbl.setTextOverrun(OverrunStyle.ELLIPSIS);
        VBox nameCol = new VBox(2, nameLbl, roleLbl);
        nameCol.setAlignment(Pos.CENTER_LEFT);
        
        HBox.setHgrow(nameCol, Priority.ALWAYS); 
        Button editBtn   = iconBtn("✎", ACCENT2);
        Button unlinkBtn = iconBtn("✕", DANGER);
        
        editBtn.setOnAction(e   -> showActorFormDialog(actor, actor.getRoleName()));
        unlinkBtn.setOnAction(e -> confirmUnlink(actor));
        HBox actions = new HBox(6, editBtn, unlinkBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setMinWidth(Region.USE_PREF_SIZE);

        HBox row = new HBox(12, avatarPane, nameCol, actions);
        
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));

        String base  = "-fx-background-color:" + BG_ROW + ";-fx-background-radius:10;" +
                       "-fx-border-color:" + BORDER_DIM + ";-fx-border-radius:10;-fx-border-width:1;";
        String hover = "-fx-background-color:" + BG_HOVER + ";-fx-background-radius:10;" +
                       "-fx-border-color:rgba(37,99,235,0.35);-fx-border-radius:10;-fx-border-width:1;";
        row.setStyle(base);
        row.setOnMouseEntered(ev -> row.setStyle(hover));
        row.setOnMouseExited(ev  -> row.setStyle(base));
        return row;
    }
//rows
    private HBox buildSuggestionRow(Actor actor) {
        ImageView thumb = new ImageView();
        thumb.setFitWidth(28); thumb.setFitHeight(28);
        loadImage(thumb, actor.getPhotoUrl(), null);
        thumb.setClip(new Circle(14, 14, 14));

        Label name = new Label(actor.getName());
        name.setStyle("-fx-text-fill:" + TEXT + ";-fx-font-size:12px;");
        HBox.setHgrow(name, Priority.ALWAYS);

        Button linkBtn = pill("Link", ACCENT, "white");
        linkBtn.setStyle(linkBtn.getStyle() + "-fx-font-size:10px;-fx-padding:3 10;");
        linkBtn.setOnAction(e -> { showRoleInputDialog(actor, null);
                                  if (searchField != null) searchField.clear(); });
    
        HBox row = new HBox(10, thumb, name, linkBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 8, 6, 8));
        row.setStyle("-fx-background-color:transparent;-fx-background-radius:6;-fx-cursor:hand;");
        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color:rgba(37,99,235,0.10);-fx-background-radius:6;-fx-cursor:hand;"));
        row.setOnMouseExited (e -> row.setStyle("-fx-background-color:transparent;-fx-background-radius:6;-fx-cursor:hand;"));
        return row;
    }
//actors
    private void showActorFormDialog(Actor existing, String role) {
        boolean isNew = (existing == null);

        VBox card = new VBox(20);
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(420);
        card.setMaxHeight(500);
        card.setPadding(new Insets(30, 32, 30, 32));
        card.setStyle(  "-fx-background-color:#0d1629;-fx-background-radius:18;" +
            "-fx-border-color:rgba(37,99,235,0.28);-fx-border-radius:18;-fx-border-width:1;"
        );
        Label cardTitle = new Label(isNew ? "＋  Add New Actor" : "✎  Edit Actor");
        cardTitle.setStyle("-fx-text-fill:white;-fx-font-size:17px;-fx-font-weight:bold;");
        Separator s = new Separator();
        s.setStyle("-fx-background-color:" + BORDER_DIM + ";");
        ImageView previewImg = new ImageView();
        
        previewImg.setFitWidth(64); previewImg.setFitHeight(64);
        previewImg.setPreserveRatio(false);
        previewImg.setClip(new Circle(32, 32, 32));
        
        if (existing != null) loadImage(previewImg, existing.getPhotoUrl(), null);
        StackPane avatarPane = new StackPane(previewImg);
        avatarPane.setPrefSize(64, 64); avatarPane.setMinSize(64, 64); avatarPane.setMaxSize(64, 64);
        avatarPane.setStyle("-fx-background-color:rgba(37,99,235,0.15);-fx-background-radius:32;");
        final String[] photoPath = { existing != null ? existing.getPhotoUrl() : null };

        Button changePhotoBtn = pill("📷  Photo", "rgba(37,99,235,0.20)", ACCENT2);
        changePhotoBtn.setStyle(changePhotoBtn.getStyle() + "-fx-font-size:11px;-fx-padding:5 12;");
        changePhotoBtn.setOnAction(ev -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose Actor Photo");
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png","*.jpg","*.jpeg","*.webp")
            );
            javafx.stage.Window win = anchorNode != null && anchorNode.getScene() != null
                ? anchorNode.getScene().getWindow() : null;
            File f = fc.showOpenDialog(win);
            if (f != null) {
                photoPath[0] = f.toURI().toString();
                loadImage(previewImg, photoPath[0], null);
            }
        });

        HBox avatarRow = new HBox(14, avatarPane, changePhotoBtn);
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl   = label("Actor Name *");
        TextField nameField = field(existing != null ? existing.getName() : "", "Full name");
        Label roleLbl   = label("Role / Character");
        TextField roleField = field(role != null ? role : "", "e.g. Tony Stark");
        Label errLbl = new Label("");
        
        errLbl.setStyle("-fx-text-fill:#f87171;-fx-font-size:11px;");
        errLbl.setVisible(false); errLbl.setManaged(false);

        Button saveBtn   = pill(isNew ? "Add Actor" : "Save Changes", ACCENT, "white");
        Button cancelBtn = pill("Cancel", "rgba(255,255,255,0.07)", MUTED);
        HBox btnRow = new HBox(12, cancelBtn, saveBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(
            cardTitle, s, avatarRow,
            new VBox(6, nameLbl, nameField),
            new VBox(6, roleLbl, roleField),
            errLbl, btnRow
        );
        Runnable[] close = { null };
        close[0] = showOverlay(card);

        cancelBtn.setOnAction(ev -> close[0].run());

        saveBtn.setOnAction(ev -> {
            String nameVal = nameField.getText().trim();
            String roleVal = roleField.getText().trim();
            if (nameVal.isEmpty()) {
                errLbl.setText("⚠  Actor name is required.");
                errLbl.setVisible(true); errLbl.setManaged(true);
                return;
            }
            int entityId = entityIdSupplier.getAsInt();
            if (isNew) {
                Actor created = actorService.createActor(nameVal, photoPath[0]);
                if (entityId > 0) {
                    if (mode == Mode.FILM) actorService.linkToFilm(entityId, created.getActorId(), roleVal);
                    else                   actorService.linkToSerie(entityId, created.getActorId(), roleVal);
                } else {
                    created.setRoleName(roleVal);
                    pendingActors.add(created);
                }
            } else {
                existing.setName(nameVal);
                if (photoPath[0] != null) existing.setPhotoUrl(photoPath[0]);
                actorService.updateActor(existing);
                if (entityId > 0) {
                    if (mode == Mode.FILM) actorService.updateFilmRole(entityId, existing.getActorId(), roleVal);
                    else                   actorService.updateSerieRole(entityId, existing.getActorId(), roleVal);
                }
            }
            refreshActors();
            close[0].run();
        });
    }

    private void showRoleInputDialog(Actor actor, String existingRole) {
        VBox card = new VBox(18);
        card.setMaxWidth(360);
        card.setMaxHeight(500);
        card.setPadding(new Insets(26, 28, 26, 28));
        card.setStyle( "-fx-background-color:#0d1629;-fx-background-radius:16;" +
            "-fx-border-color:rgba(37,99,235,0.25);-fx-border-radius:16;-fx-border-width:1;");
          
        Label title   = new Label("Link  " + actor.getName());
        title.setStyle("-fx-text-fill:white;-fx-font-size:15px;-fx-font-weight:bold;");
        Label roleLbl = label("Role / Character");
        TextField roleField = field(existingRole != null ? existingRole : "", "e.g. Bruce Wayne");
        Button linkBtn   = pill("Link Actor", ACCENT, "white");
        Button cancelBtn = pill("Cancel", "rgba(255,255,255,0.07)", MUTED);
        HBox btns = new HBox(10, cancelBtn, linkBtn);
        btns.setAlignment(Pos.CENTER_RIGHT);
        card.getChildren().addAll(title, new VBox(6, roleLbl, roleField), btns);
        Runnable[] close = { null };
        close[0] = showOverlay(card);

        cancelBtn.setOnAction(e -> close[0].run());
        linkBtn.setOnAction(e -> {
            int entityId = entityIdSupplier.getAsInt();
            String roleVal = roleField.getText().trim();
            if (entityId > 0) {
                if (mode == Mode.FILM) actorService.linkToFilm(entityId, actor.getActorId(), roleVal);
                else                   actorService.linkToSerie(entityId, actor.getActorId(), roleVal);
            } else {
                actor.setRoleName(roleVal);
                pendingActors.add(actor);
            }
            refreshActors();
            close[0].run();
        });
    }

    private void confirmUnlink(Actor actor) {
        VBox card = new VBox(20);
        card.setMaxWidth(360);
        card.setMaxHeight(500);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28, 30, 28, 30));
        card.setStyle("-fx-background-color:#0f172a;-fx-background-radius:18;" +
            "-fx-border-color:rgba(220,38,38,0.25);-fx-border-radius:18;-fx-border-width:1;");
        Label icon = new Label("✕");
        icon.setAlignment(Pos.CENTER);
        icon.setStyle( "-fx-text-fill:#f87171;-fx-font-size:20px;-fx-font-weight:bold;" +
            "-fx-background-color:rgba(220,38,38,0.12);-fx-background-radius:28;" +"-fx-min-width:56;-fx-min-height:56;");
        Label title = new Label("Remove Actor?");
        title.setStyle("-fx-text-fill:white;-fx-font-size:17px;-fx-font-weight:bold;");
        Label msg = new Label("\"" + actor.getName() + "\" will be unlinked\nfrom this " +
                              (mode == Mode.FILM ? "film" : "series") + ".");
        msg.setStyle("-fx-text-fill:#64748b;-fx-font-size:13px;-fx-text-alignment:center;");
        msg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        msg.setWrapText(true);

        Button cancelBtn = pill("Cancel", "rgba(255,255,255,0.07)", MUTED);
        Button removeBtn = pill("Remove", DANGER, "white");
        HBox btns = new HBox(12, cancelBtn, removeBtn);
        btns.setAlignment(Pos.CENTER);
        card.getChildren().addAll(icon, title, msg, btns);

        Runnable[] close = { null };
        close[0] = showOverlay(card);

        cancelBtn.setOnAction(e -> close[0].run());
        removeBtn.setOnAction(e -> {
            int entityId = entityIdSupplier.getAsInt();
            if (entityId > 0) {
                if (mode == Mode.FILM) actorService.unlinkFromFilm(entityId, actor.getActorId());
                else                   actorService.unlinkFromSerie(entityId, actor.getActorId());
            } else {
                pendingActors.removeIf(a -> a.getActorId() == actor.getActorId());
            }
            refreshActors();
            close[0].run();
        });
    }

    public void flushPendingActors() {
        int id = entityIdSupplier.getAsInt();
        if (id <= 0) return;
        for (Actor a : pendingActors) {
            if (mode == Mode.FILM) actorService.linkToFilm(id, a.getActorId(), a.getRoleName());
            else                   actorService.linkToSerie(id, a.getActorId(), a.getRoleName());
        }
        pendingActors.clear();
        refreshActors();
    }

    public void clearPending() { pendingActors.clear(); }

    private Button pill(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + bg + ";-fx-text-fill:" + fg + ";" +
            "-fx-font-size:12px;-fx-font-weight:bold;" +
            "-fx-padding:8 18;-fx-background-radius:22;-fx-cursor:hand;-fx-border-width:0;"
        );
        return b;
    }

    private Button iconBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:rgba(37,99,235,0.10);-fx-text-fill:" + color + ";" +
            "-fx-font-size:12px;-fx-font-weight:bold;" +
            "-fx-padding:4 10;-fx-background-radius:8;-fx-cursor:hand;-fx-border-width:0;"
        );
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle().replace("rgba(37,99,235,0.10)", "rgba(37,99,235,0.22)")));
        b.setOnMouseExited(e  -> b.setStyle(b.getStyle().replace("rgba(37,99,235,0.22)", "rgba(37,99,235,0.10)")));
        return b;
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:" + MUTED + ";-fx-font-size:11px;");
        return l;
    }

    private TextField field(String value, String prompt) {
        TextField tf = new TextField(value);
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color:rgba(255,255,255,0.05);-fx-text-fill:" + TEXT +
            ";-fx-prompt-text-fill:" + MUTED + ";-fx-border-color:rgba(37,99,235,0.20);" +
            "-fx-border-radius:8;-fx-background-radius:8;-fx-padding:9 12;"
        );
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private void loadImage(ImageView iv, String path, String fallback) {
        try {
            if (path == null || path.isBlank()) {
                if (fallback != null) {
                    iv.setImage(new Image(getClass().getResourceAsStream(fallback)));
                }
                return;
            }

            Image img;
            if (path.startsWith("http://") || path.startsWith("https://")) {
                img = new Image(path, true);
            }
            else if (path.startsWith("file:/")) {
                img = new Image(path, true);
            }
            else if (path.matches("^[a-zA-Z]:\\\\.*")) {
                File f = new File(path);
                img = new Image(f.toURI().toString(), true);
            }
            else {
                var stream = getClass().getResourceAsStream(path);
                if (stream != null) {
                    img = new Image(stream);
                } else {
                    return;
                }
            }

            iv.setImage(img);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}