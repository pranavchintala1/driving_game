package racingGameRemake;

import processing.core.PApplet;
import processing.core.PImage;

import java.text.DecimalFormat;

/**
 * 
 * @author Pranav Chintala
 * updated as of: 1/22/2023
 * --potentially rewriting code to separate the car into a separate object file
 * --chunk rendering algorithm?
 * 
 * GOAL: remaking the original game I made for my cs final project
 */

public class Main extends PApplet {

	private float carX, carY, carAngle, carVelocity, 
		carAcceleration, deltaAngle, cameraDistance, cameraAngle, wheelAngle, 
		wheelRotationAngle, maxWheelAngle, maxCarVelocity, 
		curMaxAngle, carRollAngle, interfaceDist, groundClearance;
	private float r, g, b;
	private boolean leftPressed, rightPressed, upPressed, downPressed, turnPressed;
	private PImage trackTexture;
	
	public static void main(String[]args)
	{
		
		PApplet.main("racingGameRemake.Main");
	}
	
	public void settings()
	{
		size(1600, 900, P3D);

	}
	 
	public void setup()
	{
		carAngle = carVelocity = wheelAngle = 0;
		carX = 10500;
		carY = 1300;
		carAcceleration = (float) 0.2;
		cameraDistance = 300;
		cameraAngle = carAngle;
		deltaAngle = 1;
		maxWheelAngle = 40;
		maxCarVelocity = 50;
		interfaceDist = 180;
		groundClearance = 10;
		trackTexture = loadImage("images/megatrack.png");
		r = (float) (255 * Math.random());
		g = (float) (255 * Math.random());
		b = (float) (255 * Math.random());
	}
	
	public void draw()
	{
		background(255, 255, 255);
		handleControls();
		handlePhysics();
		handleCamera(); //camera lags behind the car slightly when rendered after the car (for some odd reason)
		carModel();
		
		pushMatrix();
		beginShape();
		textureMode(NORMAL);
		texture(trackTexture);
		vertex(0, 0, 0, 0, 0);
		vertex(16000, 0, 1, 0);
		vertex(16000, 8000, 1, 1);
		vertex(0, 8000, 0, 1);
		endShape(CLOSE);
		popMatrix();
		//contextual shapes dont have any more timing slots than normal instances

		userInterface(); //draws the UI over everything in the 3D space
		//debugging string for console output [>...]
		System.out.println("deltaAngle: " + deltaAngle + "\ncarvelocity: " + 
		carVelocity + "\ncurWheelAngle/curMaxAngle/maxAngle: " + wheelAngle + "/" + maxWheelAngle + 
		"/" + curMaxAngle + "\ncameraAngle: " + cameraAngle + " carAngle: " + carAngle +
		"\n------------------");
	}
	
