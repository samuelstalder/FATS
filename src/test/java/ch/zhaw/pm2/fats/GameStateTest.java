package ch.zhaw.pm2.fats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class GameStateTest {

    private final Config.SoilType field = Config.SoilType.EARTH;
    private final ArrayList<String> playerNames = new ArrayList<>();
    private GameState gameState;
    private final String expectedNamePlayer1 = "Player1";
    private final String expectedNamePlayer2 = "Player2";

    @BeforeEach
    void setUp() {
        playerNames.add(expectedNamePlayer1);
        playerNames.add(expectedNamePlayer2);
        MockitoAnnotations.initMocks(this);
        gameState = new GameState(field, playerNames);
    }

    @Test
    void testSwitchPlayer() {
        //configuration player1
        String playerOneName = gameState.getCurrentPlayer().getName();

        //assert player1
        assertEquals(expectedNamePlayer1, playerOneName);

        //actualTest
        gameState.switchPlayerAndTank();

        //configuration player2
        String playerTwoName = gameState.getCurrentPlayer().getName();

        //assert player2
        assertEquals(expectedNamePlayer2, playerTwoName);
    }

    @Test
    void testGetPlayersAlive() {
        //configuration
        int playerNr1 = 0;
        Player playerOneAlive = new Player(expectedNamePlayer1, playerNr1);
        int playerNr2 = 1;
        Player playerTwoAlive = new Player(expectedNamePlayer2, playerNr2);
        List<Player> expectedPlayersAlive = Arrays.asList(playerOneAlive, playerTwoAlive);

        //actual test
        List<Player> actualPlayersAlive = gameState.getPlayersAlive();

        //assert
        assertIterableEquals(expectedPlayersAlive, actualPlayersAlive);
    }

    @Test
    void testGetWinner() {
        //configuration
        int playerNr1 = 0;
        Player expectedWinner = new Player(expectedNamePlayer1, playerNr1);
        gameState.getTanks().get(1).setDestroyed();
        ArrayList<Player> playersAlive = gameState.getPlayersAlive();

        //actual test
        Player actualWinner = gameState.getWinner(playersAlive);

        //assert
        assertEquals(expectedWinner, actualWinner);
    }

    @Test
    void testGetWinnerWithNoWinner() {
        ArrayList<Player> playersAlive = gameState.getPlayersAlive();
        assertEquals(2, playersAlive.size());

        //actual test
        Player winner = gameState.getWinner(playersAlive);

        //assert
        assertEquals(null, winner);
    }
}
