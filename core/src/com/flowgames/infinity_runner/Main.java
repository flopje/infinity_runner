package com.flowgames.infinity_runner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


public class Main extends ApplicationAdapter {
	private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    private CameraInputController cameraInputController;
    private AssetManager assetManager;
    private Array<ModelInstance> modelInstances = new Array<ModelInstance>();

    private ModelInstance skyDome;
    private ModelInstance player;
    private Vector3 playerCoords;

    private Boolean loading;
	
	@Override
	public void create () {
        environment = new Environment();
        modelBatch = new ModelBatch();

        playerCoords  = new Vector3();

        setCamera();
        setupLights();

        cameraInputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraInputController);

        assetManager = new AssetManager();
        assetManager.load("plane_box.g3db", Model.class);
        assetManager.load("dome.g3db", Model.class);

        loading = true;
	}

	@Override
	public void render () {

        if (loading && assetManager.update()) {
            doneLoading();
            playerCoords = player.transform.getTranslation(playerCoords);
        }

        if (!loading) {
            updatePlayer();
            playerCoords = player.transform.getTranslation(playerCoords);
            camera.position.set(playerCoords.x, 10f, playerCoords.z+10f);
            camera.update();
        }

        cameraInputController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(modelInstances, environment);
        if (skyDome != null) {
            modelBatch.render(skyDome);
        }
        modelBatch.end();
	}

    private void updatePlayer() {
        player.transform.translate(1f, 0f,0f);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        modelInstances.clear();
        assetManager.dispose();
    }

    /**
     * Setup Simple camera with a FOV of 67, and an aspect ratio of the width and height of the screen.
     * Then move it 10 units back, 10 units up, and 10 units to the right. Since the Z-axis is
     * pointing to the user, a positive value can be used to move the camera back.
     * Then the camera is focused at origin (0,0,0) & near and far values are set so we will
     * see our object
     */
    private void setCamera() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(20f, 10f, 0f);
        camera.lookAt(playerCoords.x, 10f, playerCoords.z + 10f);
        camera.near = 1f;
        camera.far = 500f;
        camera.update();
    }

    /**
     * Setup some simple directional and ambient lights.
     */
    private void setupLights() {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    /**
     * Called when AssetManager is done loading the models
     */
    private void doneLoading() {
        Model plane = assetManager.get("plane_box.g3db", Model.class);

        ModelInstance modelInstance = new ModelInstance(plane);
        modelInstances.add(modelInstance);

        ModelInstance modelInstance2 = new ModelInstance(plane);
        modelInstance2.transform.setTranslation(0, 0, -30f);

        modelInstances.add(modelInstance2);

        skyDome =  new ModelInstance(assetManager.get("dome.g3db", Model.class));

        createSimpleGroundPlane();

        playerPlaceHolder();

        loading = false;
    }

    /**
     * Create a simple ground plane, covering the black void underneath the dome.
     */
    private void createSimpleGroundPlane() {
        ModelBuilder builder = new ModelBuilder();
        Model model = builder.createBox(600f, 1f, 600f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelInstances.add(new ModelInstance(model));

    }

    private void playerPlaceHolder() {
        ModelBuilder builder = new ModelBuilder();
        Model model = builder.createBox(5, 18f, 3f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        player = new ModelInstance(model);
        modelInstances.add(player);
    }

}