	//<=======|Assets|=======>
	public void carModel()
	{
		/** CHASSIS **/
		pushMatrix();
		
		
		//base
		fill(r, g, b);
		translate(carX, carY, groundClearance);
		rotateZ(radians(carAngle));
		beginShape();
		vertex(75, 40, 0);
		vertex(75, -40, 0);
		vertex(-75, -40, 0);
		vertex(-75, 40, 0);
		endShape(CLOSE);
		
		//right side
		beginShape();
		vertex(-75, -40, 0);
		vertex(75, -40, 0);
		vertex(65, -40, 20);
		vertex(35, -40, 30);
		vertex(-75, -40, 30);
		endShape(CLOSE);
		
		//left side
		beginShape();
		vertex(-75, 40, 0);
		vertex(75, 40, 0);
		vertex(65, 40, 20);
		vertex(35, 40, 30);
		vertex(-75, 40, 30);
		endShape(CLOSE);
		
		//front fascia
		beginShape();
		vertex(75, 40, 0);
		vertex(75, -40, 0);
		vertex(65, -40, 20);
		vertex(65, 40, 20);
		endShape(CLOSE);
		
		//hood/bonnet
		beginShape();
		vertex(65, 40, 20);
		vertex(65, -40, 20);
		vertex(35, -40, 30);
		vertex(35, 40, 30);
		endShape(CLOSE);
		
		//roof
		beginShape();
		vertex(5, 30, 50);
		vertex(5, -30, 50);
		vertex(-40, -30, 50);
		vertex(-40, 30, 50);
		endShape(CLOSE);
		
		//trunk
		beginShape();
		vertex(-65, 40, 30);
		vertex(-75, 40, 30);
		vertex(-75, -40, 30);
		vertex(-65, -40, 30);
		endShape(CLOSE);
		
		//rear fascia
		beginShape();
		vertex(-75, 40, 30);
		vertex(-75, 40, 0);
		vertex(-75, -40, 0);
		vertex(-75, -40, 30);
		endShape(CLOSE);
		
		//left windows
		beginShape();
		fill(98, 180, 196);
		vertex(35, 40, 30);
		vertex(5, 30, 50);
		vertex(-40, 30, 50);
		vertex(-65, 40, 30);
		endShape(CLOSE);
		
		//right windows
		beginShape();
		vertex(35, -40, 30);
		vertex(5, -30, 50);
		vertex(-40, -30, 50);
		vertex(-65, -40, 30);
		endShape(CLOSE);

		//rear wind shield
		beginShape();
		vertex(-40, 30, 50);
		vertex(-65, 40, 30);
		vertex(-65, -40, 30);
		vertex(-40, -30, 50);
		endShape(CLOSE);
		
		//front wind shield
		beginShape();
		vertex(35, 40, 30);
		vertex(5, 30, 50);
		vertex(5, -30, 50);
		vertex(35, -40, 30);
		endShape(CLOSE);
		
		//WHEELS
		translate(50, 40, groundClearance / 2);
		rotateZ(radians(wheelAngle));
		rotateY(radians(wheelRotationAngle));
		fill(0, 255, 0);
		wheel();
		popMatrix();
		
		pushMatrix();
		fill(255, 0, 0);
		translate(carX, carY, 0);
		rotateZ(radians(carAngle));
		translate(50, -40, 15);
		rotateZ(radians(wheelAngle));
		rotateY(radians(wheelRotationAngle));
		wheel();
		popMatrix();
		
		pushMatrix();
		fill(0, 0, 255);
		translate(carX, carY, 0);
		rotateZ(radians(carAngle));
		translate(-50, 40, 15);
		rotateY(radians(wheelRotationAngle));
		
		wheel();
		translate(0, -80, 0);
		fill(255, 255, 0);
		wheel();
		
		popMatrix();
	}

	private int radius, width;
	public void wheel()
	{
		beginShape();
		radius = 15;
		width = 10;
		for (float angle = 0; angle <= (float) Math.PI * 2; angle += Math.PI / 8)
		{
			vertex(radius * cos(angle), -width / 2, radius * sin(angle));
		}
		endShape(CLOSE);
		beginShape(TRIANGLE_STRIP);
		for (float angle = 0; angle <= (float) Math.PI * 2 + (2 * Math.PI / 8); angle += Math.PI / 8)
		{
			vertex(radius * cos(angle), -width / 2
					, radius * sin(angle));
			vertex(radius * cos(angle), width / 2, radius * sin(angle));
		}
		endShape(CLOSE);
		beginShape();
		for (float angle = 0; angle <= (float) Math.PI * 2; angle += Math.PI / 8)
		{
			vertex(radius * cos(angle), width / 2, radius * sin(angle));
		}
		endShape(CLOSE);
	}
	
