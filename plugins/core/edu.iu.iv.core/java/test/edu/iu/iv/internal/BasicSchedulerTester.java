/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 30, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import junit.framework.TestCase;
import edu.iu.iv.common.configuration.BasicConfiguration;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.SchedulerListener;
import edu.iu.iv.core.algorithm.Algorithm;

/**
 *
 * @author Team IVC
 */
public class BasicSchedulerTester extends TestCase {
    
    private BasicScheduler scheduler;
    private int algorithmExecuted;
    private boolean blocked;
    private boolean error;
    private DummyAlgorithm algorithm;
    
    public void setUp(){
        try {
            IVC.setDelegate(new AbstractIVCDelegate(new BasicConfiguration(new File("config.xml"),"")) {

                public String getPluginPath(String pluginID) {
                    return "";
                }

                public String getPluginPath(Object plugin) {
                    return "";
                }

                public String getDefaultDataFolder() {
                    return "";
                }

                public String getTemporaryFilesFolder() {
                    return "";
                }});
            
           
        } catch (IOException e) {
            assertTrue(false);
        }
        
        IVC.getDelegate().setDefaultSettings();
        
        scheduler = new BasicScheduler();
        algorithmExecuted = 0;
        blocked = false;
        error = false;
        algorithm = new DummyAlgorithm();
    }

    public void testIsRunning() {
        assertFalse(scheduler.isRunning());
        blocked = true;
        scheduler.schedule(algorithm);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {}
        assertTrue(scheduler.isRunning());
        blocked = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        assertFalse(scheduler.isRunning());        
    }

    public void testIsEmpty() {
        assertTrue(scheduler.isEmpty());
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.roll(Calendar.DAY_OF_MONTH, true);
        scheduler.schedule(algorithm, tomorrow);
        assertFalse(scheduler.isEmpty());
        scheduler.unschedule(algorithm);
        assertTrue(scheduler.isEmpty());
    }


    public void testSchedulerFunctions() {
        DummySchedulerListener listener = new DummySchedulerListener();
        scheduler.addSchedulerListener(listener);
    
        Calendar c = Calendar.getInstance();
        c.roll(Calendar.DAY_OF_MONTH, true);
        scheduler.schedule(algorithm, c);                
        assertEquals(listener.error, 0);
        assertEquals(listener.finished, 0);
        assertEquals(listener.movedDown, 0);
        assertEquals(listener.movedToRunningQueue, 0);
        assertEquals(listener.movedUp, 0);
        assertEquals(listener.scheduledAtTime, 1);
        assertEquals(listener.scheduledNow, 0);
        assertEquals(listener.started, 0);
        listener.reset();       
        
        scheduler.unschedule(algorithm);
        scheduler.block(algorithm);
        scheduler.schedule(algorithm);
        assertEquals(listener.error, 0);
        assertEquals(listener.finished, 0);
        assertEquals(listener.movedDown, 0);
        assertEquals(listener.movedToRunningQueue, 0);
        assertEquals(listener.movedUp, 0);
        assertEquals(listener.scheduledAtTime, 1);
        assertEquals(listener.scheduledNow, 0);
        assertEquals(listener.started, 0);
        listener.reset();
        
        scheduler.unblock(algorithm);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        assertEquals(listener.error, 0);
        assertEquals(listener.finished, 1);
        assertEquals(listener.movedDown, 0);
        assertEquals(listener.movedToRunningQueue, 0);
        assertEquals(listener.movedUp, 0);
        assertEquals(listener.scheduledAtTime, 0);
        assertEquals(listener.scheduledNow, 0);
        assertEquals(listener.started, 1);
        listener.reset();
    
        scheduler.schedule(algorithm);
        error = true;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        assertEquals(listener.error, 1);
        assertEquals(listener.finished, 0);
        assertEquals(listener.movedDown, 0);
        assertEquals(listener.movedToRunningQueue, 0);
        assertEquals(listener.movedUp, 0);
        assertEquals(listener.scheduledAtTime, 0);
        assertEquals(listener.scheduledNow, 1);
        assertEquals(listener.started, 1);
        listener.reset();
        error = false;
                
        Algorithm algorithm2 = new DummyAlgorithm();
        scheduler.block(algorithm);
        scheduler.block(algorithm2);
        scheduler.runNow(algorithm);
        scheduler.schedule(algorithm2);
        scheduler.moveUp(algorithm2);
        assertEquals(listener.error, 0);
        assertEquals(listener.finished, 0);
        assertEquals(listener.movedDown, 0);
        assertEquals(listener.movedToRunningQueue, 0);
        assertEquals(listener.movedUp, 1);
        assertEquals(listener.scheduledAtTime, 0);
        assertEquals(listener.scheduledNow, 2);
        assertEquals(listener.started, 0);
        listener.reset();
        
        scheduler.moveDown(algorithm2);
        assertEquals(listener.error, 0);
        assertEquals(listener.finished, 0);
        assertEquals(listener.movedDown, 1);
        assertEquals(listener.movedToRunningQueue, 0);
        assertEquals(listener.movedUp, 0);
        assertEquals(listener.scheduledAtTime, 0);
        assertEquals(listener.scheduledNow, 0);
        assertEquals(listener.started, 0);
        listener.reset();
        
        blocked = true;
        scheduler.unschedule(algorithm);
        scheduler.unblock(algorithm);
        scheduler.unschedule(algorithm2);
        c = Calendar.getInstance();
        c.roll(Calendar.MILLISECOND, 50);
        scheduler.schedule(algorithm, c);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {}
        assertEquals(listener.error, 0);
        assertEquals(listener.finished, 0);
        assertEquals(listener.movedDown, 0);
        assertEquals(listener.movedToRunningQueue, 1);
        assertEquals(listener.movedUp, 0);
        assertEquals(listener.scheduledAtTime, 1);
        assertEquals(listener.scheduledNow, 0);
        assertEquals(listener.started, 1);
    }

    
    
    private class DummyAlgorithm implements Algorithm {

        public boolean execute() {
            while(blocked);
            if(error)
                throw new RuntimeException("aaah");
            else
                algorithmExecuted++;
            return true;
        }

        public PropertyMap getProperties() {return null;}
        public ParameterMap getParameters() {return null;}
    }

    private class DummySchedulerListener implements SchedulerListener {
        int movedToRunningQueue = 0;
        int scheduledAtTime = 0;
        int scheduledNow = 0;
        int started = 0;
        int finished = 0;
        int error = 0;
        int movedUp = 0;
        int movedDown = 0;
        
        public void reset(){
            movedToRunningQueue = 0;
            scheduledAtTime = 0;
            scheduledNow = 0;
            started = 0;
            finished = 0;
            error = 0;
            movedUp = 0;
            movedDown = 0;    
        }

        public void algorithmMovedToRunningQueue(Algorithm algorithm, int index) {
            movedToRunningQueue++;
        }

        public void algorithmScheduled(Algorithm algorithm, Calendar time, int index) {
            scheduledNow++;
        }

        public void algorithmScheduled(Algorithm algorithm, Calendar time) {
            scheduledAtTime++;
        }

        public void algorithmStarted(Algorithm algorithm) {
            started++;
        }

        public void algorithmFinished(Algorithm algorithm) {
            finished++;
        }

        public void algorithmError(Algorithm algorithm) {
            error++;
        }

        public void algorithmMovedUpInRunningQueue(Algorithm algorithm) {
            movedUp++;
        }

        public void algorithmMovedDownInRunningQueue(Algorithm algorithm) {
            movedDown++;
        }               
    }
}
