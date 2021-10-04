package ch.zhaw.pm2.fats;

import java.util.Objects;

/**
 * Represents the player
 */
public class Player {
    private final String name;
    private final int player;

    public Player(String name, Integer player) {
        this.name = name;
        this.player = player;
    }

    public int getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player1 = (Player) o;
        return getPlayer() == player1.getPlayer() &&
                Objects.equals(getName(), player1.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPlayer());
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", player=" + player +
                '}';
    }
}