	public void userInterface()
	{	
		pushMatrix();
		
		translate(carX - interfaceDist * cos(radians(cameraAngle)), 
				carY - interfaceDist * sin(radians(cameraAngle)), 50);
		rotateZ(radians(cameraAngle + 90));
		rotateX((float) -Math.atan(150 / interfaceDist));
		
		hint(PApplet.DISABLE_DEPTH_TEST);
		fill(255, 240, 0);
		textMode(SHAPE);
		textSize(10);
		text(new DecimalFormat("#").format(Math.abs(carVelocity)) + " PPS", 0, 0);
		hint(PApplet.ENABLE_DEPTH_TEST);
		popMatrix();

	}
	//<=======|Physics|=======>
	float angleDiff;
	public void handleCamera()
	{	
		angleDiff = cameraAngle - carAngle;
		angleDiff *= 0.95;
		cameraAngle = carAngle + angleDiff;
		
		if (carVelocity < 0)
		{
			cameraDistance = -Math.abs(cameraDistance);
		}
		else
		{
			cameraDistance = Math.abs(cameraDistance);
		}
		
//		float cameraZ = (float) (height/2.0) / (float) Math.tan(PI*60.0/360.0);
//		perspective(radians(90 + carVelocity), (float) (width / height), cameraZ / 100, cameraZ * 100);
		camera(carX - (float) (cameraDistance * Math.cos(radians(cameraAngle))),
				carY - (float) (cameraDistance * Math.sin(radians(cameraAngle))),
				150, carX, carY, 60, 0, 0, -1);
	}
	
	public void handlePhysics()
	{
		carX += carVelocity * Math.cos(radians(carAngle));
		carY += carVelocity * Math.sin(radians(carAngle));
		
		turnPressed = leftPressed || rightPressed;
		curMaxAngle = maxWheelAngle * (1 - carVelocity / (maxCarVelocity * 2));
		deltaAngle = (float) (0.7 * Math.sin((1 / (150 / Math.PI) * carVelocity) + 0.3) - 1.4); //redo this
		wheelRotationAngle += carVelocity;
		
		if (carVelocity > 0) //car turns in the same arc when reversing
		{
			deltaAngle = Math.abs(deltaAngle);
		}
		else if (carVelocity < 0)
		{
			deltaAngle = -Math.abs(deltaAngle);
		}
		else
		{
			deltaAngle = 0;
		}
		
		if (!(upPressed || downPressed)) //car slows when throttle is not applied
		{
			if (carVelocity < -0.2)//gains velocity when negative
			{
				carVelocity += carAcceleration / 4;
			}
			else if (carVelocity > 0.2)//loses velocity when positive
			{
				carVelocity -= carAcceleration / 4;
			}
			else //when velocity is not in the stated range, the velocity is set to zero
			{
				carVelocity = 0;
			}
		}
		
		if (wheelAngle > curMaxAngle) //wheel angle decreases when the angle exceeds the current maximum possible angle, recenters when no steering input is given
		{
			wheelAngle = curMaxAngle;
		}
		else if (wheelAngle < -curMaxAngle)
		{
			wheelAngle = -curMaxAngle;
		}
		
	}
	
	//<=======|Controls|=======>
	public void handleControls()
	{
		if (leftPressed)
		{
			carAngle -= deltaAngle;
			wheelAngle -= 8;
			
		}
		else if (rightPressed)
		{
			carAngle += deltaAngle;
			wheelAngle += 8;
		}
		else
		{
			wheelAngle /= 1.5;
			if (Math.abs(wheelAngle) < 0.1)
				wheelAngle = 0;
		}
		
		if (carVelocity < maxCarVelocity && upPressed) //acceleration
		{
			carVelocity += carAcceleration;
		}
		else if (downPressed) //brakes/reverse
		{
			if (carVelocity >= 0)
			{
				carVelocity -= carAcceleration * 1.5;
			}
			else
			{
				if (carVelocity > -maxCarVelocity / 4)
					carVelocity -= carAcceleration / 2;
			}
		}
	}

	public void keyPressed()
	{
		if (key == CODED)
		{
			if (keyCode == UP)
				upPressed = true;
			else if (keyCode == DOWN)
				downPressed = true;
			else if (keyCode == LEFT)
				leftPressed = true;
			else if (keyCode == RIGHT)
				rightPressed = true;
		}
	}
	
	public void keyReleased()
	{
		if (key == CODED)
		{
			if (keyCode == UP)
				upPressed = false;
			else if (keyCode == DOWN)
				downPressed = false;
			else if (keyCode == LEFT)
				leftPressed = false;
			else if (keyCode == RIGHT)
				rightPressed = false;
		}
	}
}