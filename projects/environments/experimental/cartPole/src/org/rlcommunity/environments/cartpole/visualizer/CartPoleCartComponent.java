package org.rlcommunity.environments.cartpole.visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import java.awt.geom.Ellipse2D;
import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.VizComponent;

public class CartPoleCartComponent implements VizComponent {

    private CartPoleVisualizer cartVis = null;
    private double poleLength = .3d; //30% of the screen long

    public CartPoleCartComponent(CartPoleVisualizer cartpoleVisualizer) {
        cartVis = cartpoleVisualizer;

    }

    public void render(Graphics2D g) {
        //SET COLOR
        g.setColor(Color.BLACK);

        AffineTransform saveAT = g.getTransform();
        double scale = .0001;
        double inverseScale = 1.0d / scale;
        double eightyPercent = .8d * inverseScale;
        double tenPercent = .1d * inverseScale;
        double fivePercent = .05d * inverseScale;
        double twentyPercent = .2d * inverseScale;
        g.scale(scale, scale);
        int transX = (int) (UtilityShop.normalizeValue(cartVis.currentXPos(), cartVis.getLeftCartBound(), cartVis.getRightCartBound()) * (eightyPercent) + tenPercent);
        int transY = (int) eightyPercent;
        
        Rectangle carRect=new Rectangle((int) (transX - tenPercent), transY, (int) twentyPercent, (int) fivePercent);
        g.fill(carRect);
        drawWheels(g,carRect);
        g.setColor(Color.BLACK);
        int x2 = transX + (int) (inverseScale * poleLength * Math.cos(cartVis.getAngle()));
        int y2 = transY + (int) (inverseScale * poleLength * Math.sin(cartVis.getAngle()));
        Stroke stroke = new BasicStroke(20.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(stroke);
        g.drawLine(transX, transY, x2, y2);
        g.setTransform(saveAT);
    }

    public boolean update() {
        return cartVis.updateCart();
    }

    private void drawWheels(Graphics2D g,Rectangle carRect) {
        g.setColor(Color.red);
        
        double carMidY=carRect.getCenterY();
        double carX1=carRect.getMinX()+carRect.width/4.0d;
        double carX2=carRect.getMaxX()-carRect.width/4.0d;
        double wheelRad=carRect.height;
        g.fillOval((int)(carX1-wheelRad/2.0d),(int)(carMidY),(int)wheelRad,(int)wheelRad);
        g.fillOval((int)(carX2-wheelRad/2.0d),(int)(carMidY),(int)wheelRad,(int)wheelRad);
    }
}
