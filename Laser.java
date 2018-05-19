import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
public class Laser extends GameObject{
	private static final int WIDTH = 10;
	private static final int HEIGHT = 10;
	private Color colour;
	private int ySpeed;
	public Laser(Point position, Color colour) {
		super(null, position, new Dimension(WIDTH, HEIGHT));
		this.colour = colour;
	}
	public void setyspeed(int ySpeed) {
		this.ySpeed = ySpeed;
	}
	@Override
	public void move(int x, int y) {
		getPosition().y -= ySpeed;
		getRectangle().setRect(getPosition().x, getPosition().y, WIDTH, HEIGHT);
	}
	@Override
	public void draw(Graphics g) {
		g.setColor(colour);
		g.fillRect(getPosition().x-5, getPosition().y, WIDTH, HEIGHT);		
	}
}
