
package GranTurismo;

import java.text.DecimalFormat;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class RacingBuild extends PApplet
{
	private boolean upPress, downPress, leftPress, rightPress;
	private float velocityX, velocityY, velocityAngled, accelerationAngled;
	private float angle, cameraAngle, deltaAngle, x, y, z, deltaTime, nextTimeInterval, temp, speedFOV, bodyRollAngle, bodyRollAngleDelta;
	private PImage trackTexture, textureBuilding, skybox, megaTrackOne, megaTrackTwo;
	private PShape globe;
	
	public static void main(String[]args)
	{
		PApplet.main("GranTurismo.RacingBuild");
	}
	
	public void settings()
	{
		size(1500, 700, P3D);
	}
	
	public void setup()
	{
		x = 480;
		y = 70;
		z = -6;
		velocityAngled = 0;
		accelerationAngled = 1;
		deltaAngle = (float) 0.02;
		nextTimeInterval = deltaTime = 500;
		trackTexture = loadImage("images/largetrack.png");
		textureBuilding = loadImage("images/building.png");
		skybox = loadImage("images/skybox.png");
		megaTrackOne = loadImage("images/megatrack01.png");
		megaTrackTwo = loadImage("images/megatrack02.png");
		temp = 0;
		speedFOV = 4;
		cameraAngle = angle;
		bodyRollAngle = 0;
		bodyRollAngleDelta = (float) 0.03;
		
		globe = createShape(SPHERE, 30000);
		globe.setTexture(skybox);
		globe.setStroke(false);

		upPress = downPress = leftPress = rightPress = false;
	}
	
	public void draw()
	{
		//asset loading
		background(200, 220, 255);
		drawCar(156, 6, 51, x, y, 20);
		
		beginCamera();
		float cameraZ = (float) (height/2.0) / (float) Math.tan(PI*60.0/360.0);
		perspective((float) (Math.PI / speedFOV), (float) width / (float) height, cameraZ / 1000, cameraZ * 1000);
		if (velocityAngled < 0)
			camera((float) (x + Math.cos(cameraAngle) * 200), (float) (y + Math.sin(cameraAngle) * 200), z + 80, x, y, z + 40, 0, 0, -1);
		else
			camera((float) (x - Math.cos(cameraAngle) * 200), (float) (y - Math.sin(cameraAngle) * 200), z + 80, x, y, z + 40, 0, 0, -1);
		endCamera();
		
//		drawBuildingGeneric(701, 1068, 150, 300);
//		drawBuildingGeneric(24, 471, 100, 150);
//		drawBuildingGeneric(954, 120, 80, 150);
//		drawBuildingGeneric(156, 147, 120, 120);
		
		//racetrack
		pushMatrix();
		textureMode(IMAGE);
		beginShape();
		texture(megaTrackOne);
		vertex(0, 0, 10, 0, 0);
		vertex(1220, 0, 10, 1200, 0);
		vertex(1220, 1390, 10, 1200, 1370);
		vertex(0, 1390, 10, 0, 1370);
		endShape();
		
		beginShape();
		texture(megaTrackTwo);
		vertex(1220, 0, 10, 0, 0);
		vertex(2440, 0, 10, 1200, 0);
		vertex(2440, 1390, 10, 1200, 1370);
		vertex(1220, 1390, 10, 0, 1370);
		endShape();
		popMatrix();
		
		pushMatrix();
		translate(x, y, 0);
		rotateX((float) -Math.PI / 2);
		beginShape();
		globe.noStroke();
		shape(globe);
		endShape();
		popMatrix();
		
		
		drawUserInterface();
		
		
		//game mechanics
		handleKeyMovement();
		handlePhysics();
	}
	
	public void drawUserInterface()
	{
		pushMatrix();
		translate((float) (x), (float) (y), 0);
		rotateZ(cameraAngle);
		rotateX((float) (3 * Math.PI / 2));
		
		if (velocityAngled >= 0)
			rotateY((float) -Math.PI / 2);
		else
			rotateY((float) Math.PI / 2);
		hint(PApplet.DISABLE_DEPTH_TEST);
		fill(255, 240, 0);
		textMode(SHAPE);
		textSize(10);
		text(new DecimalFormat("#.#").format(Math.abs(velocityAngled) * 10 / 2) + " MPH", 0, 0);
		hint(PApplet.ENABLE_DEPTH_TEST);
		popMatrix();
		
	}
	
	public void drawBuildingGeneric(float x, float y, float width, float height)
	{
		pushMatrix(); //1000, 783
		textureMode(IMAGE);
		beginShape();
		fill(255, 0, 0);
		texture(textureBuilding);
		vertex(x, y, 0, 0, 0);
		vertex(x + width, y, 0, 470, 0);
		vertex(x + width, y, height, 470, 440);
		vertex(x, y, height, 0, 440);
		endShape();
		
		beginShape();
		texture(textureBuilding);
		vertex(x, y, 0, 0, 0);
		vertex(x, y + width, 0, 470, 0);
		vertex(x, y + width, height, 470, 440);
		vertex(x, y, height, 0, 440);
		endShape();
		
		beginShape();
		texture(textureBuilding);
		vertex(x, y + width, 0, 0, 0);
		vertex(x + width, y + width, 0, 470, 0);
		vertex(x + width, y + width, height, 470, 440);
		vertex(x, y + width, height, 0, 440);
		endShape();
		
		beginShape();
		texture(textureBuilding);
		vertex(x + width, y + width, 0, 0, 0);
		vertex(x + width, y + width, height, 470, 0);
		vertex(x + width, y, height, 470, 440);
		vertex(x + width, y, 0, 0, 440);
		endShape();
		popMatrix();
	}
	
	public void drawCar(int red, int green, int blue, float x, float y, float wideBody)
	{
		pushMatrix();
		translate(x, y, 20);
		rotateZ(angle);
		rotateX(bodyRollAngle);
		fill(red, green, blue);
		
		beginShape(); //bottom
		vertex(-50, -wideBody, z);
		vertex(-50, wideBody, z);
		vertex(50, wideBody, z);
		vertex(50, -wideBody, z);
		vertex(-50, -wideBody, z);
		vertex(-50, wideBody, z);
		endShape();
		
		beginShape(); //right side
		vertex(-50, wideBody, z);
		vertex(-40, wideBody, z + 10);
		vertex(-30, wideBody, z + 10);
		vertex(20, wideBody, z + 10);
		vertex(40, wideBody, z + 10);
		vertex(50, wideBody, z);
		endShape();
		
		beginShape(); //left side
		vertex(-50, -wideBody, z);
		vertex(-40, -wideBody, z + 10);
		vertex(-30, -wideBody, z + 10);
		vertex(20, -wideBody, z + 10);
		vertex(40, -wideBody, z + 10);
		vertex(50, -wideBody, z);
		endShape();
		
		beginShape(); //front
		vertex(50, -wideBody, z);
		vertex(40, -wideBody, z + 10);
		vertex(40, wideBody, z + 10);
		vertex(50, wideBody, z);
		endShape();
		
		beginShape(); //hood
		vertex(40, -wideBody, z + 10);
		vertex(20, -wideBody, z + 10);
		vertex(20, wideBody, z + 10);
		vertex(40, wideBody, z + 10);
		endShape();
		
		beginShape(); //roof
		vertex(10, -wideBody + 5, z + 20);
		vertex(-20, -wideBody + 5, z + 20);
		vertex(-20, wideBody - 5, z + 20);
		vertex(10, wideBody - 5, z + 20);
		vertex(10, -wideBody + 5, z + 20);
		endShape();
		
		beginShape(); //rear
		vertex(-40, -wideBody, z + 10);
		vertex(-50, -wideBody, z);
		vertex(-50, wideBody, z);
		vertex(-40, wideBody, z + 10);
		vertex(-40, -wideBody, z + 10);
		endShape();
		
		beginShape(); //trunk
		vertex(-30, -wideBody, z + 10);
		vertex(-40, -wideBody, z + 10);
		vertex(-40, wideBody, z + 10);
		vertex(-30, wideBody, z + 10);
		vertex(-30, -wideBody, z + 10);
		endShape();
		
		beginShape(); //front windshield
		fill(38, 134, 166);
		vertex(20, -wideBody, z + 10);
		vertex(10, -wideBody + 5, z + 20);
		vertex(10, wideBody - 5, z + 20);
		vertex(20, wideBody, z + 10);
		endShape();
		
		beginShape(); //rear windshield
		fill(38, 134, 166);
		vertex(-20, -wideBody + 5, z + 20);
		vertex(-30, -wideBody, z + 10);
		vertex(-30, wideBody, z + 10);
		vertex(-20, wideBody - 5, z + 20);
		endShape();
		
		beginShape(); //right windows
		fill(38, 134, 166);
		vertex(-20, wideBody - 5, z + 20);
		vertex(10, wideBody - 5, z + 20);
		vertex(20, wideBody, z + 10);
		vertex(-30, wideBody, z + 10);
		endShape();
		
		beginShape(); //left windows
		fill(38, 134, 166);
		vertex(-20, -wideBody + 5, z + 20);
		vertex(10, -wideBody + 5, z + 20);
		vertex(20, -wideBody, z + 10);
		vertex(-30, -wideBody, z + 10);
		endShape();
		popMatrix();
		
		pushMatrix();
		translate(x, y, 20);
		rotateZ(angle);
		// 100 * 40
		beginShape(); //front right wheel
		fill(0, 0, 0);
		vertex(24, wideBody, z + 4);
		vertex(24, wideBody, z - 4);
		vertex(32, wideBody, z - 4);
		vertex(32, wideBody, z + 4);
		endShape();

		beginShape(); //front left wheel
		fill(0, 0, 0);
		vertex(24, -wideBody, z + 4);
		vertex(24, -wideBody, z - 4);
		vertex(32, -wideBody, z - 4);
		vertex(32, -wideBody, z + 4);
		endShape();

		beginShape(); //rear right wheel
		fill(0, 0, 0);
		vertex(24, wideBody, z + 4);
		vertex(24, wideBody, z - 4);
		vertex(32, wideBody, z - 4);
		vertex(32, wideBody, z + 4);
		endShape();

		beginShape(); //rear left wheel
		fill(0, 0, 0);
		endShape();
		popMatrix();
	}
	
	public void createWheel(float x, float y, float rotation)
	{
		
	}
	
	public void handlePhysics()
	{		
		velocityX = (float) (velocityAngled * Math.cos(angle));
		velocityY = (float) (velocityAngled * Math.sin(angle));
		
		x += velocityX;
		y += velocityY;
		
		
		
		if (speedFOV <= 4 && !upPress)
			speedFOV += 0.01;
		
		if (!leftPress)
		{
			if (cameraAngle > angle)
			{
				cameraAngle -= deltaAngle / 2;
			}
			
			//
		}
		if (!rightPress)
		{
			if (cameraAngle < angle)
			{
				cameraAngle += deltaAngle / 2;
			}
			
			//
		}
		
		if (bodyRollAngle >= 0.01)
		{
			bodyRollAngle -= bodyRollAngleDelta / 2;
		}
		if (bodyRollAngle <= -0.01)
		{
			bodyRollAngle += bodyRollAngleDelta / 2;
		}
		if (!(leftPress || rightPress) && bodyRollAngle < 0.01 && bodyRollAngle > -0.01)
		{
			bodyRollAngle = 0;
		}
		
		
		if (millis() >= nextTimeInterval)
		{
			if ((velocityAngled > 0 && !upPress) || (velocityAngled < 0 && !downPress)) //(velocityAngled > 0 && !upPress) || (velocityX < 0 && !downPress)
				velocityAngled /= 1.15;
			
			if (!(upPress || downPress))
			{
				if ((velocityAngled <= 0.04 && velocityAngled >= -0.04))
					velocityAngled = 0;
				
				accelerationAngled = 0;
			}
			
			//debug
			System.out.println("v: " + velocityAngled);
			System.out.println("f: " + speedFOV);
			System.out.println("b: " + bodyRollAngle);
			System.out.println("a: " + accelerationAngled + "\n~~~");
			
			nextTimeInterval += deltaTime;
		}
	}
	
	public void handleKeyMovement()
	{
		if (upPress)
		{
			if (velocityAngled <= 6)
				velocityAngled += accelerationAngled;
			accelerationAngled = (float) 0.02;
			if (speedFOV >= 3)
				speedFOV -= 0.01;
		}
		
		if (downPress)
		{
			if (velocityAngled >= -2)
				velocityAngled -= accelerationAngled;
			accelerationAngled = (float) 0.02;
		}
		
		if (velocityAngled > 0)
		{
			if (leftPress)
			{
				angle -= deltaAngle;
				
				if (cameraAngle <= angle + (float) (Math.PI / 8))
				{
					cameraAngle -= deltaAngle / 2;
				}
				else
					cameraAngle -= deltaAngle;
				
				if (bodyRollAngle >= (-Math.PI / 24) * (velocityAngled / 4))
				{
					bodyRollAngle -= bodyRollAngleDelta;
				}
			}
			if (rightPress)
			{
				angle += deltaAngle;
				
				if (cameraAngle >= angle - (float) (Math.PI / 8))
				{
					cameraAngle += deltaAngle / 2;
				}
				else
					cameraAngle += deltaAngle;
				
				if (bodyRollAngle <= (Math.PI / 24) * (velocityAngled / 4))
				{
					bodyRollAngle += bodyRollAngleDelta;
				}
			}
		}
		else if (velocityAngled < 0)
		{
			if (leftPress)
			{
				angle += deltaAngle;
				
				if (cameraAngle <= angle + (float) (Math.PI / 8))
				{
					cameraAngle -= deltaAngle / 1.2;
				}
				else
					cameraAngle -= deltaAngle;
			}
			if (rightPress)
			{
				angle -= deltaAngle;
				
				if (cameraAngle >= angle - (float) (Math.PI / 8))
				{
					cameraAngle += deltaAngle / 1.2;
				}
				else
					cameraAngle += deltaAngle;
			}
		}
	}
	
	public void keyPressed()
	{
		if (key == CODED)
		{
			if (keyCode == UP)
				upPress = true;
			else if (keyCode == DOWN)
				downPress = true;
			else if (keyCode == LEFT)
				leftPress = true;
			else if (keyCode == RIGHT)
				rightPress = true;
		}
	}
	
	public void keyReleased()
	{
		if (key == CODED)
		{
			if (keyCode == UP)
				upPress = false;
			else if (keyCode == DOWN)
				downPress = false;
			else if (keyCode == LEFT)
				leftPress = false;
			else if (keyCode == RIGHT)
				rightPress = false;
		}
	}
	
}
