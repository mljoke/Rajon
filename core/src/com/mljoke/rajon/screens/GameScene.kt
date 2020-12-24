package com.mljoke.rajon.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.*
import com.badlogic.gdx.Input.Keys.*
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.mljoke.rajon.Assets
import com.mljoke.rajon.Editor
import com.mljoke.rajon.Game
import com.mljoke.rajon.ItemButton
import com.mljoke.rajon.inventory.Craft
import com.mljoke.rajon.inventory.CraftActor
import com.mljoke.rajon.inventory.Inventory
import com.mljoke.rajon.inventory.InventoryActor
import com.mljoke.rajon.managers.EntityFactory
import com.mljoke.rajon.screens.widgets.EnergyWidget
import com.mljoke.rajon.screens.widgets.HealthWidget
import com.mljoke.rajon.systems.BulletSystem
import com.mljoke.rajon.systems.PlayerSystem
import com.mljoke.rajon.systems.RenderSystem
import kotlin.math.min


private val space_3px = 3f

private val decile_width = graphics.width * 0.05f

class GameScene() : Screen {
    var game = Game()
    var stage = Stage()
    var healthWidget = HealthWidget()
    var energyWidget = EnergyWidget()
    var radWidget = EnergyWidget()
    val dragAndDrop: DragAndDrop by lazy { DragAndDrop()}
    var inventoryActor = InventoryActor(Inventory(), dragAndDrop, Assets.skin)
    var craftActor = CraftActor(Craft(), dragAndDrop, Assets.skin)
    var invButton = ItemButton("Inventory")
    var craftButton = ItemButton("Craft")
    val ok = Button(Image(Texture(files.internal("ok.png"))), Assets.skin)
    var delay = DelayAction(1f)
    private val FoV = 67f
    private lateinit var camera: PerspectiveCamera
    private val engine by lazy { Engine() }
    val bulletSystem : BulletSystem by lazy { BulletSystem() }
    val modelBuilder = ModelBuilder()
    private val environment = Environment()
    private lateinit var modelBatch: ModelBatch
    lateinit var model: Model
    var debugDrawer: DebugDrawer
    val wallHorizontal = modelBuilder.createBox(40f, 20f, 1f,
        Material(ColorAttribute.createDiffuse(Color.WHITE),
            ColorAttribute.createSpecular(Color.RED), FloatAttribute
                .createShininess(16f)), (VertexAttributes.Usage.Position
                or VertexAttributes.Usage.Normal).toLong())!!
    val wallVertical = modelBuilder.createBox(1f, 20f, 40f,
        Material(ColorAttribute.createDiffuse(Color.GREEN),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(16f)), (
                VertexAttributes.Usage.Position or
                        VertexAttributes.Usage.Normal).toLong())
    val groundModel = modelBuilder.createBox(40f, 1f, 40f,
        Material(ColorAttribute.createDiffuse(Color.YELLOW),
            ColorAttribute.createSpecular(Color.BLUE),
            FloatAttribute.createShininess(16f)), (
                VertexAttributes.Usage.Position
                        or VertexAttributes.Usage.Normal).toLong())

