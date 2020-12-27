package Physics;

import Elements.GObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the force generators and the particles they apply to
 */
public class ForceRegistry {

    /**
     * Keeps track of one force generator and the obj
     *  it applies to.
     */
    protected class ForceRegistration {
        public final GObject obj;
        public final ForceGenerator forceGenerator ;
        ForceRegistration(GObject p, ForceGenerator fg) {
            obj = p ;
            forceGenerator = fg ;
        }
    }

    // Holds the list of registrations
    protected HashMap<GObject, ForceRegistration> registrations = new HashMap<>() ;

    /**
     * Register the given force to apply to the given obj
     */
    public void add(GObject p, ForceGenerator fg) {
        registrations.put(p, new ForceRegistration(p, fg)) ;
    }

    /**
     * Remove the given registered pair from the registry. If the
     * pair is not registered, this method will have no effect.
     */
    public void remove(GObject obj){
        registrations.remove(obj);
    }

    /**
     * Clear all registrations from the registry
     */
    public void clear() {
        registrations.clear() ;
    }

    /**
     * Calls all force generators to update the forces of their
     *  corresponding particles.
     */
    public void updateForces() {
        for(Map.Entry<GObject, ForceRegistration> entry : registrations.entrySet()){
            GObject p = entry.getKey();
            ForceRegistration fr = entry.getValue();
            fr.forceGenerator.updateForce(fr.obj) ;
        }
    }

    /**
     * Calls force generators to update the forces of a specific object
     */
    public void updateForces(GObject obj) {
        registrations.get(obj).forceGenerator.updateForce(obj);
    }
}
