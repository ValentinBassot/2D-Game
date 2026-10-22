package com.reboot.view;

import com.reboot.model.fighter.Robot;
import com.reboot.model.game.Game;
import com.reboot.model.settings.KeyBindings;
import com.reboot.model.team.Team;
import java.util.ArrayList;
import java.util.List;

/**
 * Poste de la Home. On y compose son équipe avec les robots rangés et on dépense des pièces pour réparer
 * les robots K.O.
 */
public class HomeView extends MenuView {

    private final Game game;
    private final Runnable onClose;
    private Robot leaving;
    private String feedback = "";

    /**
     * Crée le poste de la Home.
     *
     * @param keys les touches du joueur
     * @param game la partie dont l'équipe et la Home sont gérées
     * @param onClose ferme le poste
     */
    public HomeView(KeyBindings keys, Game game, Runnable onClose) {
        super(keys);
        this.game = game;
        this.onClose = onClose;
    }

    @Override
    protected String title() {
        return "HOME";
    }

    @Override
    protected String subtitle() {
        if (!feedback.isEmpty()) {
            return feedback;
        }
        return "Pièces : " + game.getParts().getParts() + "  ·  Équipe : " + game.getTeam().getRobots().size()
                + " / " + Team.MAX_SIZE + "  ·  Rangés : " + game.getHome().getStored().size();
    }

    @Override
    protected List<MenuItem> items() {
        List<MenuItem> items = new ArrayList<>();
        for (Robot robot : game.getTeam().getRobots()) {
            items.add(new MenuItem(label("Équipe", robot), () -> chooseTeamRobot(robot), delta -> repair(robot, delta)));
        }
        for (Robot robot : game.getHome().getStored()) {
            items.add(new MenuItem(label("Home", robot), () -> chooseStoredRobot(robot), delta -> repair(robot, delta)));
        }
        items.add(MenuItem.button("Retour", onClose));
        return items;
    }

    @Override
    protected void onBack() {
        onClose.run();
    }

    @Override
    protected double itemsTop() {
        return 250;
    }

    @Override
    protected double itemHeight() {
        return 40;
    }

    @Override
    protected String footer() {
        return "Entrée : échanger  ·  Droite : utiliser une pièce  ·  Échap : retour";
    }

    private String label(String place, Robot robot) {
        String state = robot.isUnderRepair()
                ? "réparation : " + robot.getRepairVictoriesLeft() + " vict."
                : robot.getHp() + "/" + robot.getMaxHp() + " PV";
        String mark = robot == leaving ? ">> " : "";
        return mark + place + " · " + robot.getName() + " Nv " + robot.getLevel() + " · " + state;
    }

    private void chooseTeamRobot(Robot robot) {
        leaving = robot == leaving ? null : robot;
        feedback = leaving == null ? "" : "Choisis le robot rangé qui remplace " + robot.getName() + ".";
    }

    private void chooseStoredRobot(Robot robot) {
        if (leaving != null) {
            game.getHome().swap(game.getTeam(), leaving, robot);
            feedback = robot.getName() + " remplace " + leaving.getName() + " dans l'équipe.";
            leaving = null;
        } else if (game.getHome().sendToTeam(game.getTeam(), robot)) {
            feedback = robot.getName() + " rejoint l'équipe.";
        } else {
            feedback = "Équipe pleine : choisis d'abord le robot de l'équipe à remplacer.";
        }
    }

    private void repair(Robot robot, int delta) {
        if (delta <= 0) {
            return;
        }
        if (!robot.isUnderRepair()) {
            feedback = robot.getName() + " n'est pas en réparation.";
        } else if (game.getParts().repair(robot)) {
            feedback = robot.isUnderRepair()
                    ? "Une pièce dépensée : encore " + robot.getRepairVictoriesLeft() + " victoire(s) pour "
                            + robot.getName() + "."
                    : robot.getName() + " est réparé et prêt à se battre !";
        } else {
            feedback = "Plus de pièce : achève des robots pour en récupérer.";
        }
    }
}
