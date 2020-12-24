package com.mljoke.rajon;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mljoke.rajon.java.Core;
import com.mljoke.rajon.screens.widgets.*;

import static com.mljoke.rajon.java.Core.*;

public class GameUI {
    private Core game;
    public Stage stage;
    public HealthWidget healthWidget;
    private ScoreWidget scoreWidget;
    private PauseWidget pauseWidget;
    private CrosshairWidget crosshairWidget;
    public GameOverWidget gameOverWidget;

    public GameUI(Core game) {
        this.game = game;
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        setWidgets();
        configureWidgets();
    }

    public void setWidgets() {
        healthWidget = new HealthWidget();
        scoreWidget = new ScoreWidget();
        pauseWidget = new PauseWidget(game, stage);
        crosshairWidget = new CrosshairWidget();
        gameOverWidget = new GameOverWidget(game, stage);
    }

    public void configureWidgets() {
        healthWidget.setSize(140, 25);
        healthWidget.setPosition(VIRTUAL_WIDTH / 2 - healthWidget.getWidth() / 2, 0);
        scoreWidget.setSize(140, 25);
        scoreWidget.setPosition(0, VIRTUAL_HEIGHT - scoreWidget.getHeight()); pauseWidget.setSize(64, 64);
        pauseWidget.setPosition(VIRTUAL_WIDTH - pauseWidget.getWidth(), VIRTUAL_HEIGHT - pauseWidget.getHeight());
        gameOverWidget.setSize(280, 100);
        gameOverWidget.setPosition(VIRTUAL_WIDTH / 2 - 280 / 2, VIRTUAL_HEIGHT / 2);
        crosshairWidget.setPosition(VIRTUAL_WIDTH / 2 - 16, VIRTUAL_HEIGHT / 2 - 16);
        crosshairWidget.setSize(32, 32);
        stage.addActor(healthWidget);
        stage.addActor(scoreWidget);
        stage.addActor(crosshairWidget);
        stage.setKeyboardFocus(pauseWidget);
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void dispose() {
        stage.dispose();
    }

}
