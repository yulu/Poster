package com.research.poster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import rajawali.Object3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.VideoTexture;
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.parser.LoaderOBJ;
import rajawali.parser.LoaderSTL;
import rajawali.parser.ParsingException;
import rajawali.primitives.Plane;
import rajawali.vuforia.RajawaliVuforiaRenderer;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Target:
 * arvk - video
 * piano - model
 * wearable - video
 * layout - model
 * assembly - model
 * robot - model
 * @author yulu
 *
 */

public class MyRenderer extends RajawaliVuforiaRenderer {
	//hash map to relate target name to object
	private Map<String, Object3D> targetMap = new HashMap<String, Object3D>();
	
	//lighting
	private DirectionalLight mLight;
	
	//two player for two video
	private MediaPlayer mMediaPlayer;
	private MediaPlayer mMediaPlayer2;
	private VideoTexture mVideoTexture;
	private VideoTexture mVideoTexture2;

	//listener to notify ui update
	private List<InitListener> mListeners;
	
	//init listener for ui update
	public interface InitListener{
		public void onInitComplete();
	}
	
	public void registerListener(InitListener l){
		if(mListeners == null)
			mListeners = new ArrayList<InitListener>();
		
		mListeners.add(l);
	}

	public MyRenderer(Context context) {
		super(context);
		
		mMediaPlayer = MediaPlayer.create(getContext(),
				R.raw.assis);	
		mMediaPlayer.setLooping(true);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		mMediaPlayer2 = MediaPlayer.create(getContext(),
				R.raw.test);	
		mMediaPlayer2.setLooping(true);
		mMediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);

	}
	
	protected void initScene(){
		
		//Setup lighting
		mLight = new DirectionalLight(0.0f, 1.0f, -1.0f); // set the direction
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(1);
		getCurrentScene().addLight(mLight);
		
		//Setup model parser
		LoaderSTL pianoParser = new LoaderSTL(mContext.getResources(),  R.raw.piano_stl);
		LoaderOBJ layoutParser = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.teapot_obj);
		
			
		try {
			//load ARVK-------------------------------------------------------------
			mVideoTexture = new VideoTexture("arvk", mMediaPlayer);
			Material material = new Material();
			material.setColorInfluence(0);
			material.addTexture(mVideoTexture);
			
			Object3D rObj = new Object3D();
			rObj = new Plane(600, 400, 1, 1, Axis.Y);
			rObj.setMaterial(material);
			rObj.setDoubleSided(true);
			
			rObj.setVisible(false);
			
			targetMap.put("arvk", rObj);
			addChild(rObj);
			
			//load Wearable-------------------------------------------------------------
			mVideoTexture2 = new VideoTexture("wearable", mMediaPlayer2);
			Material material2 = new Material();
			material2.setColorInfluence(0);
			material2.addTexture(mVideoTexture2);
			
			Object3D rObj2 = new Object3D();
			rObj2 = new Plane(400, 320, 1, 1, Axis.Y);
			rObj2.setMaterial(material2);
			rObj2.setDoubleSided(true);
			
			rObj2.setVisible(false);
			
			targetMap.put("wearable", rObj2);
			addChild(rObj2);
			
			//load PIANO-------------------------------------------------------------
			pianoParser.parse();
			Object3D pObj = new Object3D();
			pObj = pianoParser.getParsedObject();
			pObj.setDoubleSided(true);	
			
			Material m = new Material();
			m.enableLighting(true);
			m.setDiffuseMethod(new DiffuseMethod.Lambert());
			m.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 90));
			
			pObj.setMaterial(m);
			pObj.setColor(0xfffefe);
			pObj.setVisible(false);
			
			targetMap.put("piano", pObj);
			addChild(pObj);
			
			//load LAYOUT----------------------------------------------------------
			layoutParser.parse();
			Object3D lObj = new Object3D();
			//mObject = stlParser.getParsedObject();
			lObj = layoutParser.getParsedObject();
			lObj.setScale(200);
			lObj.setDoubleSided(true);		
			
			lObj.setMaterial(m);
			lObj.setColor(0xffffff);
			lObj.setVisible(false);
			
			targetMap.put("layout", lObj);
			addChild(lObj);
			
			//TODO: for test only, later will change to the particular model for each target
			targetMap.put("robot", lObj);
			targetMap.put("assembly", lObj);
			
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (TextureException e) {
			e.printStackTrace();
		} 
		
		//Notify Observers the init completion
		if(mListeners != null){
			for(InitListener l : mListeners){
				l.onInitComplete();
			}
		}
		
		//start and loop the video
		mMediaPlayer.start();
		mMediaPlayer2.start();

		
	}

	@Override
	protected void foundFrameMarker(int markerId, Vector3 position,
			Quaternion orientation) {

	}

	@Override
	protected void foundImageMarker(String trackableName, Vector3 position,
			Quaternion orientation) {
		
		if(targetMap.containsKey(trackableName)){
			
			Object3D renderObj = targetMap.get(trackableName);
			renderObj.setVisible(true);
			renderObj.setPosition(position);
			renderObj.setOrientation(orientation);
		}
		
	}

	@Override
	public void noFrameMarkersFound() {

		
	}
	
	public void onDrawFrame(GL10 glUnused) {
			
		for(Object3D b : targetMap.values()){
			b.setVisible(false);
		}
		
		//update video texture
		mVideoTexture.update();
		mVideoTexture2.update();
		
		super.onDrawFrame(glUnused);
	}


}