    init {
        Bullet.init()
        initEnvironment()
        initModelBatch();
        initCamera();
        addSystems();
        addEntities();

        debugDrawer = DebugDrawer()
        debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE

        input.isCursorCatched = true
        input.inputProcessor = stage
        //set gebug on mouse position
        //stage.setDebugParentUnderMouse(true)

        healthWidget.apply {
            setSize(graphics.width / space_3px, decile_width)
            setPosition(10f, graphics.height - healthWidget.height)
            setValue(55f)
        }
        energyWidget.apply {
            setSize(graphics.width / space_3px, decile_width)
            setPosition(graphics.width - energyWidget.width, graphics.height - healthWidget.height)
            setValue(68f)
        }
        invButton.apply {
            setSize(decile_width, decile_width)
            setPosition(graphics.width - invButton.width * space_3px, invButton.height / 2)
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    toogleButton()
                    inventoryActor.isVisible = true
                }
            })
        }
        radWidget.apply {
            setSize(graphics.width / 4f, decile_width)
            setPosition(graphics.width / 2f - radWidget.width / 2f, graphics.height - healthWidget.height)
            setLabel("Rad")
            setValue(95f)
        }
        craftButton.apply {
            setSize(decile_width, decile_width)
            setPosition(graphics.width - invButton.width * 1.5f, invButton.height * 2)
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    toogleButton()
                    craftActor.isVisible = true
                }
            })
        }
        ok.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                toogleButton()
                craftActor.isVisible = false
            }
        })
        inventoryActor.apply {
            setSize(graphics.width.toFloat(), graphics.height - healthWidget.height)
            add(ok).right()
        }
        craftActor.apply {
            setSize(graphics.width.toFloat(), graphics.height - healthWidget.height)
            add(ok).right()
        }
        stage.apply {
            addActor(inventoryActor)
            addActor(craftActor)
            addActor(healthWidget)
            addActor(energyWidget)
            addActor(radWidget)
            addActor(invButton)
            addActor(craftButton)
        }
    }

    private fun initCamera() {
        PerspectiveCamera(FoV, graphics.width.toFloat(), graphics.height.toFloat()).run {
            camera = this
            position.set(30f, 40f, 30f)
            near = 1f
            far = 300f
            update()
        }
    }

    private fun initModelBatch() {
        modelBatch = ModelBatch()
    }

    private fun initEnvironment() {
        environment.run {
            set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
//            add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        }
    }

    private fun addEntities() {
        createGround()
        createPlayer(5f, 3f, 5f);
    }

    private fun createPlayer(x: Float, y: Float, z: Float) {
        engine.addEntity(EntityFactory.createPlayer(bulletSystem, x, y, z))
    }

    private fun createGround() {
        engine.addEntity(EntityFactory.createStaticEntity(groundModel, 0f, 0f, 0f))
        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0f, 10f, -20f))
        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0f, 10f, 20f))
        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, 20f, 10f, 0f))
        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, -20f, 10f, 0f))
    }

    private fun addSystems() {
        engine.addSystem(RenderSystem(modelBatch, environment))
        engine.addSystem(bulletSystem)
        bulletSystem.collisionWorld.debugDrawer = this.debugDrawer
        engine.addSystem(PlayerSystem(camera,this ))
    }

    private fun toogleButton() {
            craftButton.isVisible =  craftButton.isVisible.not()
            invButton.isVisible = invButton.isVisible.not()
    }

    override fun hide() {
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        game.render(delta)
        delay.act(delta)
        renderWorld(delta)
        stage.act(min(delta, 1 / 30f));
        stage.draw()

        if (input.isKeyJustPressed(I)) {
            toogleButton()
            inventoryActor.isVisible = inventoryActor.isVisible.not()
        }
        if (input.isKeyJustPressed(C)) {
            toogleButton()
            craftActor.isVisible = craftActor.isVisible.not()
        }
        if (input.isKeyJustPressed(F)) {
            with(graphics) {
                if (isFullscreen)
                    setWindowedMode(width, height)
                else
                    setFullscreenMode(displayMode)
            }
        }
        if (input.isKeyJustPressed(ESCAPE)) app.exit()
    }

    private fun renderWorld(delta: Float) {
        modelBatch.begin(camera)
        engine.update(delta)
        modelBatch.end()
        if (debug) {
            debugDrawer.begin(camera);
            bulletSystem.collisionWorld.debugDrawWorld()
            debugDrawer.end()
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        game.resize(width, height)
        stage.viewport.update(width, height, true)

    }

    override fun dispose() {
        bulletSystem.dispose()
        wallHorizontal.dispose()
        wallVertical.dispose()
        groundModel.dispose()
        game.dispose()
        stage.dispose()
        modelBatch.dispose()

    }

    companion object {
        var debug = true
    }
}